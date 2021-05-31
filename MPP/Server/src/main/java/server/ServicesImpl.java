package server;

import model.Reservation;
import model.Route;
import model.User;
import persistence.IReservationRepository;
import persistence.IRouteRepository;
import persistence.IUserRepository;
import services.IObserver;
import services.IService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesImpl implements IService {
    private IUserRepository userRepository;
    private IRouteRepository routeRepository;
    private IReservationRepository reservationRepository;
    private Map<String, IObserver> loggedClients;

    private final int defaultThreadsNo=5;

    public ServicesImpl(IUserRepository uRepo, IRouteRepository rouRepo, IReservationRepository resRepo) {

        userRepository = uRepo;
        routeRepository = rouRepo;
        reservationRepository = resRepo;
        loggedClients=new ConcurrentHashMap<>();;
    }

    @Override
    public synchronized void login(User user, IObserver client) throws Exception {
        User userR=userRepository.findByUsername(user.getUsername());
        if (userR!=null){
            if(loggedClients.get(user.getUsername())!=null)
                throw new Exception("User already logged in.");
            System.out.println(user);
            try{
            loggedClients.put(userR.getUsername(), client);}
            catch(Exception ex){
                System.out.println(ex);
            }
            System.out.println("server");
        }else
            throw new Exception("Authentication failed.");

    }

    @Override
    public synchronized void logout(User user, IObserver client) throws Exception {
        IObserver localClient = loggedClients.remove(user.getUsername());
        if (localClient==null)
            throw new Exception("User " + user.getUsername() + " is not logged in.");
    }

    @Override
    public synchronized Iterable<Route> findAllRoutes() {
        return routeRepository.findAll();
    }

    @Override
    public Iterable<Reservation> findReservationByRoute(Route route) {
        return reservationRepository.findByRoute(route);
    }

    @Override
    public void updateReservation(Reservation reservation) throws Exception {
        Route route = routeRepository.findOne(reservation.getRoute().getId());
        if(reservation.getClientName().equals("-")){
            route.setAvailableSeats(route.getAvailableSeats() + 1);
        }
        else{
            route.setAvailableSeats(route.getAvailableSeats() - 1);
        }
        routeRepository.update(route);
        reservationRepository.update(reservation);
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver obs:loggedClients.values()){
           executor.execute(()-> {
               try {
                   obs.notifyNewReservation(reservation);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           });
        }
        executor.shutdown();
    }
}
