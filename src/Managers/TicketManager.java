package Managers;

import java.util.HashMap;
import java.util.Map;

import TicketReservation.*;
import java.util.Iterator;

public class TicketManager {
	private HashMap<String, Ticket> tickets;
    private int ticketCounter;
    private FlightSystemContext context;
    
    public TicketManager() {
        this.tickets = new HashMap<String,Ticket>();
        this.ticketCounter = 0;
    }
    
	public void setContext(FlightSystemContext context){
		this.context = context;
	}
    
    public boolean createTicket(Reservation reservation) {
    	if(reservation == null) {
    		System.out.println("Geçersiz reservation");
    		return false;
    	}
    	String ticketID = "TCKT-" + (++ticketCounter);

    	Ticket newTicket = new Ticket(ticketID , reservation, context.getCalculatePrice());
    	 tickets.put(ticketID, newTicket);
    	 return true;
    }
    public boolean createTicket(Reservation reservation , double baggageWeight) {
    	if(reservation == null) {
    		System.out.println("Geçersiz reservation");
    		return false;
    	}
    	if(baggageWeight <= 0) {
    		System.out.println("Geçersiz bagaj ağırlığı");
    		return false;
    	}
    	String ticketID = "TCKT-" + (++ticketCounter);

    	Ticket newTicket = new Ticket(ticketID , reservation,baggageWeight,context.getCalculatePrice());
    	 tickets.put(ticketID, newTicket);
    	 return true;
    }

    public void addLoadedTicket(Ticket ticket) {
        tickets.put(ticket.getTicketId(), ticket);
        
        try {
            String[] parts = ticket.getTicketId().split("-");
            if (parts.length == 2) {
                int idNum = Integer.parseInt(parts[1]);
                if (idNum >= ticketCounter) {
                    ticketCounter = idNum;
                }
            }
        } 
        catch (NumberFormatException e) {
        }
    }
    
    public boolean deleteTicket(String ticketID) {
		Ticket ticket = this.getTicket(ticketID);
        if (ticket == null) {
            System.out.println("Bilet bulunamadı !");
            return false;
        }
        tickets.remove(ticketID);
        return true;
    }
    
    public void removeAllTickets() {
        this.tickets.clear();
    }
    
    public Ticket getTicket(String ticketID) {
    	return tickets.get(ticketID);
    }
    
    public synchronized boolean deleteTicketByReservation(Reservation reservation) {
    	if (reservation == null) {
            return false;
    	}
    	
        boolean anyDeleted = false;

        Iterator<Map.Entry<String, Ticket>> iterator =
                tickets.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Ticket> entry = iterator.next();
            Ticket ticket = entry.getValue();

            if (ticket.getReservation().equals(reservation)) {
                iterator.remove();
                anyDeleted = true;
            }
        }

        return anyDeleted;
    }
    public Iterable<Ticket> getAllTickets() {
        return tickets.values();
    }
}
