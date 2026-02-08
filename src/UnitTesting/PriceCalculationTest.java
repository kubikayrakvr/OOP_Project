package UnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import Managers.CalculatePrice;
import Managers.FlightSystemContext;
import Services.PriceCalculationService;
import FlightManagement.*;
import TicketReservation.*;

public class PriceCalculationTest {
	@Test
    void economyPriceCalculation() {
        CalculatePrice calculatePrice = new CalculatePrice(500, 50, 15, 2.5, 1.5);

        Seat seat = new Seat("A1", FlightClass.ECONOMY);

        Plane plane = new Plane("PL1", "A320", new Seating(null, new Seat[][]{{seat}}));

        Flight flight = new Flight(
            1, plane, new Route("IST", "ANK"), "2026-01-01", 10, 60
        );

        Passenger p = new Passenger("P1", "A", "B", "m", "p", "c");
        Reservation r = new Reservation("R1", flight, p, seat, "2026");

        double price = calculatePrice.priceCalculation(r, 10);

        double expected = 
              500.0       
            + (60 * 1.5)       
            + (10 * 15.0)      
            + 50.0;        

        assertEquals(expected, price, 0.01); 
    }
	@Test
    void businessPriceCalculation() {
        CalculatePrice calculatePrice = new CalculatePrice(1200, 50, 15, 2.0, 1.5);

        Seat seat = new Seat("B1", FlightClass.BUSINESS);
        Plane plane = new Plane("PL2", "A321", new Seating(new Seat[][]{{seat}}, null));
        Flight flight = new Flight(2, plane, new Route("IST", "IZM"), "2026-01-02", 12, 90);
        Passenger p = new Passenger("P2", "X", "Y", "m", "p", "c");
        Reservation r = new Reservation("R2", flight, p, seat, "2026");

        double price = calculatePrice.priceCalculation(r, 0);

        double subtotal = 1200.0 + (90 * 1.5); 
        double expected = (subtotal * 2.0) + 50.0;

        assertEquals(expected, price, 0.01);
    }

    @Test
    void economyWithoutBaggage() {
        CalculatePrice calculatePrice = new CalculatePrice(400, 50, 15, 2.5, 1.5);

        Seat seat = new Seat("A5", FlightClass.ECONOMY);
        Plane plane = new Plane("PL3", "A320", new Seating(null, new Seat[][]{{seat}}));
        Flight flight = new Flight(3, plane, new Route("IST", "ANK"), "2026-01-03", 9, 50);
        Passenger p = new Passenger("P3", "A", "B", "m", "p", "c");
        Reservation r = new Reservation("R3", flight, p, seat, "2026");

        double price = calculatePrice.priceCalculation(r, 0);

        double expected = 400.0 + (50 * 1.5) + 50.0; 

        assertEquals(expected, price, 0.01);
    }

    @Test
    void businessWithBaggage() {
        CalculatePrice calculatePrice = new CalculatePrice(1500, 50, 20, 1.5, 1.5);

        Seat seat = new Seat("B2", FlightClass.BUSINESS);
        Plane plane = new Plane("PL4", "A321", new Seating(new Seat[][]{{seat}}, null));
        Flight flight = new Flight(4, plane, new Route("ANK", "IZM"), "2026-01-04", 14, 80);
        Passenger p = new Passenger("P4", "C", "D", "m", "p", "c");
        Reservation r = new Reservation("R4", flight, p, seat, "2026");

        double price = calculatePrice.priceCalculation(r, 15);

        double subtotal = 1500.0 + (80 * 1.5); 
        double expected = (subtotal * 1.5) + (15 * 20.0) + 50.0; 

        assertEquals(expected, price, 0.01);
    }

    @Test
    void zeroDurationFlightPrice() {
        CalculatePrice calculatePrice = new CalculatePrice(300, 50, 20, 2.0, 1.5);

        Seat seat = new Seat("A10", FlightClass.ECONOMY);
        Plane plane = new Plane("PL5", "A320", new Seating(null, new Seat[][]{{seat}}));
        Flight flight = new Flight(5, plane, new Route("IZM", "IST"), "2026-01-05", 16, 0);
        Passenger p = new Passenger("P5", "E", "F", "m", "p", "c");
        Reservation r = new Reservation("R5", flight, p, seat, "2026");

        double price = calculatePrice.priceCalculation(r, 5);

        double expected = 300.0 + 0.0 + (5 * 20.0) + 50.0; 

        assertEquals(expected, price, 0.01);
    }
}
