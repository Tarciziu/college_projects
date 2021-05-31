package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Reservation;
import model.Route;
import model.User;
import services.IService;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import services.IObserver;

public class MainWindowController2 extends UnicastRemoteObject implements Serializable, IObserver {
    private User loggedUser;

    private IService service;

    @FXML
    private TextField destinationTxt;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Integer> hourComboBox;

    @FXML
    TableColumn<Route, String> destinationColumn;
    @FXML
    TableColumn<Route, LocalDateTime> departureColumn;
    @FXML
    TableColumn<Route, Integer> availableSeatsColumn;
    @FXML
    TableView<Route> tableRoutes;

    ObservableList<Route> modelRoute = FXCollections.observableArrayList();

    public MainWindowController2() throws RemoteException {
    }

    @FXML
    public void initialize() {
        System.out.println("Ajung aici");
        destinationColumn.setCellValueFactory(new PropertyValueFactory<Route,String>("destination"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<Route, LocalDateTime>("departureDateTime"));
        availableSeatsColumn.setCellValueFactory(new PropertyValueFactory<Route, Integer>("availableSeats"));

        // LocalDateTime formatter
        departureColumn.setCellFactory(col -> new TableCell<Route, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {

                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            }
        });

        // setting table's items
        tableRoutes.setItems(modelRoute);

        // adding filter's handle
        destinationTxt.textProperty().addListener(x-> {
            try {
                handleFilter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        datePicker.valueProperty().addListener(x-> {
            try {
                handleFilter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hourComboBox.valueProperty().addListener(x-> {
            try {
                handleFilter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        for(int i=0;i<24;i++)
            hourComboBox.getItems().add(i);

    }


    @Override
    public void notifyNewReservation(Reservation reservation) throws RemoteException {
        /*modelRoute.clear();
        modelRoute.setAll(getRouteList());*/
        for(int i=0;i<modelRoute.size();i++){
            if(modelRoute.get(i).getId().equals(reservation.getRoute().getId()))
                modelRoute.get(i).setAvailableSeats(modelRoute.get(i).getAvailableSeats()-1);
            System.out.println(modelRoute.get(i));
            System.out.println(reservation.getRoute());
        }
        tableRoutes.refresh();
    }

    private List<Route> getRouteList() throws Exception {
        return StreamSupport.stream(service.findAllRoutes().spliterator(),false)
                .collect(Collectors.toList());
    }

    public void setService(IService service) throws Exception {
        this.service = service;
    }

    public void setTableRoutes() throws Exception {
        modelRoute.setAll(getRouteList());
    }

    private void handleFilter() throws Exception {
        Predicate<Route> destinationPredicate = x->x.getDestination().toLowerCase(Locale.ROOT).startsWith(destinationTxt.getText());

        if(datePicker.valueProperty().getValue() != null && hourComboBox.getValue() == null){
            Predicate<Route> datePredicate = x -> x.getDepartureDateTime().isAfter(datePicker.getValue().atStartOfDay());

            modelRoute.setAll(getRouteList().stream().filter(destinationPredicate.and(datePredicate))
                    .collect(Collectors.toList()));
        }

        else if(hourComboBox.getValue()!=null) {
            Predicate<Route> dateTimePredicate = x -> x.getDepartureDateTime().isAfter(datePicker.getValue().atTime(LocalTime.of(hourComboBox.getValue(), 0)));

            modelRoute.setAll(getRouteList().stream().filter(destinationPredicate.and(dateTimePredicate))
                    .collect(Collectors.toList()));
        }
        else{
            modelRoute.setAll(getRouteList().stream().filter(destinationPredicate)
                    .collect(Collectors.toList()));
        }
    }

    public void handleLogout(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/gui/login.fxml"));
        Scene login = new Scene(loader.load());
        Stage primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        primaryStage.setScene(login);
        LoginController loginController = loader.getController();
        loginController.setService(service);
        service.logout(loggedUser,this);
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void handleReserve(ActionEvent event) throws Exception {
        Route route = tableRoutes.getSelectionModel().getSelectedItem();
        if(route !=null){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/gui/reservationScreen2.fxml"));
            Scene login = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(login);
            stage.show();
            ReservationScreenController2 resController = loader.getController();
            resController.setService(service);
            resController.setRoute(route);
        }
    }
}
