package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Route implements Entity, Serializable {
    private String destination;
    private LocalDateTime departureDateTime;
    private Integer availableSeats;
    private Long id;

    public Route(String destination, LocalDateTime departureDateTime) {
        this.destination = destination;
        this.departureDateTime = departureDateTime;
        this.availableSeats = 18;
    }

    public Route(String destination, LocalDateTime departureDateTime, int availableSeats) {
        this.destination = destination;
        this.departureDateTime = departureDateTime;
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "Route{" + "id='" + getId() + '\'' +
                "destination='" + destination + '\'' +
                ", departureDateTime=" + departureDateTime +
                ", availableSeats=" + availableSeats +
                '}';
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
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
