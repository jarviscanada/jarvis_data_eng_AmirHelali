package ca.jrvs.apps.jdbc.sotckquote.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionDao implements CrudDao<Position, String> {

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
            statement.setString(1, entity.getTicker());
            statement.setInt(2, entity.getNumOfShares());
            statement.setBigDecimal(3, BigDecimal.valueOf(entity.getValuePaid()));
            statement.executeUpdate();
        } catch(SQLException e){
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
                    return Optional.of(rowToPositionMap(rs));
                }else{
                    return Optional.empty();
                }
            }
        } catch (SQLException e){
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
        } catch (SQLException e){
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
        } catch (SQLException e){
            throw new RuntimeException("Error deleting position by ID: " + s, e);
        }
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM position";
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(query);
        } catch(SQLException e){
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
