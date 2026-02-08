package Services;

import java.io.*;
import Managers.TicketManager;
import Managers.ReservationManager;
import TicketReservation.Reservation;
import TicketReservation.Ticket;

public class TicketService {

    private static final String FILE_PATH = "tickets.txt";

    static {
        FileHelper.ensureFileExists(FILE_PATH);
    }
    
    public static void loadTickets(TicketManager ticketManager, ReservationManager resManager) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                
                if (parts.length == 4) {
                    String ticketId = parts[0];
                    String resCode = parts[1];
                    
                    try {
                        double price = Double.parseDouble(parts[2]);
                        double weight = Double.parseDouble(parts[3]);

                        Reservation res = resManager.getReservation(resCode);

                        if (res != null) {
                            Ticket t = new Ticket(ticketId, res, price, weight);
                            
                            ticketManager.addLoadedTicket(t);
                        }
                    } 
                    catch (NumberFormatException e) {
                        System.err.println("Skipping invalid ticket data: " + line);
                    }
                }
            }
            System.out.println("Tickets loaded.");
        } 
        catch (IOException e) {
            System.err.println("Error loading tickets: " + e.getMessage());
        }
    }

    public static void saveTickets(TicketManager ticketManager) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Ticket t : ticketManager.getAllTickets()) {
                String line = String.format("%s;%s;%.2f;%.2f",
                        t.getTicketId(),
                        t.getReservation().getReservationCode(),
                        t.getPrice(),
                        t.getBaggageWeight()
                );
                line = line.replace(",", "."); 
                
                bw.write(line);
                bw.newLine();
            }
        } 
        catch (IOException e) {
            System.err.println("Error saving tickets: " + e.getMessage());
        }
    }
}