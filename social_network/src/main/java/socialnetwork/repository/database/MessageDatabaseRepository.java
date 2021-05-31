package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MessageDatabaseRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;

    public MessageDatabaseRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Optional<Message> findOne(Long aLong) {
        Optional<Message> optional = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages" +
                     " WHERE mid=?")) {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long mid = resultSet.getLong(1);
                String text = resultSet.getString(2);
                LocalDateTime dateTime = resultSet.getTimestamp(3).toLocalDateTime();
                Long id_sender = resultSet.getLong(4);
                Long id_receiver = resultSet.getLong(5);

                Message message = new Message(text,id_sender,dateTime);
                message.setId(mid);
                message.addReceiver(id_receiver);
                optional = Optional.ofNullable(message);
            }
            return optional;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return optional;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long mid = resultSet.getLong(1);
                String text = resultSet.getString(2);
                LocalDateTime dateTime = resultSet.getTimestamp(3).toLocalDateTime();
                Long id_sender = resultSet.getLong(4);
                Long id_receiver = resultSet.getLong(5);

                Message message = new Message(text,id_sender,dateTime);
                message.setId(mid);
                message.addReceiver(id_receiver);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {
        Optional<Message> optional = Optional.empty();

            try(Connection connection = DriverManager.getConnection(url, username, password)) {
                PreparedStatement statement = connection
                        .prepareStatement("INSERT INTO messages(text, date," +
                                "sender, receiver)" +
                                "VALUES (?,?,?,?)");
                statement.setString(1,entity.getMessage());
                statement.setTimestamp(2,Timestamp.valueOf(entity.getDate()));
                statement.setLong(3,entity.getSender());
                statement.setLong(4,entity.getReceivers().get(0));
                statement.execute();
                return Optional.ofNullable(entity);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ValidationException e) {
                throw e;
            }
        return optional;
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }
}
