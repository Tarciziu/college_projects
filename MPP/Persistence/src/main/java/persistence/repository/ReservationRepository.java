package persistence.repository;

import model.Reservation;
import model.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.IReservationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReservationRepository implements IReservationRepository {

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public ReservationRepository(Properties props) {
        logger.info("Initializing ReservationDB with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Reservation reservation) {
        logger.traceEntry("saving reservation {}", reservation);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("insert into reservation " +
                "(clientName,reservedSeat, id_route) values(?,?,?)")){
            preparedStatement.setString(1, reservation.getClientName());
            preparedStatement.setInt(2, reservation.getReservedSeat());
            preparedStatement.setLong(3,reservation.getRoute().getId());
            int result = preparedStatement.executeUpdate();

            logger.trace("Saved {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public void delete(Reservation reservation) {
        logger.traceEntry("deleting reservation {}", reservation);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("delete from reservation " +
                "where id = ?")){
            preparedStatement.setLong(1, reservation.getId());
            int result = preparedStatement.executeUpdate();

            logger.trace("Deleted {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public void update(Reservation reservation) {
        logger.traceEntry("updating reservation {}", reservation);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("update reservation " +
                "set clientName = ?, reservedSeat = ?, id_route = ? where id = ?")){
            preparedStatement.setString(1, reservation.getClientName());
            preparedStatement.setInt(2, reservation.getReservedSeat());
            preparedStatement.setLong(3,reservation.getRoute().getId());
            preparedStatement.setLong(4, reservation.getId());
            int result = preparedStatement.executeUpdate();

            logger.trace("Updated {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public Iterable<Reservation> findAll() {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Reservation> reservations = new ArrayList<>();
        try(Statement stmt = connection.createStatement()){
            try(ResultSet rs = stmt.executeQuery("select * from reservation res inner join " +
                    "route rou on rou.id = res.id_route")){
                while(rs.next()) {
                    long id = rs.getLong("id");
                    long id_route = rs.getLong("id_route");
                    Route route = new Route(rs.getString("destination"),
                            rs.getTimestamp("departureDateTime").toLocalDateTime());
                    Reservation reservation = new Reservation(rs.getString("clientName"),
                            rs.getInt("reservedSeat"), route);
                    route.setId(id_route);
                    reservation.setId(id);
                    reservations.add(reservation);
                }
            }
        }
        catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return reservations;
    }

    @Override
    public Reservation findOne(Long id) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from reservation res " +
                "inner join route rou on rou.id = res.id_route " +
                "where res.id=?")){
            preparedStatement.setLong(1, id);
            try(ResultSet rs = preparedStatement.executeQuery()){
                    long id_route = rs.getLong("id_route");
                    Route route = new Route(rs.getString("destination"),
                            rs.getTimestamp("departureDateTime").toLocalDateTime());
                    route.setId(id_route);
                    Reservation reservation = new Reservation(rs.getString("clientName"),
                            rs.getInt("reservedSeat"), route);
                    reservation.setId(id);
                return reservation;
                }
            } catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Iterable<Reservation> findByClientName(String clientName) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Reservation> reservations = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from reservation res " +
                "inner join route rou on rou.id = res.id_route " +
                "where clientName = ?")){
            preparedStatement.setString(1, clientName);
            try(ResultSet rs = preparedStatement.executeQuery()){
                long id_route = rs.getLong("id_route");
                long id = rs.getLong("rou.id");
                Route route = new Route(rs.getString("destination"),
                        rs.getTimestamp("departureDateTime").toLocalDateTime());
                route.setId(id_route);
                Reservation reservation = new Reservation(rs.getString("clientName"),
                        rs.getInt("reservedSeat"), route);
                reservation.setId(id);
                reservations.add(reservation);
            }
        } catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return reservations;
    }

    @Override
    public Iterable<Reservation> findByRoute(Route route) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Reservation> reservations = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select res.id as resid, clientName," +
                " id_route, rou.id as rouid, reservedSeat" +
                " from reservation res " +
                "inner join route rou on rou.id = res.id_route " +
                "where id_route = ?")){
            preparedStatement.setLong(1, route.getId());
            try(ResultSet rs = preparedStatement.executeQuery()){
                while(rs.next()){
                    long id = rs.getLong("resid");
                    Reservation reservation = new Reservation(rs.getString("clientName"),
                            rs.getInt("reservedSeat"), route);
                    reservation.setId(id);
                    reservations.add(reservation);
                }
            }
        } catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return reservations;
    }
}
