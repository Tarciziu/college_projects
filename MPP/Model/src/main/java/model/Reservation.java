package model;

import java.io.Serializable;

public class Reservation implements Entity, Serializable {
    private String clientName;
    private Integer reservedSeat;
    private Route route;
    private Long id;

    public Reservation(String clientName, Integer reservedSeat, Route route) {
        this.clientName = clientName;
        this.reservedSeat = reservedSeat;
        this.route = route;
    }

    public Reservation(Integer reservedSeat, Route route) {
        this.clientName = "null";
        this.reservedSeat = reservedSeat;
        this.route = route;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public Integer getReservedSeat() {
        return reservedSeat;
    }

    public Route getRoute() {
        return route;
    }

    @Override
    public String toString() {
        return "Reservation{" + "id='" + getId() + '\'' +
                "clientName='" + clientName + '\'' +
                ", reservedSeat=" + reservedSeat +
                ", route=" + route +
                '}';
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
