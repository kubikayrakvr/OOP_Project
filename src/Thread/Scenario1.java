package Thread;

import FlightManagement.*;
import Managers.*;
import TicketReservation.*;

public class Scenario1 {

    public static Plane start(boolean synchronizedMode) {
    	
        SeatManager seatManager = new SeatManager();

        int[][] layout = {
            {0, 0},
            {6, 30}
        };

        Seating seating = seatManager.createSeatings(layout);
        Plane plane = new Plane("PL-1", "A320", seating);
        Route route = new Route("IST", "ANK");
        Flight flight = new Flight(1001, plane, route, "2026-01-10", 10, 60);

        ReservationManager reservationManager = new ReservationManager();

        Thread[] threads = new Thread[90];

        for (int i = 0; i < 90; i++) {

            Passenger p = new Passenger(
                "P-" + i,
                "Name" + i,
                "Surname" + i,
                "mail@test.com",
                "05" + i,
                "Address"
            );

            threads[i] = new Thread(
                new PassengerTask(p, flight, reservationManager, synchronizedMode)
            );
            threads[i].start();
        }

        // Hepsini bekle
        for (int i = 0; i < 90; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int emptyEconomy = flight.getPlane().availableSeats()[1];
        int occupied = 180 - emptyEconomy;

        System.out.println("Synchronized = " + synchronizedMode);
        System.out.println("Occupied seats: " + occupied);
        
        return plane;
    }
}
