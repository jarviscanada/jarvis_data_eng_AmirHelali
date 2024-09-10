package ca.jrvs.apps.jdbc.stockquote.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDao implements CrudDao<Position, String> {

    private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);
    private Connection connection;

    public PositionDao(Connection connection){
        this.connection = connection;
    }

    @Override
    public Position save(Position entity) throws IllegalArgumentException {
        if(entity == null || entity.getTicker() == null){
            throw new IllegalArgumentException("Entity and ID cannot be null");
        }
        String query = "INSERT INTO position (symbol, number_of_shares, value_paid) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (symbol) DO UPDATE SET " +
                "number_of_shares = EXCLUDED.number_of_shares, " +
                "value_paid = EXCLUDED.value_paid";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            logger.info("Saving position for symbol: {}", entity.getTicker());
            statement.setString(1, entity.getTicker());
            statement.setInt(2, entity.getNumOfShares());
            statement.setBigDecimal(3, BigDecimal.valueOf(entity.getValuePaid()));
            statement.executeUpdate();
            logger.info("Position saved for symbol: {}", entity.getTicker());
        } catch(SQLException e){
            logger.error("Error saving position for symbol: {}", entity.getTicker(), e);
            throw new RuntimeException("Error saving position", e);
        }
        return entity;
    }

    @Override
    public Optional<Position> findById(String s) throws IllegalArgumentException {
        if(s == null){
            throw new IllegalArgumentException("ID cannot be null");
        }

        String query = "SELECT * FROM position WHERE symbol = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, s);
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    logger.info("Position found for symbol: {}", s);
                    return Optional.of(rowToPositionMap(rs));
                }else{
                    logger.warn("No position found for symbol: {}", s);
                    return Optional.empty();
                }
            }
        } catch (SQLException e){
            logger.error("Error finding position by ID: {}", s, e);
            throw new RuntimeException("Error finding position by ID: " + s, e);
        }
    }

    @Override
    public Iterable<Position> findAll() {
        List<Position> positions = new ArrayList<>();
        String query = "SELECT * FROM position";
        try(Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(query)){
            while (rs.next()){
                positions.add(rowToPositionMap(rs));
            }
            logger.info("Found {} quotes", positions.size());
        } catch (SQLException e){
            logger.error("Error finding positions", e);
            throw new RuntimeException("Error finding positions");
        }
        return positions;
    }

    @Override
    public void deleteById(String s) throws IllegalArgumentException {
        if(s == null){
            throw new IllegalArgumentException("ID cannot be null");
        }

        String query = "DELETE FROM position WHERE symbol = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, s);
            statement.executeUpdate();
            logger.info("Position deleted for symbol: {}", s);
        } catch (SQLException e){
            logger.error("Error deleting position by ID: {}", s, e);
            throw new RuntimeException("Error deleting position by ID: " + s, e);
        }
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM position";
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(query);
            logger.info("All positions deleted");
        } catch(SQLException e){
            logger.error("Error deleting positions", e);
            throw new RuntimeException("Error deleting all positions", e);
        }
    }

    private Position rowToPositionMap(ResultSet rs) throws SQLException{
        Position position = new Position();
        position.setTicker(rs.getString("symbol"));
        position.setNumOfShares(rs.getInt("number_of_shares"));
        position.setValuePaid(rs.getBigDecimal("value_paid").doubleValue());
        return position;
    }
}
