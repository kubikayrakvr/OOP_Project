package Services;

import java.io.*;
import FlightManagement.Flight;
import FlightManagement.Plane;
import FlightManagement.Route;
import Managers.FlightManager;
import Managers.PlaneManager;

public class FlightService {

    private static final String FILE_PATH = "flights.txt";
    
    static {
        FileHelper.ensureFileExists(FILE_PATH);
    }
    
    public static void loadFlights(FlightManager flightManager, PlaneManager planeManager) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    try {
                        int flightNum = Integer.parseInt(parts[0]);
                        String planeId = parts[1];
                        String departure = parts[2];
                        String arrival = parts[3];
                        String date = parts[4];
                        int hour = Integer.parseInt(parts[5]);
                        int duration = Integer.parseInt(parts[6]);

                        Plane plane = planeManager.getPlane(planeId);

                        if (plane != null) {
                            Route route = new Route(departure, arrival);

                            Flight flight = new Flight(flightNum, plane, route, date, hour, duration);

                            flightManager.addLoadedFlight(flight);
                        } else {
                            System.err.println("Warning: Skipping flight " + flightNum + " because Plane " + planeId + " was not found.");
                        }

                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid flight record: " + line);
                    }
                }
            }
            System.out.println("Flights loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading flights: " + e.getMessage());
        }
    }

    public static void saveFlights(FlightManager flightManager) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Flight flight : flightManager.getAllFlights()) {
                
                String dep = "Unknown";
                String arr = "Unknown";
                if (flight.getRoute() != null) {
                    dep = flight.getRoute().getDeparturePlace(); 
                    arr = flight.getRoute().getArrivalPlace();
                }

                String line = String.format("%d;%s;%s;%s;%s;%d;%d",
                        flight.getFlightNum(),
                        flight.getPlane().getPlaneId(),
                        dep,
                        arr,
                        flight.getDate(),
                        flight.getHour(),
                        flight.getDuration()
                );

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving flights: " + e.getMessage());
        }
    }
}