package Thread;


import Managers.*;
import TicketReservation.*;
import FlightManagement.*;
public class PassengerTask implements Runnable {

    private Passenger passenger;
    private Flight flight;
    private ReservationManager manager;
    private boolean synchronizedMode;

    public PassengerTask(Passenger passenger,
                         Flight flight,
                         ReservationManager manager,
                         boolean synchronizedMode) {
        this.passenger = passenger;
        this.flight = flight;
        this.manager = manager;
        this.synchronizedMode = synchronizedMode;
    }

    @Override
    public void run() {
    	    	 if(this.synchronizedMode) {
         	manager.createRandomReservationSync(flight, passenger);
         }
    	 else {
    		 manager.createRandomReservationAsync(flight, passenger);
    	 }
    }


}
