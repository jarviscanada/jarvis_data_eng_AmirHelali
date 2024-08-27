package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.sotckquote.dao.Position;
import ca.jrvs.apps.jdbc.sotckquote.dao.PositionDao;
import ca.jrvs.apps.jdbc.sotckquote.dao.Quote;
import ca.jrvs.apps.jdbc.sotckquote.dao.QuoteDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PositionService_UnitTest {
    @Mock
    private PositionDao positionDao;

    @Mock
    private QuoteDao quoteDao;

    @InjectMocks
    private PositionService positionService;

    private Quote quote;
    private Position position;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        quote = new Quote();
        quote.setTicker("AAPL");
        quote.setVolume(100);
        quote.setPrice(150.00);

        position = new Position();
        position.setTicker("AAPL");
        position.setNumOfShares(20);
        position.setValuePaid(3000.00);
    }

    @Test
    public void testBuy_NewPosition() {
        when(quoteDao.findById("AAPL")).thenReturn(Optional.of(quote));
        when(positionDao.findById("AAPL")).thenReturn(Optional.empty());

        ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);
        when(positionDao.save(positionCaptor.capture())).thenReturn(position);

        Position result = positionService.buy("AAPL", 20, 150.00);

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(20, result.getNumOfShares());
        assertEquals(3000.00, result.getValuePaid(), 0.01);
        Position capturedPosition = positionCaptor.getValue();
        assertEquals("AAPL", capturedPosition.getTicker());
        assertEquals(20, capturedPosition.getNumOfShares());
        assertEquals(3000.00, capturedPosition.getValuePaid(), 0.01);
    }

    @Test
    public void testBuy_ExistingPosition() {
        when(quoteDao.findById("AAPL")).thenReturn(Optional.of(quote));
        when(positionDao.findById("AAPL")).thenReturn(Optional.of(position));

        ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);
        when(positionDao.save(positionCaptor.capture())).thenReturn(position);

        Position result = positionService.buy("AAPL", 20, 150.00);

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(40, result.getNumOfShares());
        assertEquals(6000.00, result.getValuePaid(), 0.01);
        Position capturedPosition = positionCaptor.getValue();
        assertEquals("AAPL", capturedPosition.getTicker());
        assertEquals(40, capturedPosition.getNumOfShares());
        assertEquals(6000.00, capturedPosition.getValuePaid(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuy_ExceedsVolume() {
        when(quoteDao.findById("AAPL")).thenReturn(Optional.of(quote));

        positionService.buy("AAPL", 200, 150.00);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuy_NoQuoteData() {
        when(quoteDao.findById("AAPL")).thenReturn(Optional.empty());

        positionService.buy("AAPL", 20, 150.00);

    }

    @Test
    public void testSell() {
        doNothing().when(positionDao).deleteById("AAPL");

        positionService.sell("AAPL");

        verify(positionDao).deleteById("AAPL");
    }
}
