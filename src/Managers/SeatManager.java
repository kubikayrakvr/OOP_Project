package Managers;
import FlightManagement.*;
import Managers.PlaneManager;
import Managers.ReservationManager;
import Managers.FlightManager;
import Managers.TicketManager;

public class SeatManager {
    private FlightSystemContext context;
    
	public SeatManager() {}
	
	public void setContext(FlightSystemContext context){
		this.context = context;
	}
	
	public Seating createSeatings(int[][] seatArrangement) {
        // [0][0] Business Column
        // [0][1] Business Row
        // [1][0] Economy Column
        // [1][1] Economy Row

        int businessCol = seatArrangement[0][0];
        int businessRow = seatArrangement[0][1];
        int econCol = seatArrangement[1][0];
        int econRow = seatArrangement[1][1];

        Seat[][] bussSeats = new Seat[businessRow][businessCol];
        Seat[][] econSeats = new Seat[econRow][econCol];

        for (int row = 0; row < businessRow; row++) {
            for (int col = 0; col < businessCol; col++) {

                char seatLetter = (char) ('A' + col);      // A, B, C
                int seatRowNum = row + 1;                  // 1, 2, 3

                String seatNum = seatLetter + String.valueOf(seatRowNum);

                bussSeats[row][col] = new Seat(seatNum, FlightClass.BUSINESS);
            }
        }

        int econRowStart = businessRow + 1;

        for (int row = 0; row < econRow; row++) {
            for (int col = 0; col < econCol; col++) {

                char seatLetter = (char) ('A' + col);      // A, B, C, D, E...
                int seatRowNum = econRowStart + row;       // 6, 7, 8...

                String seatNum = seatLetter + String.valueOf(seatRowNum);

                econSeats[row][col] = new Seat(seatNum, FlightClass.ECONOMY);
            }
        }

        return new Seating(bussSeats, econSeats);
    }
	
	public int[] calculateAvailableSeats(Plane plane){
		return plane.availableSeats();
	}
}
