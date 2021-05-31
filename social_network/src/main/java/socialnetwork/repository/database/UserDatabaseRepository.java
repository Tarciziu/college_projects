package socialnetwork.repository.database;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDatabaseRepository implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public UserDatabaseRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Optional<User> findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE uid = ?")){
            statement.setLong(1,aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                User user = new User(resultSet.getString(2), resultSet.getString(3));
                user.setId(resultSet.getLong(1));
                if (resultSet.getBinaryStream("image") != null){
                    InputStream in = resultSet.getBinaryStream("image");
                    Path file = Files.createTempFile("temp","photo.jpg");
                    OutputStream os = new FileOutputStream(file.toFile());
                    byte[] content = new byte[1000];
                    int size = 0;
                    while((size = in.read(content)) != -1)
                        os.write(content,0,size);
                    os.close();
                    in.close();
                    Image image = new Image("file:///"+file.toString(),
                            300,300,false,true);
                    user.setPicture(image);
                }
                Optional<User> optional = Optional.ofNullable(user);
                return optional;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("uid");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");

                User User = new User(firstName, lastName);
                User.setId(id);
                users.add(User);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        Optional<User> optional=Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            validator.validate(entity);
            try{
                PreparedStatement statement = connection
                        .prepareStatement("INSERT INTO Users(uid, firstname, lastname)"+
                                "VALUES (?,?,?)");
                statement.setLong(1,entity.getId());
                statement.setString(2,entity.getFirstName());
                statement.setString(3,entity.getLastName());
                statement.execute();
            }catch(SQLException e){
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM Users WHERE uid=?");
                statement.setLong(1,entity.getId());
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next())
                {User user = new User(resultSet.getString(2),resultSet.getString(3));
                user.setId(resultSet.getLong(1));
                optional = Optional.ofNullable(user);}
                return optional;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        catch (ValidationException e){
            throw e;
        }
        return optional;
    }

    @Override
    public Optional<User> delete(Long aLong) {
        Optional<User> optional=Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE uid = ?")) {
            statement.setInt(1,aLong.intValue());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                User user = new User(resultSet.getString(2),resultSet.getString(3));
                user.setId(resultSet.getLong(1));
                optional = Optional.ofNullable(user);
            }
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM Users WHERE uid = ?");
            preparedStatement.setLong(1,aLong);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return optional;
    }

    @Override
    public Optional<User> update(User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET image=? WHERE uid=?");

            /*statement.setBytes(1,
                    javax.xml.bind.DatatypeConverter.parseBase64Binary(entity.getPicture().toString()));
            */
            BufferedImage bImage = javafx.embed.swing.SwingFXUtils.fromFXImage(entity.getPicture(), null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(bImage, "png", outputStream);
                byte[] res  = outputStream.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(res);
                statement.setBinaryStream(1,inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            statement.setLong(2,entity.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
