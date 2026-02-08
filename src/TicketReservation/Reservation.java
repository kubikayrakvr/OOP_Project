package TicketReservation;
import FlightManagement.*;

public class Reservation {
    private String reservationCode;
    private Flight flight;
    private Passenger passenger;
    private Seat seat;
    private String dateOfReservation;
    
    public Reservation(String reservationCode, Flight flight, Passenger passenger, Seat seat, String date){
        this.reservationCode = reservationCode;
        this.flight = flight;
        this.passenger = passenger;
        this.seat = seat;
        this.dateOfReservation = date;
    }

    // --- EXISTING GETTERS ---
    public Flight getFlight() { return this.flight; }
    public Seat getSeat() { return this.seat; }

    // --- NEW GETTERS REQUIRED FOR SERVICE ---
    public String getReservationCode() { return reservationCode; }
    public Passenger getPassenger() { return passenger; }
    public String getDateOfReservation() { return dateOfReservation; }
    
    @Override
    public String toString() {
        return "ResCode: " + reservationCode + 
               " | Flight: " + flight.getFlightNum() + 
               " | Seat: " + seat.getSeatNum() + 
               " | Pass: " + passenger.getName() + " " + passenger.getSurname();
    }
}