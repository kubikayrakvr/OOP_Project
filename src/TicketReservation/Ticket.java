package TicketReservation;

import Managers.*;

public class Ticket {
    private String ticketId;
    private Reservation reservation;
    private double price;
    private Baggage baggageAllowance;


    public Ticket(String ticketId, Reservation reservation, CalculatePrice calculator) {
        this.ticketId = ticketId;
        this.reservation = reservation;
        this.baggageAllowance = null;
        this.price = calculator.priceCalculation(reservation, 0);
    }

    public Ticket(String ticketId, Reservation reservation, double baggageWeight , CalculatePrice calculator) {
        this.ticketId = ticketId;
        this.reservation = reservation;
        this.baggageAllowance = new Baggage(baggageWeight);
        this.price = calculator.priceCalculation(reservation, baggageWeight);
    }
    
    public Ticket(String ticketId, Reservation reservation, double price, double baggageWeight) {
        this.ticketId = ticketId;
        this.reservation = reservation;
        this.price = price;
        if (baggageWeight > 0) {
            this.baggageAllowance = new Baggage(baggageWeight);
        } else {
            this.baggageAllowance = null;
        }
    }
	
    public class Baggage{
        private double weight;
        Baggage(double weight){
            this.weight = weight;
        }
        @Override
        public String toString() { return weight + " kg"; }
    }
	
    public String getTicketId() { return ticketId; }
    public Reservation getReservation() { return reservation; }
    public double getPrice() { return price; }
    
    public double getBaggageWeight() {
        return baggageAllowance == null ? 0 : baggageAllowance.weight;
    }

    @Override
    public String toString() {
        return String.format("[%s] Res: %s | Price: %.2f TL | Bag: %.1f kg", 
                ticketId, reservation.getReservationCode(), price, getBaggageWeight());
    }
}
