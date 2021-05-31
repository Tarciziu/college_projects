package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDatabaseRepository implements Repository<Long, Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;

    public FriendshipDatabaseRepository(String url, String username,
                                        String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Friendship> findOne(Long id){
        Optional<Friendship> optional = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE fid = ?")){
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
            Friendship friendship = new Friendship(resultSet.getLong(1),
                    resultSet.getLong(2),resultSet.getDate(3).toLocalDate(),
                    resultSet.getString(4));

            optional=Optional.ofNullable(friendship);
            }
            return optional;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return optional;
    }
    @Override
    public Iterable<Friendship> findAll(){
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("fid");
                Long id1 = resultSet.getLong("u1id");
                Long id2 = resultSet.getLong("u2id");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String state = resultSet.getString("state");

                Friendship friendship = new Friendship(id1,id2,date,state);
                friendship.setId(id);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }
    @Override
    public Optional<Friendship> save(Friendship entity) {
        Optional<Friendship> optional = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            validator.validate(entity);
            try {
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM friendships" +
                                " WHERE ((u1id=? and u2id=?) or (u1id =? and u2id=?))");
                statement.setLong(1, entity.getFriendship().getLeft());
                statement.setLong(2, entity.getFriendship().getRight());
                statement.setLong(3, entity.getFriendship().getRight());
                statement.setLong(4, entity.getFriendship().getLeft());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    Friendship friendship = new Friendship(resultSet.getLong(2),
                            resultSet.getLong(3), resultSet
                            .getDate(4).toLocalDate());
                    friendship.setId(resultSet.getLong(1));
                    optional = Optional.ofNullable(friendship);
                    return optional;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                PreparedStatement statement = connection
                        .prepareStatement("INSERT INTO Friendships(u1id, u2id, date)" +
                                "VALUES (?,?,?)");
                statement.setLong(1, entity.getFriendship().getLeft());
                statement.setLong(2, entity.getFriendship().getRight());
                statement.setDate(3, Date
                        //.valueOf(entity.getDate().format(Constants.DATE_TIME_FORMATTER)));
                        .valueOf(LocalDate.now()
                                .format(Constants.DATE_FORMATTER)));
                statement.execute();
                return optional;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ValidationException e) {
                throw e;
            }} catch (SQLException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            throw e;
        }
            return optional;

    }
    @Override
    public Optional<Friendship> delete(Long id){
        Optional<Friendship> optional = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE fid=?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Friendship friendship = new Friendship(resultSet.getLong(2),
                        resultSet.getLong(3),resultSet.getDate(4).toLocalDate());
                friendship.setId(resultSet.getLong(1));
                optional = Optional.ofNullable(friendship);
            }
            String query = "DELETE FROM friendships WHERE fid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return optional;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return optional;
    }
    @Override
    public Optional<Friendship> update(Friendship entity){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM friendships" +
                                " WHERE ((u1id=? and u2id=?) or (u1id =? and u2id=?))");
                statement.setLong(1, entity.getFriendship().getLeft());
                statement.setLong(2, entity.getFriendship().getRight());
                statement.setLong(3, entity.getFriendship().getRight());
                statement.setLong(4, entity.getFriendship().getLeft());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    Friendship friendship = new Friendship(resultSet.getLong(2),
                            resultSet.getLong(3), resultSet
                            .getDate(4).toLocalDate());
                    friendship.setId(resultSet.getLong(1));
                    PreparedStatement preparedStatement = connection
                            .prepareStatement("UPDATE friendships SET state=?, date = ? WHERE fid=?");
                    preparedStatement.setString(1,entity.getState());
                    preparedStatement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                    preparedStatement.setLong(3,friendship.getId());
                    preparedStatement.execute();
                    Optional<Friendship> optional;
                    optional = Optional.ofNullable(friendship);
                    return optional;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return Optional.empty();
    }
}
