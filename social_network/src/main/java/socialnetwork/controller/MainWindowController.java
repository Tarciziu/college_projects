package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socialnetwork.service.UserService;

import java.io.IOException;

public class MainWindowController {
    @FXML
    private AnchorPane mainAPane;
    @FXML
    private Pane mainPageButton;

    private AnchorPane friendsPane;
    private AnchorPane profilePane;
    private AnchorPane mainPagePane;
    private AnchorPane messengerPane;
    private AnchorPane rapoartePane;
    private UserService service;
    private FriendsWindowController friendsWindowController;
    private ProfileController profileController;
    private MainPageController mainPageController;
    private MessengerController messengerController;
    private RapoarteController rapoarteController;

    @FXML
    private void initialize() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/socialnetwork/friendsWindow.fxml"));
        friendsPane = (AnchorPane) loader.load();
        friendsWindowController = loader.getController();

        FXMLLoader loader2 = new FXMLLoader();
        loader2.setLocation(getClass().getResource("/socialnetwork/profile.fxml"));
        profilePane = (AnchorPane) loader2.load();
        profileController = loader2.getController();

        FXMLLoader loader3 = new FXMLLoader();
        loader3.setLocation(getClass().getResource("/socialnetwork/mainPage.fxml"));
        mainPagePane = (AnchorPane) loader3.load();
        mainPageController = loader3.getController();

        FXMLLoader loader4 = new FXMLLoader();
        loader4.setLocation(getClass().getResource("/socialnetwork/messenger.fxml"));
        messengerPane = (AnchorPane) loader4.load();
        messengerController = loader4.getController();

        FXMLLoader loader5 = new FXMLLoader();
        loader5.setLocation(getClass().getResource("/socialnetwork/rapoarte.fxml"));
        rapoartePane = (AnchorPane) loader5.load();
        rapoarteController = loader5.getController();
    }

    @FXML
    public void handleFriends(ActionEvent event) throws IOException {
        mainAPane.getChildren().clear();
        mainAPane.getChildren().add(friendsPane);
        AnchorPane.setLeftAnchor(friendsPane,0.0);
        friendsPane.toFront();
        friendsWindowController.setFriends();
    }

    @FXML
    public void handleProfile(ActionEvent actionEvent) throws IOException{
        mainAPane.getChildren().clear();
        mainAPane.getChildren().add(profilePane);
        AnchorPane.setLeftAnchor(profilePane,0.0);
        profilePane.toFront();
        profileController.setProfile();

    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/socialnetwork/login.fxml"));
        Scene login = new Scene(loader.load());
        Stage primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        primaryStage.setScene(login);
        primaryStage.resizableProperty().set(false);
        primaryStage.setMaximized(false);
        primaryStage.setHeight(400.0);
        primaryStage.setWidth(600.0);
        LoginController loginController = loader.getController();
        loginController.setService(service);
    }

    public void setService(UserService srv){
        service = srv;

        friendsWindowController.setService(service);
        profileController.setService(service);
        mainPageController.setService(service);
        messengerController.setService(service);
        rapoarteController.setService(service);
    }

    public void setMainPage(){
        mainAPane.getChildren().add(mainPagePane);
        AnchorPane.setLeftAnchor(friendsPane,0.0);
        mainPagePane.toFront();
        mainPageButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainAPane.getChildren().clear();
                mainAPane.getChildren().add(mainPagePane);
                AnchorPane.setLeftAnchor(mainPagePane,0.0);
                mainPagePane.toFront();
                mainPageController.setService(service);
                mainPageController.clear();
            }
        });
    }

    public void handleMessenger(ActionEvent actionEvent) {
        mainAPane.getChildren().clear();
        mainAPane.getChildren().add(messengerPane);
        AnchorPane.setLeftAnchor(messengerPane,0.0);
        messengerPane.toFront();
    }

    public void handleRapoarte(ActionEvent actionEvent) {
        mainAPane.getChildren().clear();
        mainAPane.getChildren().add(rapoartePane);
        AnchorPane.setLeftAnchor(rapoartePane,0.0);
        rapoartePane.toFront();
    }
}
