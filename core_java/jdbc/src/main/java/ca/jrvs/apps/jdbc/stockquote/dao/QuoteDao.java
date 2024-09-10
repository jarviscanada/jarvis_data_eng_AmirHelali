package ca.jrvs.apps.jdbc.stockquote.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteDao implements CrudDao<Quote, String> {

    private final Logger logger = LoggerFactory.getLogger(QuoteDao.class);
    private Connection connection;

    public QuoteDao(Connection connection){
        this.connection = connection;
    }

    @Override
    public Quote save(Quote entity) throws IllegalArgumentException {
        if (entity == null || entity.getTicker() == null){
            throw new IllegalArgumentException("Quote or Quote ID can't be null");
        }
        String query = "INSERT INTO quote (symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp)" +
                "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (symbol) DO UPDATE SET " +
                "open = EXCLUDED.open, " +
                "high = EXCLUDED.high, " +
                "low = EXCLUDED.low, " +
                "price = EXCLUDED.price, " +
                "volume = EXCLUDED.volume, " +
                "latest_trading_day = EXCLUDED.latest_trading_day, " +
                "previous_close = EXCLUDED.previous_close, " +
                "change = EXCLUDED.change, " +
                "change_percent = EXCLUDED.change_percent, " +
                "timestamp = EXCLUDED.timestamp";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            logger.info("Saving quote for symbol: {},", entity.getTicker());
            statement.setString(1, entity.getTicker());
            statement.setBigDecimal(2, BigDecimal.valueOf(entity.getOpen()));
            statement.setBigDecimal(3, BigDecimal.valueOf(entity.getHigh()));
            statement.setBigDecimal(4, BigDecimal.valueOf(entity.getLow()));
            statement.setBigDecimal(5, BigDecimal.valueOf(entity.getPrice()));
            statement.setInt(6, entity.getVolume());
            statement.setDate(7, new java.sql.Date(entity.getLatestTradingDay().getTime()));
            statement.setBigDecimal(8, BigDecimal.valueOf(entity.getPreviousClose()));
            statement.setBigDecimal(9, BigDecimal.valueOf(entity.getChange()));
            statement.setString(10, entity.getChangePercent());
            statement.setTimestamp(11, entity.getTimestamp());

            statement.executeUpdate();
            logger.info("Quote saved for symbol: {}", entity.getTicker());
        } catch (SQLException e){
            logger.error("Error saving quote for symbol: {}", entity.getTicker(), e);
            throw new RuntimeException("Error saving quote", e);
        }
        return entity;
    }

    @Override
    public Optional<Quote> findById(String s) throws IllegalArgumentException {
        if (s == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        String query = "SELECT * FROM quote WHERE symbol = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, s);
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    logger.info("Quote found for symbol: {}", s);
                    return Optional.of(rowToQuoteMap(rs));
                }else{
                    logger.warn("No quote found for symbol: {}", s);
                    return Optional.empty();
                }
            }
        } catch(SQLException e){
            logger.error("Error finding quote by ID: {}", s, e);
            throw new RuntimeException("Error finding quote by ID: " + s, e);
        }
    }

    @Override
    public Iterable<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();
        String query = "SELECT * FROM quote";
        try(Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                quotes.add(rowToQuoteMap(rs));
            }
            logger.info("Found {} quotes", quotes.size());
        } catch(SQLException e){
            logger.error("Error finding quotes", e);
            throw new RuntimeException("Error finding quotes", e);
        }
        return quotes;
    }

    @Override
    public void deleteById(String s) throws IllegalArgumentException {
        if (s == null){
            throw new IllegalArgumentException("ID cannot be null");
        }
        String query = "DELETE FROM quote WHERE symbol = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, s);
            statement.executeUpdate();
            logger.info("Quote deleted for symbol: {}", s);
        } catch (SQLException e){
            logger.error("Error deleting quote by ID: {}", s, e);
            throw new RuntimeException("Error deleting quote by ID: " + s, e);
        }
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM quote";
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(query);
            logger.info("All quotes deleted");
        } catch(SQLException e){
            logger.error("Error deleting quotes", e);
            throw new RuntimeException("Error deleting quotes", e);
        }
    }

    private Quote rowToQuoteMap(ResultSet rs) throws SQLException{
        Quote quote = new Quote();
        quote.setTicker(rs.getString("symbol"));
        quote.setOpen(rs.getBigDecimal("open").doubleValue());
        quote.setHigh(rs.getBigDecimal("high").doubleValue());
        quote.setLow(rs.getBigDecimal("low").doubleValue());
        quote.setPrice(rs.getBigDecimal("price").doubleValue());
        quote.setVolume(rs.getInt("volume"));
        quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
        quote.setPreviousClose(rs.getBigDecimal("previous_close").doubleValue());
        quote.setChange(rs.getBigDecimal("change").doubleValue());
        quote.setChangePercent(rs.getString("change_percent"));
        quote.setTimestamp(rs.getTimestamp("timestamp"));
        return quote;
    }
}
