package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import services.IService;

import javax.swing.*;
import java.io.IOException;

public class LoginController {
    private IService service;

    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwordTxt;

    Scene mainParent;
    private MainWindowController2 mainWindowCtrl;

    @FXML
    private void initialize() {
    }

    public void setService(IService service){
        this.service = service;
    }

    public void handleLogin(ActionEvent event) throws Exception {
        try{
            User user = new User(usernameTxt.getText(),passwordTxt.getText());
            service.login(user, mainWindowCtrl);
            /*FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/gui/mainWindow.fxml"));
            Scene mainWindowScene = new Scene(loader.load());*/
            Stage primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            primaryStage.setScene(mainParent);
            primaryStage.setMaximized(false);
            primaryStage.resizableProperty().set(false);
            MainWindowController2 mainWindowController = mainWindowCtrl;
            mainWindowController.initialize();
            mainWindowController.setService(service);
            mainWindowController.setLoggedUser(user);
            mainWindowController.setTableRoutes();
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Incorrect username or password",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setController(MainWindowController2 mainWindowCtrl) {
        this.mainWindowCtrl = mainWindowCtrl;
    }

    public void setParent(Scene croot) {
        this.mainParent = croot;
    }
}
