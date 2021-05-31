package persistence;


import model.Reservation;
import model.Route;

public interface IReservationRepository extends IRepository<Reservation> {
    Iterable<Reservation> findByClientName(String clientName);

    Iterable<Reservation> findByRoute(Route route);
}
