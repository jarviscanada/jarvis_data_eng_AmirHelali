package ca.jrvs.apps.jdbc.stockquote.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import ca.jrvs.apps.jdbc.sotckquote.dao.Quote;
import ca.jrvs.apps.jdbc.sotckquote.dao.QuoteDao;
import ca.jrvs.apps.jdbc.sotckquote.dao.QuoteHttpHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import java.util.Optional;

public class QuoteService_UnitTest {
    @Mock
    private QuoteDao quoteDao;

    @Mock
    private QuoteHttpHelper httpHelper;

    @InjectMocks
    private  QuoteService quoteService;

    private Quote validQuote;

    @Before
    public void setup(){
        MockitoAnnotations.openMocks(this);
        validQuote = new Quote();
        validQuote.setTicker("AAPL");
        validQuote.setPrice(150.00);
        validQuote.setVolume(10000);
    }

    @Test
    public void testFetchQuoteDataFromAPI_validTicker(){
        when(httpHelper.fetchQuoteInfo("AAPL")).thenReturn(validQuote);
        when(quoteDao.save(validQuote)).thenReturn(validQuote);

        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("AAPL");

        assertTrue(result.isPresent());
        assertEquals(validQuote, result.get());
        verify(quoteDao, times(1)).save(validQuote);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchQuoteDataFromAPI_invalidTicker(){
        when(httpHelper.fetchQuoteInfo(anyString())).thenThrow(new IllegalArgumentException());
        quoteService.fetchQuoteDataFromAPI("INVALID");
    }

    @Test
    public void testFetchQuoteDataFromAPI_nullTicker() {
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(null);

        assertFalse(result.isPresent());
        verify(quoteDao, never()).save(any());
    }

    @Test
    public void testFetchQuoteDataFromAPI_emptyTicker() {
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("  ");

        assertFalse(result.isPresent());
        verify(quoteDao, never()).save(any());
    }
}
