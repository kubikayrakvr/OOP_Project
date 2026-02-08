package FlightManagement;

public class Seat {
	private String seatNum;
	private FlightClass flightClass;
	private double price;
	private boolean reserveStatus;
	
	public Seat(String seatNum , FlightClass flightClass){
		this.seatNum = seatNum;
		this.flightClass = flightClass;
		this.price = this.flightClass.getPricing();
		this.reserveStatus = false;
	}
	public boolean getReserveStatus() {
		return this.reserveStatus;
	}
	
	@Override
	public String toString() {
	    return "Seat Information\n" +
	           "----------------\n" +
	           "Seat Number : " + seatNum + "\n" +
	           "Class       : " + flightClass + "\n" +
	           "Price       : " + price + " TL\n" +
	           "Status      : " + (reserveStatus ? "Reserved" : "Available");
	}
	
	public String getSeatNum() {
		return seatNum;
	}
	public FlightClass getFlightClass() {
		return flightClass;
	}
	public double getPrice() {
		return price;
	}
	public void setSeatNum(String seatNum) {
		this.seatNum = seatNum;
	}
	public void setFlightClass(FlightClass flightClass) {
		this.flightClass = flightClass;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public void setReserveStatus(boolean reserveStatus) {
		this.reserveStatus = reserveStatus;
	}

	
}
