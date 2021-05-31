package socialnetwork.repository.database;

import socialnetwork.domain.LoginCredentials;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.Optional;

public class LoginCredentialsDatabaseRepository implements Repository<String, LoginCredentials> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<LoginCredentials> validator;

    public LoginCredentialsDatabaseRepository(String url, String username, String password, Validator<LoginCredentials> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<LoginCredentials> findOne(String s) {
        Optional<LoginCredentials> optional = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM logincredentials WHERE email = ?")){
            statement.setString(1,s);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
               LoginCredentials loginCredentials = new LoginCredentials(
                       resultSet.getLong("uid"),
                       resultSet.getString("password"));
               loginCredentials.setId(resultSet.getString("email"));

                optional=Optional.ofNullable(loginCredentials);
            }
            return optional;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<LoginCredentials> findAll() {
        return null;
    }

    @Override
    public Optional<LoginCredentials> save(LoginCredentials entity) {
        return Optional.empty();
    }

    @Override
    public Optional<LoginCredentials> delete(String s) {
        return Optional.empty();
    }

    @Override
    public Optional<LoginCredentials> update(LoginCredentials entity) {
        return Optional.empty();
    }
}
