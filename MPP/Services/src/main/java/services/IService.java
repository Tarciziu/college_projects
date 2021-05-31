package services;
import model.Reservation;
import model.Route;
import model.User;

public interface IService {
    void login(User user, IObserver client) throws Exception;
    void logout(User user, IObserver client) throws Exception;
    Iterable<Route> findAllRoutes() throws Exception;
    Iterable<Reservation> findReservationByRoute(Route route) throws Exception;
    void updateReservation(Reservation reservation) throws Exception;
}
