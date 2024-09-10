package ca.jrvs.apps.jdbc.stockquote.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.*;

public class PositionDaoTest {
    private Connection connection;
    private QuoteDao quoteDao;
    private PositionDao positionDao;
    private Position position;
    private String password;
    private String user;

    @Before
    public void setUp() throws Exception{
        password = System.getenv("PASSWORD");
        user = System.getenv("USER");
        String url = "jdbc:postgresql://localhost:5432/stock_quote";
        connection = DriverManager.getConnection(url, user, password);
        quoteDao = new QuoteDao(connection);
        positionDao = new PositionDao(connection);
        position = new Position();
        position.setTicker("AAPL");
        position.setNumOfShares(100);
        position.setValuePaid(15000.00);
    }

    @After
    public void cleanUp() throws Exception {
        positionDao.deleteAll();
        quoteDao.deleteAll();
        connection.close();
    }

    @Test
    public void testSavePosition() throws Exception {
        Quote quote = new Quote();
        quote.setTicker("AAPL");
        quote.setOpen(145.20);
        quote.setHigh(147.00);
        quote.setLow(144.50);
        quote.setPrice(146.50);
        quote.setVolume(1000000);
        quote.setLatestTradingDay(new Date());
        quote.setPreviousClose(145.00);
        quote.setChange(1.50);
        quote.setChangePercent("1.04%");
        quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
        quoteDao.save(quote);
        positionDao.save(position);
        Optional<Position> retrievedPosition = positionDao.findById("AAPL");
        assertTrue(retrievedPosition.isPresent());
        assertEquals("AAPL", retrievedPosition.get().getTicker());
    }

    @Test
    public void testFindById() throws Exception {
        Quote quote = new Quote();
        quote.setTicker("AAPL");
        quote.setOpen(145.20);
        quote.setHigh(147.00);
        quote.setLow(144.50);
        quote.setPrice(146.50);
        quote.setVolume(1000000);
        quote.setLatestTradingDay(new Date());
        quote.setPreviousClose(145.00);
        quote.setChange(1.50);
        quote.setChangePercent("1.04%");
        quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
        quoteDao.save(quote);
        positionDao.save(position);
        Optional<Position> retrievedPosition = positionDao.findById("AAPL");
        assertTrue(retrievedPosition.isPresent());
        assertEquals(100, retrievedPosition.get().getNumOfShares());
        assertEquals(15000.00, retrievedPosition.get().getValuePaid(), 0.01);
    }

    @Test
    public void testFindAllPositions() throws Exception{
        Quote quote = new Quote();
        quote.setTicker("AAPL");
        quote.setOpen(145.20);
        quote.setHigh(147.00);
        quote.setLow(144.50);
        quote.setPrice(146.50);
        quote.setVolume(1000000);
        quote.setLatestTradingDay(new Date());
        quote.setPreviousClose(145.00);
        quote.setChange(1.50);
        quote.setChangePercent("1.04%");
        quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
        quoteDao.save(quote);
        Quote quote2 = new Quote();
        quote2.setTicker("GOOGL");
        quote2.setOpen(2735.10);
        quote2.setHigh(2750.00);
        quote2.setLow(2720.00);
        quote2.setPrice(2740.00);
        quote2.setVolume(500000);
        quote2.setLatestTradingDay(new Date());
        quote2.setPreviousClose(2730.00);
        quote2.setChange(10.00);
        quote2.setChangePercent("0.37%");
        quote2.setTimestamp(new Timestamp(System.currentTimeMillis()));
        quoteDao.save(quote2);
        Position position2 = new Position();
        position2.setTicker("GOOGL");
        position2.setNumOfShares(50);
        position2.setValuePaid(80000.00);
        positionDao.save(position);
        positionDao.save(position2);

        Iterable<Position> positions = positionDao.findAll();
        List<Position> positionList = new ArrayList<>();
        positions.forEach(positionList::add);

        assertEquals(2, positionList.size());
        assertTrue(positionList.stream().anyMatch(p -> p.getTicker().equals("AAPL")));
        assertTrue(positionList.stream().anyMatch(p -> p.getTicker().equals("GOOGL")));
    }

    @Test
    public void testDeleteById() throws Exception {
        Quote quote = new Quote();
        quote.setTicker("AAPL");
        quote.setOpen(145.20);
        quote.setHigh(147.00);
        quote.setLow(144.50);
        quote.setPrice(146.50);
        quote.setVolume(1000000);
        quote.setLatestTradingDay(new Date());
        quote.setPreviousClose(145.00);
        quote.setChange(1.50);
        quote.setChangePercent("1.04%");
        quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
        quoteDao.save(quote);
        positionDao.save(position);
        positionDao.deleteById("AAPL");
        Optional<Position> retrievedPosition = positionDao.findById("AAPL");
        assertFalse(retrievedPosition.isPresent());
    }

    @Test
    public void testDeleteAll() throws Exception {
        Quote quote = new Quote();
        quote.setTicker("AAPL");
        quote.setOpen(145.20);
        quote.setHigh(147.00);
        quote.setLow(144.50);
        quote.setPrice(146.50);
        quote.setVolume(1000000);
        quote.setLatestTradingDay(new Date());
        quote.setPreviousClose(145.00);
        quote.setChange(1.50);
        quote.setChangePercent("1.04%");
        quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
        quoteDao.save(quote);
        Quote quote2 = new Quote();
        quote2.setTicker("GOOGL");
        quote2.setOpen(2735.10);
        quote2.setHigh(2750.00);
        quote2.setLow(2720.00);
        quote2.setPrice(2740.00);
        quote2.setVolume(500000);
        quote2.setLatestTradingDay(new Date());
        quote2.setPreviousClose(2730.00);
        quote2.setChange(10.00);
        quote2.setChangePercent("0.37%");
        quote2.setTimestamp(new Timestamp(System.currentTimeMillis()));
        quoteDao.save(quote2);
        Position position2 = new Position();
        position2.setTicker("GOOGL");
        position2.setNumOfShares(50);
        position2.setValuePaid(80000.00);
        positionDao.save(position);
        positionDao.save(position2);

        positionDao.deleteAll();

        assertFalse(positionDao.findById("AAPL").isPresent());
        assertFalse(positionDao.findById("GOOGL").isPresent());
    }
}
