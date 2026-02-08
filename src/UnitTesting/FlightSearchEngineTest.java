package UnitTesting;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import FlightManagement.*;
import Managers.*;
import Services.PriceCalculationService;
class FlightSearchEngineTest {

	FlightSystemContext context1;
	FlightSystemContext context2;
	FlightSystemContext context3;
	FlightSystemContext context4;
	FlightSystemContext context5;
	
    private Plane plane;
    private Route istAnk;
    private Route istBer;

    private static final DateTimeFormatter FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

  

    @Test
    void shouldReturnCorrectFlightsByDepartureAndArrival() {
    	this.context1 = new FlightSystemContext();
    	context1.setContexts();

        int[][] seatPlan = {{0,0},{2,2}};
        Seating seating = context1.getSeatManager().createSeatings(seatPlan);

        istAnk = new Route("Istanbul", "Ankara");
        istBer = new Route("Istanbul", "Berlin");
        context1.getPlaneManager().createPlane("F35", seating);

    	context1.getFlightManager().createFlight("PL-1", istAnk, "01/06/2026", 12, 90);
    	    context1.getFlightManager().createFlight("PL-1", istBer, "01/06/2026", 10, 60);


    	    List<Flight> results =
    	        context1.getFlightManager().searchFlightsByRoute("Istanbul", "Ankara");

    	    assertEquals(1, results.size());
    	    assertEquals("Ankara" , results.get(0).getRoute().getArrivalPlace());
    	    
    }
    @Test
    void shouldReturnCorrectFlightsByDate() {
       	this.context2 = new FlightSystemContext();
    	context2.setContexts();

        int[][] seatPlan = {{0,0},{2,2}};
        Seating seating = context2.getSeatManager().createSeatings(seatPlan);

        istAnk = new Route("Istanbul", "Ankara");
        istBer = new Route("Istanbul", "Berlin");
    	context2.getPlaneManager().createPlane("F35", seating);

    	context2.getFlightManager().createFlight("PL-1", istAnk, "01/06/2026", 12, 90);
    	    context2.getFlightManager().createFlight("PL-1", istBer, "05/10/2026", 10, 60);


    	    List<Flight> results =
    	        context2.getFlightManager().searchFlightsByDate("05/10/2026");
    	    assertEquals(1, results.size());
    	    assertEquals("Berlin" , results.get(0).getRoute().getArrivalPlace());
    	    
    }
    @Test
    void shouldReturnCorrectFlightsByDateAndRoute() {
    	this.context3 = new FlightSystemContext();
    	context3.setContexts();

        int[][] seatPlan = {{0,0},{2,2}};
        Seating seating = context3.getSeatManager().createSeatings(seatPlan);

        istAnk = new Route("Istanbul", "Ankara");
        istBer = new Route("Istanbul", "Berlin");
    	context3.getPlaneManager().createPlane("F35", seating);

    	context3.getFlightManager().createFlight("PL-1", istAnk, "01/06/2026", 12, 90);
    	context3.getFlightManager().createFlight("PL-1", istAnk, "04/06/2026", 12, 90);
    	    context3.getFlightManager().createFlight("PL-1", istBer, "05/10/2026", 10, 60);


    	    List<Flight> results =
    	        context3.getFlightManager().searchFlights("Istanbul", "Ankara", "01/06/2026");
    	    assertEquals(1, results.size());
    	    assertEquals("Ankara" , results.get(0).getRoute().getArrivalPlace());
    	    assertEquals("01/06/2026" , results.get(0).getDate());

    	    
    }
    @Test
    void shouldEliminateFlightsWithPastDepartureTime() {
    	this.context4 = new FlightSystemContext();
    	context4.setContexts();

        int[][] seatPlan = {{0,0},{2,2}};
        Seating seating = context4.getSeatManager().createSeatings(seatPlan);

        istAnk = new Route("Istanbul", "Ankara");
        istBer = new Route("Istanbul", "Berlin");
    	context4.getPlaneManager().createPlane("F35", seating);

    	context4.getFlightManager().createFlight("PL-1", istAnk, "01/06/2024", 12, 90);
    	context4.getFlightManager().createFlight("PL-1", istAnk, "04/06/2023", 12, 90);
    	    context4.getFlightManager().createFlight("PL-1", istBer, "05/10/2026", 10, 60);


    	    List<Flight> results =
    	        context4.getFlightManager().searchFlightsByRoute("Istanbul", "Ankara");
    	    assertEquals(0, results.size());
    	    

    	    
    }
    @Test
    void shouldEliminateFlightsWithPastDepartureTime2() {
    	this.context5 = new FlightSystemContext();
    	context5.setContexts();

        int[][] seatPlan = {{0,0},{2,2}};
        Seating seating = context5.getSeatManager().createSeatings(seatPlan);

        istAnk = new Route("Istanbul", "Ankara");
        istBer = new Route("Istanbul", "Berlin");
    	context5.getPlaneManager().createPlane("F35", seating);

    	context5.getFlightManager().createFlight("PL-1", istAnk, "01/06/2024", 12, 90);
    	context5.getFlightManager().createFlight("PL-1", istAnk, "04/06/2023", 12, 90);
    	    context5.getFlightManager().createFlight("PL-1", istBer, "05/10/2002", 10, 60);


    	    List<Flight> results =
    	        context5.getFlightManager().searchFlightsByRoute("Istanbul", "Berlin");
    	    assertEquals(0, results.size());
    	    

    	    
    }
}
