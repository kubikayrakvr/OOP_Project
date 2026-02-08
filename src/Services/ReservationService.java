package Services;

import java.io.*;
import java.util.List;
import FlightManagement.Flight;
import FlightManagement.Plane;
import FlightManagement.Seat;
import Managers.FlightManager;
import Managers.ReservationManager;
import TicketReservation.Passenger;
import TicketReservation.Reservation;

public class ReservationService {

    private static final String FILE_PATH = "reservations.txt";

    static {
        FileHelper.ensureFileExists(FILE_PATH);
    }
    
    public static void loadReservations(ReservationManager resManager, FlightManager flightManager) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");

                if (parts.length == 7) {
                    String code = parts[0];
                    String flightNumStr = parts[1];
                    String name = parts[2];
                    String surname = parts[3];
                    String passId = parts[4];
                    String seatNum = parts[5];
                    String date = parts[6];

                    try {
                        int flightNum = Integer.parseInt(flightNumStr);
                        Flight flight = flightManager.getFlight(flightNum);
                        
                        if (flight != null) {
                            Seat seat = findSeatOnPlane(flight.getPlane(), seatNum);

                            if (seat != null) {
                                seat.setReserveStatus(true);

                                Passenger passenger = new Passenger(passId, name, surname, "N/A", "N/A", "N/A");

                                Reservation res = new Reservation(code, flight, passenger, seat, date);

                                resManager.addLoadedReservation(res);
                            }
                        }
                    } 
                    catch (NumberFormatException e) {
                        System.err.println("Skipping invalid flight number: " + flightNumStr);
                    }
                }
            }
            System.out.println("Reservations loaded successfully.");
        } 
        catch (IOException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
    }

    public static void saveReservations(ReservationManager resManager) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Reservation res : resManager.getAllReservations()) {
                
                String line = String.format("%s;%d;%s;%s;%s;%s;%s",
                        res.getReservationCode(),
                        res.getFlight().getFlightNum(),
                        res.getPassenger().getName(),
                        res.getPassenger().getSurname(),
                        res.getPassenger().getPassengerID(),
                        res.getSeat().getSeatNum(),
                        res.getDateOfReservation());
                
                bw.write(line);
                bw.newLine();
            }
        } 
        catch (IOException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }

    private static Seat findSeatOnPlane(Plane plane, String seatNum) {
        if (plane.getBussSeats() != null) {
            for (Seat[] row : plane.getBussSeats()) {
                for (Seat seat : row) {
                    if (seat != null && seat.getSeatNum().equals(seatNum)) return seat;
                }
            }
        }

        if (plane.getEconSeats() != null) {
            for (Seat[] row : plane.getEconSeats()) {
                for (Seat seat : row) {
                    if (seat != null && seat.getSeatNum().equals(seatNum)) return seat;
                }
            }
        }
        return null;
    }
}