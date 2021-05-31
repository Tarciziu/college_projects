package persistence.repository;

import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.IUserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserRepository implements IUserRepository {

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public UserRepository(Properties props) {
        logger.info("Initializing UserDB with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(User user) {
        logger.traceEntry("saving user {}", user);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("insert into user " +
                "(username,password) values(?,?)")){
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            int result = preparedStatement.executeUpdate();

            logger.trace("Saved {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public void delete(User user) {
        logger.traceEntry("deleting user {}", user);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("delete from user " +
                "where id = ?")){
            preparedStatement.setLong(1, user.getId());
            int result = preparedStatement.executeUpdate();

            logger.trace("Deleted {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public void update(User user) {
        logger.traceEntry("deleting user {}", user);
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("update user " +
                "set username = ?, password = ? where id = ?")){
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setLong(3, user.getId());
            int result = preparedStatement.executeUpdate();

            logger.trace("Updated {} instances", result);
        }
        catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public Iterable<User> findAll() {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<User> users = new ArrayList<>();
        try(Statement stmt = connection.createStatement()){
            try(ResultSet rs = stmt.executeQuery("select * from user")){
                while(rs.next()) {
                    User user = new User(rs.getString("username"),rs.getString("password"));
                    user.setId(rs.getLong("id"));
                    users.add(user);
                }
            }
        }
        catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return users;
    }

    @Override
    public User findOne(Long id) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from user " +
                "where id=?")){
            preparedStatement.setLong(1, id);
            try(ResultSet rs = preparedStatement.executeQuery()){
                User user = new User(rs.getString("username"),rs.getString("password"));
                user.setId(rs.getLong("id"));
                return user;
            }
        } catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from user " +
                "where username = ?")){
            preparedStatement.setString(1, username);
            try(ResultSet rs = preparedStatement.executeQuery()){
                User user = new User(rs.getString("username"),rs.getString("password"));
                user.setId(rs.getLong("id"));
                System.out.println(user);
                return user;
            }
        } catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return null;
    }
}
