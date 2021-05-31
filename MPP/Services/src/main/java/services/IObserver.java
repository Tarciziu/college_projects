package services;

import model.Reservation;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {
    void notifyNewReservation(Reservation reservation) throws RemoteException;
}
