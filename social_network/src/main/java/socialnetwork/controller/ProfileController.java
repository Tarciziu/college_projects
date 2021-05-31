package socialnetwork.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.service.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ProfileController {
    @FXML
    private ImageView profileImage;
    @FXML
    private Label nameLabel;

    private UserService service;

    @FXML
    private void initialize() {
        profileImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png",
                        "*.jpg","*.gif"));
                File file = fileChooser.showOpenDialog((Stage) ((Node)event.getSource()).getScene().getWindow());
                if(file != null) {
                    Image image = null;
                    try {
                        image = new Image(new FileInputStream(file));
                        profileImage.setImage(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    BufferedImage bImage = javafx.embed.swing.SwingFXUtils.fromFXImage(image, null);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(bImage, "png", outputStream);
                        byte[] res = outputStream.toByteArray();
                        InputStream inputStream = new ByteArrayInputStream(res);
                        User user = service.getLoggedUser();
                        user.setPicture(image);
                        service.updateUser(user);
                        profileImage.setImage(new Image(inputStream));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Rectangle createClip(){
        Rectangle clip = new Rectangle();
        clip.setWidth(220);
        clip.setHeight(200);
        clip.setArcHeight(250);
        clip.setArcWidth(200);
        return clip;
    }
    public void setService(UserService srv){
        service = srv;
    }
    public void setProfile(){
        if(service.getLoggedUser().getPicture()!=null)
            profileImage.setImage(service.getLoggedUser().getPicture());
        else
            profileImage.setImage(new Image("/images/default.jpg",
                    300,300,false,true));
        profileImage.setClip(createClip());
        User u = service.getLoggedUser();
        nameLabel.setText(u.getFirstName() + " " +u.getLastName());
        nameLabel.autosize();
        nameLabel.setAlignment(Pos.CENTER);
    }
}
