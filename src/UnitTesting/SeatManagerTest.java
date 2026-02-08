package UnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import FlightManagement.*;
import Managers.*;
import Services.PriceCalculationService;
import TicketReservation.*;

public class SeatManagerTest {
	FlightSystemContext context1;
	FlightSystemContext context2;
	
    @Test
    void TestNonExistentSeat() {
    	this.context1 = new FlightSystemContext();
    	context1.setContexts();

        int[][] seatPlan = {{2,2},{2,2}};
        Seating seating = context1.getSeatManager().createSeatings(seatPlan);

        Plane plane = new Plane("PL1", "Boeing", seating);

        Flight flight = new Flight(
            1,
            plane,
            new Route("IST", "ANK"),
            "2026/01/01",
            10,
            60
        );

        Passenger passenger = new Passenger(
            "P1", "A", "B", "m", "p", "c"
        );

        Seat fakeSeat = new Seat("Z99", FlightClass.BUSINESS);

        assertThrows(
        		IllegalArgumentException.class,
            () -> context1.getReservationManager().createReservation(
                flight,
                passenger,
                fakeSeat
            )
        );
    } 
    @Test
    void TestNonExistentSeat2() {
    	this.context2 = new FlightSystemContext();
    	context2.setContexts();

        int[][] seatPlan = {{3,4},{5,5}};
        Seating seating = context2.getSeatManager().createSeatings(seatPlan);

        Plane plane = new Plane("PL1", "Boeing", seating);

        Flight flight = new Flight(
            1,
            plane,
            new Route("ABD", "IRQ"),
            "2001/11/09",
            10,
            60
        );

        Passenger passenger = new Passenger(
            "P1", "A", "B", "m", "p", "c"
        );

        Seat fakeSeat = new Seat("B100", FlightClass.BUSINESS);

        assertThrows(
        		IllegalArgumentException.class,
            () -> context2.getReservationManager().createReservation(
                flight,
                passenger,
                fakeSeat
            )
        );
    } 
    @Test
    void TestEmptySeatsDecreaseAfterReservation() {

        SeatManager seatManager = new SeatManager();
        ReservationManager reservationManager = new ReservationManager();

        
        int[][] seatPlan = {{0,0},{2,2}}; // TOPLAM 4 YER VAR
        Seating seating = seatManager.createSeatings(seatPlan);

        Plane plane = new Plane("PL2", "Airbus", seating);

        Flight flight = new Flight(
            2,
            plane,
            new Route("IST", "IZM"),
            "2026/01/02",
            12,
            90
        );

        Passenger passenger = new Passenger("P2","X","Y","m","p","c");

        int[] beforeArray = seatManager.calculateAvailableSeats(plane);
        int before = beforeArray[0] + beforeArray[1];

        Seat seatToReserve = flight.getRandomAvailableEconomySeat();

        reservationManager.createReservation(
            flight,
            passenger,
            seatToReserve
        ); // TOPLAM 4 YER VARDI 1 TANESİ REZERVE OLDU 3 KALDI


        int[] afterArray = seatManager.calculateAvailableSeats(plane);
        int after = afterArray[0] + afterArray[1];

        assertEquals(3, after);
    }
    @Test
    void TestEmptySeatsDecreaseAfterReservation2() {

        SeatManager seatManager = new SeatManager();
        ReservationManager reservationManager = new ReservationManager();

        
        int[][] seatPlan = {{0,0},{5,10}}; // TOPLAM 50 YER VAR
        Seating seating = seatManager.createSeatings(seatPlan);

        Plane plane = new Plane("PL2", "Airbus", seating);

        Flight flight = new Flight(
            2,
            plane,
            new Route("IST", "IZM"),
            "2026/01/02",
            12,
            90
        );

        Passenger passenger = new Passenger("P2","X","Y","m","p","c");

        int[] beforeArray = seatManager.calculateAvailableSeats(plane);
        int before = beforeArray[0] + beforeArray[1];

        for(int i = 0; i< 5 ; i++) {
        	Seat seatToReserve = flight.getRandomAvailableEconomySeat();

            reservationManager.createReservation(
                flight,
                passenger,
                seatToReserve
            ); 

        } // 5 kere rezervasyon yapıldı 50-5 = 45
        

        int[] afterArray = seatManager.calculateAvailableSeats(plane);
        int after = afterArray[0] + afterArray[1];

        assertEquals(45, after);
    }
    @Test
    void TestEmptySeatsDecreaseAfterReservation3() {

        SeatManager seatManager = new SeatManager();
        ReservationManager reservationManager = new ReservationManager();

        
        int[][] seatPlan = {{0,0},{5,10}}; // TOPLAM 50 YER VAR
        Seating seating = seatManager.createSeatings(seatPlan);

        Plane plane = new Plane("PL2", "Airbus", seating);

        Flight flight = new Flight(
            2,
            plane,
            new Route("IST", "IZM"),
            "2026/01/02",
            12,
            90
        );

        Passenger passenger = new Passenger("P2","X","Y","m","p","c");

        int[] beforeArray = seatManager.calculateAvailableSeats(plane);
        int before = beforeArray[0] + beforeArray[1];

        for(int i = 0; i< 10 ; i++) {
        	Seat seatToReserve = flight.getRandomAvailableEconomySeat();

            reservationManager.createReservation(
                flight,
                passenger,
                seatToReserve
            ); 

        } // 10 kere rezervasyon yapıldı 50-10 = 40
        

        int[] afterArray = seatManager.calculateAvailableSeats(plane);
        int after = afterArray[0] + afterArray[1];

        assertEquals(40, after);
    }
}
