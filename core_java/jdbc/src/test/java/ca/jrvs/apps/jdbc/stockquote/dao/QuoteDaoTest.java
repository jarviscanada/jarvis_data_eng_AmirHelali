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

public class QuoteDaoTest {
    private Connection connection;
    private QuoteDao quoteDao;
    private Quote quote;
    private String password;
    private String user;

    @Before
    public void setUp() throws Exception{
        password = System.getenv("PASSWORD");
        user = System.getenv("USER");
        String url = "jdbc:postgresql://localhost:5432/stock_quote";
        connection = DriverManager.getConnection(url, user, password);
        quoteDao = new QuoteDao(connection);
        quote = new Quote();
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
    }

    @After
    public void cleanUp() throws Exception {
        quoteDao.deleteAll();
        connection.close();
    }

    @Test
    public void testSaveQuote() throws Exception{
        quoteDao.save(quote);
        Optional<Quote> retrievedQuote = quoteDao.findById("AAPL");
        assertTrue(retrievedQuote.isPresent());
        assertEquals("AAPL", retrievedQuote.get().getTicker());
    }

    @Test
    public void testFindById() throws Exception {
        quoteDao.save(quote);
        Optional<Quote> retrievedQuote = quoteDao.findById("AAPL");
        assertTrue(retrievedQuote.isPresent());
        assertEquals(145.20, retrievedQuote.get().getOpen(), 0.01);
        assertEquals(146.50, retrievedQuote.get().getPrice(), 0.01);
    }

    @Test
    public void testFindAllQuotes() throws Exception {
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
        quoteDao.save(quote);
        quoteDao.save(quote2);

        Iterable<Quote> quotes = quoteDao.findAll();
        List<Quote> quoteList = new ArrayList<>();
        quotes.forEach(quoteList::add);

        assertEquals(2, quoteList.size());
        assertTrue(quoteList.stream().anyMatch(q -> q.getTicker().equals("AAPL")));
        assertTrue(quoteList.stream().anyMatch(q -> q.getTicker().equals("GOOGL")));
    }

    @Test
    public void testDeleteById() throws Exception{
        quoteDao.save(quote);
        quoteDao.deleteById("AAPL");
        Optional<Quote> retrievedQuote = quoteDao.findById("AAPL");
        assertFalse(retrievedQuote.isPresent());
    }

    @Test
    public void testDeleteAll() throws Exception{
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

        quoteDao.save(quote);
        quoteDao.save(quote2);

        quoteDao.deleteAll();

        assertFalse(quoteDao.findById("AAPL").isPresent());
        assertFalse(quoteDao.findById("GOOGL").isPresent());
    }
}
