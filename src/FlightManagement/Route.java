package FlightManagement;

public class Route {
	private String departurePlace;
	private String arrivalPlace;
	
	public Route(String departure , String arrival){
		this.departurePlace = departure;
		this.arrivalPlace = arrival;
	}

	public String getDeparturePlace() {
		return departurePlace;
	}

	public String getArrivalPlace() {
		return arrivalPlace;
	}

	public void setDeparturePlace(String departurePlace) {
		this.departurePlace = departurePlace;
	}

	public void setArrivalPlace(String arrivalPlace) {
		this.arrivalPlace = arrivalPlace;
	}
	
    @Override
    public String toString() {
        return departurePlace + " -> " + arrivalPlace;
    }
	
}
