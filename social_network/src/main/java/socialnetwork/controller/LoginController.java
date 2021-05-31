package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import socialnetwork.service.UserService;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label labelInfo;

    private UserService service;

    @FXML
    private void initialize() {
    }

    public void setService(UserService service){
        this.service = service;
    }

    @FXML
    public void handleLogin(ActionEvent event) throws IOException {
        if(!emailField.getText().equals("") && !passwordField.getText().equals("")) {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (service.tryLogin(email,password)) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/socialnetwork/mainWindow.fxml"));
                Scene mainWindowScene = new Scene(loader.load());
                Stage primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                primaryStage.setScene(mainWindowScene);
                primaryStage.setMaximized(true);
                primaryStage.resizableProperty().set(false);
                MainWindowController mainWindowController = loader.getController();
                mainWindowController.setService(service);
                mainWindowController.setMainPage();
            }
            else {
                labelInfo.setText("Email or password incorrect. Try again.");
                labelInfo.setTextFill(Paint.valueOf("red"));
            }
        }
        else {
            labelInfo.setText("Please insert your email and password");
            labelInfo.setTextFill(Paint.valueOf("#f50000"));
        }
    }
}
