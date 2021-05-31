package persistence.repository;

import model.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.IRouteRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RouteRepository implements IRouteRepository {

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public RouteRepository(Properties props) {
        logger.info("Initializing RouteDB with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Route route) {
        logger.traceEntry("saving route {}", route);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("insert into route " +
                "(destination,departureDateTime, availableSeats) values(?,?,?)")){
            preparedStatement.setString(1, route.getDestination());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(route.getDepartureDateTime()));
            preparedStatement.setLong(3,route.getAvailableSeats());
            int result = preparedStatement.executeUpdate();

            logger.trace("Saved {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public void delete(Route route) {
        logger.traceEntry("deleting route {}", route);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("delete from route " +
                "where id = ?")){
            preparedStatement.setLong(1, route.getId());
            int result = preparedStatement.executeUpdate();

            logger.trace("Deleted {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public void update(Route route) {
        logger.traceEntry("updating route {}", route);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("update route " +
                "set destination = ?, availableSeats = ?, departureDateTime = ? where id = ?")){
            preparedStatement.setString(1, route.getDestination());
            preparedStatement.setInt(2, route.getAvailableSeats());
            preparedStatement.setTimestamp(3,Timestamp.valueOf(route.getDepartureDateTime()));
            preparedStatement.setLong(4, route.getId());
            int result = preparedStatement.executeUpdate();

            logger.trace("Updated {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public Iterable<Route> findAll() {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Route> routes = new ArrayList<>();
        try(Statement stmt = connection.createStatement()){
            try(ResultSet rs = stmt.executeQuery("select * from route")){
                while(rs.next()) {
                    long id = rs.getLong("id");
                    Route route = new Route(rs.getString("destination"),
                            rs.getTimestamp("departureDateTime").toLocalDateTime());
                    route.setAvailableSeats(rs.getInt("availableSeats"));
                    route.setId(id);
                    routes.add(route);
                }
            }
        }
        catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return routes;
    }

    @Override
    public Route findOne(Long id) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from route " +
                "where id=?")){
            preparedStatement.setLong(1, id);
            try(ResultSet rs = preparedStatement.executeQuery()){
                Route route = new Route(rs.getString("destination"),
                        rs.getTimestamp("departureDateTime").toLocalDateTime());
                route.setAvailableSeats(rs.getInt("availableSeats"));
                route.setId(id);
                return route;
            }
        } catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Iterable<Route> findByDestination(String destination) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Route> routes = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from route " +
                "where destination=?")){
            preparedStatement.setString(1, destination);
            try(ResultSet rs = preparedStatement.executeQuery()){
                Route route = new Route(rs.getString("destination"),
                        rs.getTimestamp("departureDateTime").toLocalDateTime());
                route.setAvailableSeats(rs.getInt("availableSeats"));
                route.setId(rs.getLong("id"));
                routes.add(route);
            }
        } catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return routes;
    }
}
