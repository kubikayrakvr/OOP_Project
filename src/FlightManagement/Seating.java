package FlightManagement;

public class Seating {
	private Seat[][] bussSeats;
	private Seat[][] econSeats;
	
	public Seating(Seat[][] bussSeats , Seat[][] econSeats) {
		this.bussSeats = bussSeats;
		this.econSeats = econSeats;
	}

	public Seat[][] getBussSeats() {
		return bussSeats;
	}

	public Seat[][] getEconSeats() {
		return econSeats;
	}
}
