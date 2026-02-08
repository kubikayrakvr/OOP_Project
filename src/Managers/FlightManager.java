package Managers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import FlightManagement.Flight;
import FlightManagement.Plane;
import FlightManagement.Route;

import Managers.PlaneManager;
import Managers.ReservationManager;
import Managers.SeatManager;
import Managers.TicketManager;
import TicketReservation.Reservation;
import TicketReservation.Ticket;
public class FlightManager {
    private ArrayList<Flight> flights;
    private int counter;
    private FlightSystemContext context;
    
	public FlightManager() {
		this.flights = new ArrayList<Flight>();
		this.counter = 1000;
	}
	
	public void setContext(FlightSystemContext context){
		this.context = context;
	}
    
	public boolean createFlight(String planeId, Route route, String date, int hour, int duration) {
        Plane plane = context.getPlaneManager().getPlane(planeId);
        if (plane == null) {
            System.out.println("Plane not found!");
            return false;
        }

        if (hour < 0 || hour > 23) {
            System.out.println("Invalid hour: " + hour + ". Must be 0-23.");
            return false;
        }

        int flightNum = counter++;
        return this.flights.add(new Flight(flightNum, plane, route, date, hour, duration));
    }
	
	public List<Flight> getAllFlights() {
	    return flights;
	}
	
	public void addLoadedFlight(Flight flight) {
        this.flights.add(flight);
        

        if (flight.getFlightNum() >= this.counter) {
            this.counter = flight.getFlightNum() + 1;
        }
    }
	
	public boolean updateFlight(int flightNum, String planeId, String dep, String arr, String date, int hour, int duration) {
        Flight flightToUpdate = this.getFlight(flightNum);
        if (flightToUpdate == null) {
            System.out.println("Flight not found!");
            return false;
        }

        Plane newPlane = context.getPlaneManager().getPlane(planeId);
        if (newPlane == null) {
            System.out.println("Plane not found: " + planeId);
            return false;
        }

        if (hour < 0 || hour > 23) {
            System.out.println("Invalid hour: " + hour);
            return false;
        }

        flightToUpdate.setPlane(newPlane);
        flightToUpdate.setRoute(new Route(dep, arr));
        flightToUpdate.setDate(date);
        flightToUpdate.setHour(hour);
        flightToUpdate.setDuration(duration);

        return true;
    }
	
	public boolean deleteFlight(int flightNum) {
		Flight flight = this.getFlight(flightNum);
        if (flight == null) {
            System.out.println("Uçuş bulunamadı !");
            return false;
        }
        context.getReservationManager().deleteReservationByFlight(flight);
        return flights.remove(flight);
	}
	
	public void removeAllFlights() {
        this.flights.clear();
    }
	
	public synchronized boolean deleteFlightByPlane(Plane plane) {
        if (plane == null) {
            return false;
        }

        List<Flight> flightsToRemove = new ArrayList<>();

        for (Flight flight : flights) {
            if (flight.getPlane().getPlaneId().equals(plane.getPlaneId())) {
                flightsToRemove.add(flight);
            }
        }

        boolean anyDeleted = false;

        for (Flight flight : flightsToRemove) {
            // Cancel reservations for this flight
            if (context.getReservationManager() != null) {
            	context.getReservationManager().deleteReservationByFlight(flight);
            }

            if (flights.remove(flight)) {
                anyDeleted = true;
            }
        }

        return anyDeleted;
    }
	
	public Flight getFlight(int flightNum) {
		for(Flight flight : this.flights) {
			if(flight.getFlightNum() == flightNum) {
				return flight;
			}
		}
		return null;
	}

	public List<Flight> searchFlights(String dep, String arr, String date) {
		List<Flight> results = new ArrayList<>();

	    for (Flight flight : flights) {
	        if (hasDepartureTimePassed(flight)) {
	            continue;
	        }

	        boolean matchDep =
	            dep == null || dep.isEmpty() ||
	            flight.getRoute().getDeparturePlace()
	                  .equalsIgnoreCase(dep);

	        boolean matchArr =
	            arr == null || arr.isEmpty() ||
	            flight.getRoute().getArrivalPlace()
	                  .equalsIgnoreCase(arr);

	        boolean matchDate =
	            date == null || date.isEmpty() ||
	            flight.getDate().equals(date);

	        if (matchDep && matchArr && matchDate) {
	            results.add(flight);
	        }
	    }

	    return results;
	}
	
	public boolean hasDepartureTimePassed(Flight flight) {

	    DateTimeFormatter formatter =
	        DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    LocalDate flightDate =
	        LocalDate.parse(flight.getDate(), formatter);

	    LocalTime departureTime =
	        LocalTime.of(flight.getHour(), 0);

	    LocalDateTime flightDateTime =
	        LocalDateTime.of(flightDate, departureTime);

	    return flightDateTime.isBefore(LocalDateTime.now());
	}


	// Sadece rotaya göre arama (tarih olmadan)
	public List<Flight> searchFlightsByRoute(String departurePlace, String arrivalPlace) {
	    return searchFlights(departurePlace, arrivalPlace, null);
	}

	// Sadece tarihe göre arama
	public List<Flight> searchFlightsByDate(String date) {
	    return searchFlights(null, null, date);
	}
	
}

