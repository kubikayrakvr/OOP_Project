package Managers;

import TicketReservation.Reservation;
import FlightManagement.Flight;
import FlightManagement.FlightClass;
import FlightManagement.Seat;

public class CalculatePrice {
    private double basePrice;
    private double tax;
    private double baggageFeePerKg;
    private double businessMultiplier;
    private double durationCostPerMinute; // <--- NEW FIELD

    public CalculatePrice(double basePrice, double tax, double baggageFeePerKg, double businessMultiplier, double durationCostPerMinute) {
        this.basePrice = basePrice;
        this.tax = tax;
        this.baggageFeePerKg = baggageFeePerKg;
        this.businessMultiplier = businessMultiplier;
        this.durationCostPerMinute = durationCostPerMinute;
    }

    public double priceCalculation(Flight flight, Seat seat) {
        double total = basePrice;

        if (flight != null) {
            total += flight.getDuration() * durationCostPerMinute; 
        }

        if (seat.getFlightClass() == FlightClass.BUSINESS) {
            total *= businessMultiplier;
        }

        total += tax;

        return total;
    }
    
    public double priceCalculation(Reservation reservation, double baggageWeight) {
        double total = basePrice;

        if (reservation.getFlight() != null) {
            total += reservation.getFlight().getDuration() * durationCostPerMinute; 
        }

        Seat seat = reservation.getSeat();
        if (seat.getFlightClass() == FlightClass.BUSINESS) {
            total *= businessMultiplier;
        }

        if (baggageWeight > 0) {
            total += baggageWeight * baggageFeePerKg;
        }

        total += tax;

        return total;
    }
    
    public double getBasePrice() { return basePrice; }
    public double getTax() { return tax; }
    public double getBaggageFeePerKg() { return baggageFeePerKg; }
    public double getBusinessMultiplier() { return businessMultiplier; }
    public double getDurationCostPerMinute() { return durationCostPerMinute; }
}