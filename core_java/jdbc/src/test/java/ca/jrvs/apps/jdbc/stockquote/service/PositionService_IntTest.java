package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.stockquote.dao.Position;
import ca.jrvs.apps.jdbc.stockquote.dao.PositionDao;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.Assert.*;

public class PositionService_IntTest {
    private PositionService positionService;
    private PositionDao positionDao;
    private QuoteDao quoteDao;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC driver not found.", e);
        }

        String url = "jdbc:postgresql://localhost:5432/stock_quote";
        String user = System.getenv("USER");
        String password = System.getenv("PASSWORD");
        connection = DriverManager.getConnection(url, user, password);

        positionDao = new PositionDao(connection);
        quoteDao = new QuoteDao(connection);
        positionService = new PositionService(positionDao, quoteDao);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM position");
            statement.executeUpdate("DELETE FROM quote");
            statement.executeUpdate("INSERT INTO quote (symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent) VALUES ('AAPL', 145.00, 150.00, 140.00, 148.00, 100, '2024-08-26', 144.00, 4.00, '2.78%')");
        }
    }

    @After
    public void cleanUp() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM position");
            statement.executeUpdate("DELETE FROM quote");
        }
        connection.close();
    }

    @Test
    public void testBuy_NewPosition() {
        Position result = positionService.buy("AAPL", 20, 150.00);

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(20, result.getNumOfShares());
        assertEquals(3000.00, result.getValuePaid(), 0.01);
    }

    @Test
    public void testBuy_ExistingPosition() {
        Position pos = new Position();
        pos.setTicker("AAPL");
        pos.setNumOfShares(10);
        pos.setValuePaid(1500.00);
        positionDao.save(pos);

        Position result = positionService.buy("AAPL", 20, 150.00);

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(30, result.getNumOfShares());
        assertEquals(4500.00, result.getValuePaid(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuy_ExceedsVolume() {
        positionService.buy("AAPL", 200, 150.00);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuy_NoQuoteData() {
        positionService.buy("INVALID", 20, 150.00);
    }

    @Test
    public void testSell() {
        Position pos = new Position();
        pos.setTicker("AAPL");
        pos.setNumOfShares(20);
        pos.setValuePaid(3000.00);

        positionDao.save(pos);

        positionService.sell("AAPL");

        assertFalse(positionDao.findById("AAPL").isPresent());
    }
}
