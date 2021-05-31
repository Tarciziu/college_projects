package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import socialnetwork.domain.User;
import socialnetwork.service.UserService;


public class FriendsWindowController {
    @FXML
    private HBox hbox1;
    @FXML
    private HBox hbox2;
    @FXML
    private HBox hbox3;

    private UserService service;

    @FXML
    private void initialize() {
    }
    private Rectangle createClip(){
        Rectangle clip = new Rectangle();
        clip.setWidth(120);
        clip.setHeight(100);
        clip.setArcHeight(150);
        clip.setArcWidth(150);
        return clip;
    }
    public void setService(UserService srv){
        service = srv;
    }
    public void setFriends(){
        hbox1.getChildren().clear();
        hbox2.getChildren().clear();
        hbox3.getChildren().clear();
        int i=0;
        for(User u:service.getFriends()){
            Image image;
            if(u.getPicture() != null)
                image = u.getPicture();
            else
                image = new Image("/images/default.jpg",120,120, false, true);
            ImageView imageView = new ImageView(image);
            imageView.setClip(createClip());
            imageView.setFitHeight(120);
            imageView.setFitWidth(120);
            Button friendButton = new Button(u.getFirstName()+" "+u.getLastName(),imageView);
            friendButton.setContentDisplay(ContentDisplay.TOP);
            if(i/5==0) hbox1.getChildren().add(friendButton);
            if(i/5==1) hbox1.getChildren().add(friendButton);
            if(i/5==2) hbox1.getChildren().add(friendButton);
        }
    }
}
