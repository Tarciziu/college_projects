package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Reservation;
import model.Route;
import services.IService;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ReservationScreenController2 {
    private IService service;
    private Route route;
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField seatTxt;


    @FXML
    TableColumn<Reservation, Integer> seatNumberColumn;
    @FXML
    TableColumn<Reservation, String> clientNameColumn;
    @FXML
    TableView<Reservation> tableSeats;
    ObservableList<Reservation> modelSeats = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<Reservation,String>("clientName"));
        seatNumberColumn.setCellValueFactory(new PropertyValueFactory<Reservation,Integer>("reservedSeat"));
        tableSeats.setItems(modelSeats);
    }

    public void setService(IService service) {
        this.service = service;
    }

    public void handleReserve(ActionEvent actionEvent) throws Exception {
        Reservation res=null;
        for(Reservation x: service.findReservationByRoute(route)){
            if(x.getReservedSeat()==Integer.parseInt(seatTxt.getText()))
                res=x;
        }
        if (!res.getClientName().equals("-")) {
            JOptionPane.showMessageDialog(null, "Seat already reserved",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{
            res.setClientName(nameTxt.getText());
            service.updateReservation(res);
            Stage stage = (Stage) nameTxt.getScene().getWindow();
            stage.close();
        }
    }

    private List<Reservation> getReservationByRoute(Route route) throws Exception {
        return StreamSupport.stream(service.findReservationByRoute(route).spliterator(),false)
                .collect(Collectors.toList());
    }

    public void setRoute(Route route) throws Exception {
        this.route = route;
        modelSeats.setAll(getReservationByRoute(route));
    }
}
