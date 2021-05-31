package socialnetwork.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.PopOver;
import socialnetwork.domain.User;
import socialnetwork.service.UserService;
import socialnetwork.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainPageController {
    @FXML
    public ListView<HBox> listView;
    @FXML
    private TextField searchBar;
    @FXML
    private Button friendRequestsButton;
    @FXML
    private Button notificationsButton;

    private ObservableList<User> list = FXCollections.observableArrayList();
    private ObservableList<HBox> listHBox = FXCollections.observableArrayList();
    private FilteredList<User> filteredList;
    private ObservableList<User> friends = FXCollections.observableArrayList();
    private ObservableList<User> friendshipsAsked = FXCollections.observableArrayList();
    private ObservableList<User> friendReq = FXCollections.observableArrayList();

    private UserService service;
    private PopOver popOverFriendRequests;

    @FXML
    private void initialize() {
    }

    private void handleSearch() {
        listView.getItems().clear();
        Predicate<User> filterUsers = x->{
          String s = x.getFirstName()+" "+x.getLastName();
          if(s.contains(searchBar.getText())&& service.getLoggedUser().getId()!=x.getId())
              return true;
          return false;
        };
        list.setAll(StreamSupport.stream(service.getAll().spliterator(),false)
        .filter(filterUsers)
        .collect(Collectors.toList()));
        for(User u:list){
            String userName = u.getFirstName()+" "+u.getLastName();

            // Image setting
            Image image = u.getPicture();
            if(image == null)
                image = new Image("/images/default.jpg");

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setClip(createClip());

            // Buttons creation
            Button profile = new Button(userName,imageView);
            profile.setGraphicTextGap(15);
            profile.setStyle("-fx-background-color: transparent");
            Button addOrUnFriend;
            if(friends.contains(u)){
                addOrUnFriend = new Button("Unfriend");
                addOrUnFriend.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        service.updateFriendship(u.getId(),"UNFRIENDED");
                        friends.clear();
                        friends.setAll(service.getFriends());
                    }
                });
            }
            else if(friendshipsAsked.contains(u)){
                addOrUnFriend = new Button("Cancel request");
                addOrUnFriend.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        service.updateFriendship(u.getId(),"CANCELED");
                        friendshipsAsked.remove(u);
                    }
                });
            }
            else if(friendReq.contains(u)){
                addOrUnFriend = new Button("Accept");
                addOrUnFriend.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        service.updateFriendship(u.getId(),"ACCEPTED");
                        friendReq.remove(u);
                    }
                });
            }
            else {
                addOrUnFriend = new Button("Add friend");
                addOrUnFriend.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        service.addFriend(u.getId());
                        service.updateFriendship(u.getId(),"PENDING");
                        friendshipsAsked.add(u);
                    }
                });
            }
            HBox hbox = new HBox(profile,addOrUnFriend);
            hbox.setStyle("-fx-background-color: #1e1e1e");
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(10);
            listHBox.add(hbox);
        }
        listView.setItems(listHBox);
        listView.setPrefHeight(listHBox.get(0).getPrefHeight()*listHBox.size());
    }

    public void setService(UserService srv){
        service = srv;
        friends.setAll(service.getFriends());
        friendshipsAsked.setAll(service.getFriendRequestsSent());
        friendReq.setAll(service.getFriendRequests());
        searchBar.textProperty().addListener(x->handleSearch());
    }

    public void clear(){
        searchBar.clear();
        listView.getItems().clear();
    }

    public void handleFriendRequests(ActionEvent actionEvent) {
        popOverFriendRequests = new PopOver();
        ListView<HBox> friendRequests = new ListView<>();
        friendRequests.setStyle("-fx-background-color: #1e1e1e");

        // sau mai bine cu un Pane in dreptul caruia pun cele 2 butoane.
        // Unul colorat albastru sau mov(culoarea aplicatiei), celalalt gri

        /*Button profile = new Button("Ion Ababei",imageView);
        friendRequests.getItems().add(new HBox(profile,
                new Button("Accept"),
                new Button("Reject")));*/
        for(User u:friendReq){
            String userName = u.getFirstName()+" "+u.getLastName();

            // Image setting
            Image image = u.getPicture();
            if(image == null)
                image = new Image("/images/default.jpg");

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setClip(createClip());

            // Buttons creation
            Button profile = new Button(userName,imageView);
            profile.setGraphicTextGap(15);
            profile.setStyle("-fx-background-color: transparent");
            Label date = new Label(service.getFriendshipRequest(u.getId()).getDate().format(Constants.DATE_FORMATTER));
            date.setStyle("-fx-font-size: 9");
            date.setPrefWidth(200);
            date.setAlignment(Pos.CENTER);
            VBox vbox = new VBox(profile,date);
            Button accept = new Button("Accept");
            accept.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    service.updateFriendship(u.getId(),"ACCEPTED");
                    friendReq.remove(u);
                }
            });
            Button reject = new Button("Reject");
            reject.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    service.updateFriendship(u.getId(),"REJECTED");
                    friendReq.remove(u);
                }
            });
            HBox hbox = new HBox(vbox,accept,reject);
            hbox.setStyle("-fx-background-color: #1e1e1e");
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(10);
            friendRequests.getItems().add(hbox);
        }
        friendRequests.setMaxHeight(300);
        friendRequests.setMaxWidth(400);
        friendRequests.setPrefHeight(90);
        friendRequests.setPrefWidth(400);
        friendRequests.autosize();
        popOverFriendRequests = new PopOver(friendRequests);
        popOverFriendRequests.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOverFriendRequests.setDetachable(false);
        popOverFriendRequests.show(friendRequestsButton);
    }

    private Rectangle createClip(){
        Rectangle clip = new Rectangle();
        clip.setWidth(50);
        clip.setHeight(50);
        clip.setArcHeight(50);
        clip.setArcWidth(50);
        return clip;
    }
}
