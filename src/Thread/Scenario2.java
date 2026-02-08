package Thread;

import FlightManagement.*;
import Managers.FlightSystemContext;
import TicketReservation.*;
import javafx.application.Platform;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Scenario2 implements Runnable {

    private final Random random = new Random();
    private FlightSystemContext context;
    // Simulation Constants
    private final int planeCount = 10000;
    private final int flightCount = 500000;
    private final int passengerCount = 3000000;

    private final String[] airports = {
        "IST", "ANK", "IZM", "ADB", "ESB",
        "AYT", "DLM", "BJV", "TZX", "VAN"
    };

    private Passenger[] passengers = new Passenger[passengerCount];

    private Consumer<String> onReportReady;

    public Scenario2(Consumer<String> onReportReady) {
        this.onReportReady = onReportReady;

        this.context = new FlightSystemContext(); 
        this.context.setContexts();
    }

    @Override
    public void run() {
        try {
            String report = startScenario();

            Platform.runLater(() -> {
                if (onReportReady != null) onReportReady.accept(report);
            });

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                if (onReportReady != null) onReportReady.accept("Error occurred during simulation: " + e.getMessage());
            });
        }
    }


    private String startScenario() {

        for (int i = 0; i < passengerCount; i++) {
            passengers[i] = new Passenger("P" + i, "Name" + i, "Surname" + i, i + "@gmail.com", "05" + (10000000 + i), "City-" + (i % 81));
        }

        // 2. Create Planes
        for (int i = 0; i < planeCount; i++) {
            int[][] seatPlan = {{random.nextInt(2) + 2, random.nextInt(3) + 2}, {random.nextInt(3) + 4, random.nextInt(15) + 15}};
            Seating s = context.getSeatManager().createSeatings(seatPlan);
            context.getPlaneManager().createPlane("PL" + i, s);
        }
        List<String> planeIds = context.getPlaneManager().getAllPlaneIds();


        for (int i = 0; i < flightCount; i++) {
            String planeId = planeIds.get(random.nextInt(planeIds.size()));
            String dep, arr;
            do {
                dep = airports[random.nextInt(airports.length)];
                arr = airports[random.nextInt(airports.length)];
            } while (dep.equals(arr));

            context.getFlightManager().createFlight(planeId, new Route(dep, arr), LocalDate.now().plusDays(random.nextInt(30)).toString(), random.nextInt(24), random.nextInt(150) + 30);
        }
        List<Flight> flights = context.getFlightManager().getAllFlights();


        for (int i = 0; i < passengerCount; i++) {
            Flight flight = flights.get(random.nextInt(flights.size()));
            Passenger passenger = passengers[random.nextInt(passengerCount)];
            Seat seat = flight.getRandomAvailableEconomySeat();

            if (seat != null) {
                context.getReservationManager().createReservation(flight, passenger, seat);
            }
        }


        int ticketCreated = 0;
        for (Reservation r : context.getReservationManager().getAllReservations()) {
            double baggage = Math.random() < 0.5 ? 0 : (Math.random() * 20 + 5);
            if (baggage == 0) context.getTicketManager().createTicket(r);
            else context.getTicketManager().createTicket(r, baggage);

            if (++ticketCreated >= passengerCount) break;
        }

        return buildReport(ticketCreated);
    }

    private String buildReport(int totalTickets) {
        int totalPlanes = context.getPlaneManager().getAllPlaneIds().size();
        int totalFlights = context.getFlightManager().getAllFlights().size();
        int totalReservations = 0;
        for (Reservation r : context.getReservationManager().getAllReservations()) {
            totalReservations++;
        }
        
        double totalRevenue = 0;
        for (Ticket t : context.getTicketManager().getAllTickets()) totalRevenue += t.getPrice();

        StringBuilder sb = new StringBuilder();
        sb.append("\n========== SIMULATION REPORT ==========\n");
        sb.append("Total Planes        : ").append(totalPlanes).append("\n");
        sb.append("Total Flights       : ").append(totalFlights).append("\n");
        sb.append("Total Passengers    : ").append(passengerCount).append("\n");
        sb.append("Total Reservations  : ").append(totalReservations).append("\n");
        sb.append("Total Tickets       : ").append(totalTickets).append("\n");
        sb.append(String.format("Total Revenue (TL)  : %.2f\n", totalRevenue));
        sb.append("========================================");
        return sb.toString();
    }
}