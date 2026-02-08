package FlightManagement;

public class Plane {
	private String planeId;
	private String planeModel;
	private int capacity;
	private Seat[][] econSeats;
	private Seat[][] bussSeats;
	
	public Plane(String planeId , String planeModel , Seating seatings){
		this.planeId = planeId;
		this.planeModel = planeModel;
		this.econSeats = seatings.getEconSeats();
		this.bussSeats = seatings.getBussSeats();
		int tempCapacity = this.availableSeats()[0] + this.availableSeats()[1];
		this.capacity = tempCapacity;
	}
	
	
	public void setPlaneModel(String planeModel) {
		this.planeModel = planeModel;
	}
	

	public void setSeatMatrix(Seating seatings) {
		this.econSeats = seatings.getEconSeats();
		this.bussSeats = seatings.getBussSeats();
	}
	public boolean hasAnyReservedSeat() {
	    return hasReservedInMatrix(econSeats) ||
	           hasReservedInMatrix(bussSeats);
	}
	private boolean hasReservedInMatrix(Seat[][] seats) {
	    if (seats == null)
	        return false;

	    for (Seat[] row : seats) {
	        for (Seat seat : row) {
	            if (seat != null && seat.getReserveStatus()) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	public int[] availableSeats() {

	    
	    int[] availableSeatData = new int[2];

	    int businessAvailable = 0;
	    int economyAvailable = 0;

	    // Business
	    if (bussSeats != null) {
	        for (Seat[] row : bussSeats) {
	            for (Seat seat : row) {
	                if (seat != null && !seat.getReserveStatus()) {
	                    businessAvailable++;
	                }
	            }
	        }
	    }

	    // Economy
	    if (econSeats != null) {
	        for (Seat[] row : econSeats) {
	            for (Seat seat : row) {
	                if (seat != null && !seat.getReserveStatus()) {
	                    economyAvailable++;
	                }
	            }
	        }
	    }

	    availableSeatData[0]= businessAvailable;
	    availableSeatData[1] = economyAvailable;

	    return availableSeatData;
	}
	
	
	public int getTotalSeatCapacity() {
	    int total = 0;

	    if (bussSeats != null && bussSeats.length > 0 && bussSeats[0].length > 0) {
	        total += bussSeats.length * bussSeats[0].length;
	    }

	    if (econSeats != null && econSeats.length > 0 && econSeats[0].length > 0) {
	        total += econSeats.length * econSeats[0].length;
	    }

	    return total;
	}
	

	@Override
    public String toString() {
        return "Plane {" +
               "planeId='" + planeId + '\'' +
               ", planeModel='" + planeModel + '\'' +
               ", capacity=" + capacity +
               '}';
    }


	public Seat[][] getEconSeats() {
		return this.econSeats;
	}
	
	public String getPlaneId() {
	    return planeId;
	}

	public String getPlaneModel() {
	    return planeModel;
	}

	public Seat[][] getBussSeats() {
	    return bussSeats;
	}
}
