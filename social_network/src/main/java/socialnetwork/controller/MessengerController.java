package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.PopOver;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.service.UserService;
import socialnetwork.utils.Constants;
import java.util.Collection;

public class MessengerController {

    @FXML
    private ListView<HBox> conversationsView;
    @FXML
    private ListView<VBox> chatView;
    @FXML
    private TextField typeField;
    @FXML
    private Label chatterName;
    @FXML
    private Button newMessageButton;

    private UserService service;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<User> conversations = FXCollections.observableArrayList();
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private ObservableList<HBox> listHBox = FXCollections.observableArrayList();
    private ObservableList<VBox> listVBox = FXCollections.observableArrayList();
    private ObservableList<HBox> listPop = FXCollections.observableArrayList();

    private PopOver newMessagePopOver;

    @FXML
    public void initialize(){
    }

    public void setService(UserService service) {
        this.service = service;
        users.setAll((Collection<? extends User>) service.getAll());
        users.remove(service.getLoggedUser());
        conversations.setAll(service.getMyConversations());
        loadConversations();
    }

    private void loadConversations(){

        for(User u:conversations){
            Image image = u.getPicture();

            if(image == null)
                image = new Image("/images/default.jpg");

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setClip(createClip());

            Label label = new Label(u.getFirstName()+" "+u.getLastName());
            label.setPrefHeight(50);
            label.setPrefWidth(150);
            label.setStyle("-fx-text-alignment: CENTER;-fx-font-size: 15;-fx-text-fill: white;" +
                    "-fx-background-color: transparent");
            label.setAlignment(Pos.CENTER);

            HBox hbox = new HBox(imageView,label);
            hbox.setStyle("-fx-background-color: #1e1e1e;-fx-border-color: white;" +
                    "-fx-border-width: 0.05em;");
            hbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    chatView.getItems().clear();
                    messages.setAll(service.getConversationWith(u.getId()));
                    chatterName.setText(u.getFirstName()+" "+u.getLastName());
                    for(Message m:messages){
                        Label label = new Label(m.getMessage());
                        label.setWrapText(true);
                        label.autosize();
                        label.setStyle("-fx-font-size: 15;" +
                                "-fx-background-radius: 5em;-fx-text-fill: white");
                        Label date = new Label(m.getDate().format(Constants.DATE_TIME_FORMATTER));
                        date.setStyle("-fx-font-size: 9;-fx-background-color: transparent;-fx-text-fill: white");
                        date.autosize();

                        VBox vbox = new VBox(label,date);
                        if(!m.getSender().equals(u.getId())){
                            label.setStyle("-fx-background-color: #b5104a;");
                            vbox.setAlignment(Pos.CENTER_RIGHT);
                        }
                        else{
                            label.setStyle("-fx-background-color: #3c3c3d;-fx-background-radius: 5em;");
                        }
                        vbox.autosize();
                        vbox.maxWidth(200);
                        listVBox.add(vbox);
                    }
                    chatView.setItems(listVBox);
                    typeField.onKeyPressedProperty().unbind();
                    typeField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            if(event.getCode() == KeyCode.ENTER){
                                String message = typeField.getText();
                                Message m = service.sendMessage(service.getLoggedUser().getId(), u.getId(),message).get();
                                if(m!=null)
                                    messages.add(m);
                                typeField.setText("");
                                Label label = new Label(m.getMessage());
                                label.setAlignment(Pos.CENTER_LEFT);
                                label.setWrapText(true);
                                label.autosize();
                                label.setStyle("-fx-font-size: 15;" +
                                        "-fx-text-fill: white");
                                Label date = new Label(m.getDate().format(Constants.DATE_TIME_FORMATTER));
                                date.setStyle("-fx-font-size: 9;-fx-background-color: transparent;-fx-text-fill: white");
                                date.autosize();
                                VBox vbox = new VBox(label,date);

                                label.setStyle("-fx-background-color: #b5104a;");
                                vbox.setAlignment(Pos.CENTER_RIGHT);
                                vbox.autosize();
                                vbox.maxWidth(200);
                                chatView.getItems().add(vbox);
                            }
                        }
                    });
                }
            });
            listHBox.add(hbox);
        }
        chatView.scrollTo(chatView.getItems().size() - 1);
        conversationsView.setItems(listHBox);
    }

    private Rectangle createClip(){
        Rectangle clip = new Rectangle();
        clip.setWidth(50);
        clip.setHeight(50);
        clip.setArcHeight(50);
        clip.setArcWidth(50);
        return clip;
    }

    public void handleNewMessage(ActionEvent actionEvent) {
        listPop.clear();
        for(User u:users){
            Image image = u.getPicture();
            if(image == null)
                image = new Image("/images/default.jpg");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setClip(createClip());
            Button profile = new Button(u.getFirstName()+" "+u.getLastName(),imageView);
            profile.setGraphicTextGap(15);
            profile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    if(!conversations.contains(u)) {
                        Image image = u.getPicture();

                        if(image == null)
                            image = new Image("/images/default.jpg");

                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        imageView.setClip(createClip());

                        Label label = new Label(u.getFirstName()+" "+u.getLastName());
                        label.setPrefHeight(50);
                        label.setPrefWidth(150);
                        label.setStyle("-fx-text-alignment: CENTER;-fx-font-size: 15;-fx-text-fill: white;" +
                                "-fx-background-color: transparent");
                        label.setAlignment(Pos.CENTER);

                        HBox hbox = new HBox(imageView,label);
                        hbox.setStyle("-fx-background-color: #1e1e1e;-fx-border-color: white;" +
                                "-fx-border-width: 0.05em;");
                        hbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                chatView.getItems().clear();
                                messages.setAll(service.getConversationWith(u.getId()));
                                chatterName.setText(u.getFirstName()+" "+u.getLastName());
                                for(Message m:messages){
                                    Label label = new Label(m.getMessage());
                                    label.setWrapText(true);
                                    label.autosize();
                                    label.setStyle("-fx-font-size: 15;" +
                                            "-fx-background-radius: 5em;-fx-text-fill: white");
                                    Label date = new Label(m.getDate().format(Constants.DATE_TIME_FORMATTER));
                                    date.setStyle("-fx-font-size: 9;-fx-background-color: transparent;-fx-text-fill: white");
                                    date.autosize();

                                    VBox vbox = new VBox(label,date);
                                    if(!m.getSender().equals(u.getId())){
                                        label.setStyle("-fx-background-color: #b5104a;");
                                        vbox.setAlignment(Pos.CENTER_RIGHT);
                                    }
                                    else{
                                        label.setStyle("-fx-background-color: #3c3c3d;-fx-background-radius: 5em;");
                                    }
                                    vbox.autosize();
                                    vbox.maxWidth(200);
                                    listVBox.add(vbox);
                                }
                                chatView.setItems(listVBox);
                                typeField.onKeyPressedProperty().unbind();
                                typeField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                                    @Override
                                    public void handle(KeyEvent event) {
                                        if(event.getCode() == KeyCode.ENTER){
                                            String message = typeField.getText();
                                            Message m = service.sendMessage(service.getLoggedUser().getId(), u.getId(),message).get();
                                            if(m!=null)
                                                messages.add(m);
                                            typeField.setText("");
                                            Label label = new Label(m.getMessage());
                                            label.setAlignment(Pos.CENTER_LEFT);
                                            label.setWrapText(true);
                                            label.autosize();
                                            label.setStyle("-fx-font-size: 15;" +
                                                    "-fx-text-fill: white");
                                            Label date = new Label(m.getDate().format(Constants.DATE_TIME_FORMATTER));
                                            date.setStyle("-fx-font-size: 9;-fx-background-color: transparent;-fx-text-fill: white");
                                            date.autosize();
                                            VBox vbox = new VBox(label,date);

                                            label.setStyle("-fx-background-color: #b5104a;");
                                            vbox.setAlignment(Pos.CENTER_RIGHT);
                                            vbox.autosize();
                                            vbox.maxWidth(200);
                                            chatView.getItems().add(vbox);
                                        }
                                    }
                                });
                            }
                        });
                        listHBox.add(hbox);
                        chatView.getItems().clear();
                        messages.setAll(service.getConversationWith(u.getId()));
                        chatterName.setText(u.getFirstName()+" "+u.getLastName());
                        for(Message m:messages){
                            Label label2 = new Label(m.getMessage());
                            label2.setWrapText(true);
                            label2.autosize();
                            label2.setStyle("-fx-font-size: 15;" +
                                    "-fx-background-radius: 5em;-fx-text-fill: white");
                            Label date = new Label(m.getDate().format(Constants.DATE_TIME_FORMATTER));
                            date.setStyle("-fx-font-size: 9;-fx-background-color: transparent;-fx-text-fill: white");
                            date.autosize();

                            VBox vbox = new VBox(label2,date);
                            if(!m.getSender().equals(u.getId())){
                                label2.setStyle("-fx-background-color: #b5104a;");
                                vbox.setAlignment(Pos.CENTER_RIGHT);
                            }
                            else{
                                label2.setStyle("-fx-background-color: #3c3c3d;-fx-background-radius: 5em;");
                            }
                            vbox.autosize();
                            vbox.maxWidth(200);
                            listVBox.add(vbox);
                        }
                        chatView.setItems(listVBox);
                        typeField.onKeyPressedProperty().unbind();
                        typeField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                if(event.getCode() == KeyCode.ENTER){
                                    String message = typeField.getText();
                                    Message m = service.sendMessage(service.getLoggedUser().getId(), u.getId(),message).get();
                                    if(m!=null)
                                        messages.add(m);
                                    typeField.setText("");
                                    Label label = new Label(m.getMessage());
                                    label.setAlignment(Pos.CENTER_LEFT);
                                    label.setWrapText(true);
                                    label.autosize();
                                    label.setStyle("-fx-font-size: 15;" +
                                            "-fx-text-fill: white");
                                    Label date = new Label(m.getDate().format(Constants.DATE_TIME_FORMATTER));
                                    date.setStyle("-fx-font-size: 9;-fx-background-color: transparent;-fx-text-fill: white");
                                    date.autosize();
                                    VBox vbox = new VBox(label,date);

                                    label.setStyle("-fx-background-color: #b5104a;");
                                    vbox.setAlignment(Pos.CENTER_RIGHT);
                                    vbox.autosize();
                                    vbox.maxWidth(200);
                                    chatView.getItems().add(vbox);
                                }
                            }
                        });
                        conversations.add(u);
                    }
                    else{
                        chatView.getItems().clear();
                        messages.setAll(service.getConversationWith(u.getId()));
                        chatterName.setText(u.getFirstName()+" "+u.getLastName());
                        for(Message m:messages){
                            Label label = new Label(m.getMessage());
                            label.setWrapText(true);
                            label.autosize();
                            label.setStyle("-fx-font-size: 15;" +
                                    "-fx-background-radius: 5em;-fx-text-fill: white");
                            Label date = new Label(m.getDate().format(Constants.DATE_TIME_FORMATTER));
                            date.setStyle("-fx-font-size: 9;-fx-background-color: transparent;-fx-text-fill: white");
                            date.autosize();

                            VBox vbox = new VBox(label,date);
                            if(!m.getSender().equals(u.getId())){
                                label.setStyle("-fx-background-color: #b5104a;");
                                vbox.setAlignment(Pos.CENTER_RIGHT);
                            }
                            else{
                                label.setStyle("-fx-background-color: #3c3c3d;-fx-background-radius: 5em;");
                            }
                            vbox.autosize();
                            vbox.maxWidth(200);
                            listVBox.add(vbox);
                        }
                        chatView.setItems(listVBox);
                        typeField.onKeyPressedProperty().unbind();
                        typeField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                if(event.getCode() == KeyCode.ENTER){
                                    String message = typeField.getText();
                                    Message m = service.sendMessage(service.getLoggedUser().getId(), u.getId(),message).get();
                                    if(m!=null)
                                        messages.add(m);
                                    typeField.setText("");
                                    Label label = new Label(m.getMessage());
                                    label.setAlignment(Pos.CENTER_LEFT);
                                    label.setWrapText(true);
                                    label.autosize();
                                    label.setStyle("-fx-font-size: 15;" +
                                            "-fx-text-fill: white");
                                    Label date = new Label(m.getDate().format(Constants.DATE_TIME_FORMATTER));
                                    date.setStyle("-fx-font-size: 9;-fx-background-color: transparent;-fx-text-fill: white");
                                    date.autosize();
                                    VBox vbox = new VBox(label,date);

                                    label.setStyle("-fx-background-color: #b5104a;");
                                    vbox.setAlignment(Pos.CENTER_RIGHT);
                                    vbox.autosize();
                                    vbox.maxWidth(200);
                                    chatView.getItems().add(vbox);
                                }
                            }
                        });
                    }
                }
            });
            HBox hbox = new HBox(profile);
            listPop.add(hbox);
        }
        ListView<HBox> listView = new ListView(listPop);
        newMessagePopOver = new PopOver(listView);
        newMessagePopOver.show(newMessageButton);
    }
}
