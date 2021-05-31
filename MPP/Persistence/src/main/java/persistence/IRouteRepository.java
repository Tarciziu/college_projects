package persistence;

import model.Route;

public interface IRouteRepository extends IRepository<Route>{
    Iterable<Route> findByDestination(String destination);
}
