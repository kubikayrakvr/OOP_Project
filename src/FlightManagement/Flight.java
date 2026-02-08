package FlightManagement;

import java.util.ArrayList;
import java.util.List;

public class Flight {
	private int flightNum;
	private Plane plane;
	private Route route;
	private String date;
	private int hour;
	private int duration;
	
	public Flight(int flightNum , Plane plane , Route route , String date , int hour , int duration){
		this.flightNum = flightNum;
		this.plane = plane;
		this.route = route;
		this.date = date;
		this.hour = hour;
		this.duration = duration;
	}

	public int getFlightNum() {
		return flightNum;
	}

	public Plane getPlane() {
		return plane;
	}

	public Route getRoute() {
		return route;
	}

	public String getDate() {
		return date;
	}

	public int getHour() {
		return hour;
	}

	public int getDuration() {
		return duration;
	}

	public void setPlane(Plane plane) {
		this.plane = plane;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Seat getRandomAvailableEconomySeat() {
	    Seat[][] seats = plane.getEconSeats();
	    List<Seat> availableSeats = new ArrayList<>();
	    
	    // Tüm boş koltukları topla
	    for (Seat[] row : seats) {
	        for (Seat seat : row) {
	            if (seat != null && !seat.getReserveStatus()) {
	                availableSeats.add(seat);
	            }
	        }
	    }
	    
	    if (availableSeats.isEmpty()) {
	        return null;
	    }
	    
	    // Random bir tanesini seç
	    int randomIndex = (int) (Math.random() * availableSeats.size());
	    return availableSeats.get(randomIndex);
	}
	@Override
    public String toString() {
        String r = (route != null) ? route.toString() : "Unknown Route";
        String p = (plane != null) ? plane.getPlaneId() : "No Plane";
        return String.format("[%d] %s | %s @ %02d:00 (%s)", flightNum, r, date, hour, p);
    }

}
