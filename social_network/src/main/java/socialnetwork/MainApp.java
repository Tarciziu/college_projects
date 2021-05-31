package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.LoginCredentials;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.LoginCredentialsValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDatabaseRepository;
import socialnetwork.repository.database.LoginCredentialsDatabaseRepository;
import socialnetwork.repository.database.MessageDatabaseRepository;
import socialnetwork.repository.database.UserDatabaseRepository;
import socialnetwork.service.UserService;

import java.io.IOException;

public class MainApp extends Application {
    private Scene loginScene;
    private Scene mainWindowScene;
    private UserService service;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Repository<Long, User> userDatabaseRepository =
                new UserDatabaseRepository("jdbc:postgresql://localhost:5432/social_network",
                        "postgres","tarciziu0609", new UserValidator());
        Repository<Long, Friendship> friendshipDatabaseRepositoryRepository = new FriendshipDatabaseRepository(
                "jdbc:postgresql://localhost:5432/social_network",
                "postgres","tarciziu0609", new FriendshipValidator());

        Repository<Long, Message> messageDatabaseRepositoryRepository = new MessageDatabaseRepository(
                "jdbc:postgresql://localhost:5432/social_network",
                "postgres","tarciziu0609", new MessageValidator());

        Repository<String, LoginCredentials> loginCreds = new LoginCredentialsDatabaseRepository(
                "jdbc:postgresql://localhost:5432/social_network",
                "postgres","tarciziu0609", new LoginCredentialsValidator());
        service = new UserService(userDatabaseRepository,
                friendshipDatabaseRepositoryRepository,
                messageDatabaseRepositoryRepository, loginCreds);
        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/socialnetwork/login.fxml"));
        AnchorPane loginLayout = loader.load();
        primaryStage.setScene(new Scene(loginLayout));
        primaryStage.setTitle("UMEET");

        LoginController loginController = loader.getController();
        loginController.setService(this.service);
    }
}
