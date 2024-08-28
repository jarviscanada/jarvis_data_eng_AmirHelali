package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.stockquote.dao.Quote;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteDao;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteHttpHelper;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import static org.junit.Assert.*;

public class QuoteService_IntTest {
    private Connection connection;
    private QuoteDao quoteDao;
    private QuoteHttpHelper httpHelper;
    private QuoteService quoteService;
    private OkHttpClient client;
    private String apiKey;

    @Before
    public void setUp() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/stock_quote";
        String user = System.getenv("USER");
        String password = System.getenv("PASSWORD");
        apiKey = System.getenv("API");
        connection = DriverManager.getConnection(url, user, password);

        quoteDao = new QuoteDao(connection);
        client = new OkHttpClient();
        httpHelper = new QuoteHttpHelper(apiKey, client);
        quoteService = new QuoteService(quoteDao, httpHelper);
    }

    @After
    public void cleanUp() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void testFetchQuoteDataFromAPI_validTicker() {
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("AAPL");

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getTicker());

        Optional<Quote> savedQuote = quoteDao.findById("AAPL");
        assertTrue(savedQuote.isPresent());
        assertEquals("AAPL", savedQuote.get().getTicker());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchQuoteDataFromAPI_invalidTicker() {
        quoteService.fetchQuoteDataFromAPI("INVALID");
    }

    @Test
    public void testFetchQuoteDataFromAPI_nullTicker() {
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(null);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFetchQuoteDataFromAPI_emptyTicker() {
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("  ");

        assertFalse(result.isPresent());
    }
}
