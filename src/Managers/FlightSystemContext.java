package Managers;

import Services.FleetService;
import Services.FlightService;
import Services.PriceCalculationService;
import Services.ReservationService;
import Services.TicketService;

public class FlightSystemContext {

    private final PlaneManager planeManager;
    private final SeatManager seatManager;
    private final TicketManager ticketManager;
    private final FlightManager flightManager;
    private final ReservationManager reservationManager;
    private CalculatePrice calculatePrice;

    public FlightSystemContext() {
        this.seatManager = new SeatManager();
        this.ticketManager = new TicketManager();
        this.planeManager = new PlaneManager();
        this.flightManager = new FlightManager();
        this.reservationManager = new ReservationManager();
        this.calculatePrice = PriceCalculationService.loadCalculator();
        // refreshAllData();
    }
    
    public void setContexts() {
	    this.getPlaneManager().setContext(this);
	    this.getFlightManager().setContext(this);
	    this.getReservationManager().setContext(this);
	    this.getSeatManager().setContext(this);
	    this.getTicketManager().setContext(this);
    }
    
    public void setCalculatePrice(CalculatePrice calculatePrice) {
    	this.calculatePrice = calculatePrice;
    }
    
    public void refreshFleetData() {
        planeManager.removeAllPlanes();
        FleetService.loadPlanes(planeManager, seatManager);
    }

    public void refreshFlightData() {
        flightManager.removeAllFlights();
        FlightService.loadFlights(flightManager, planeManager);
    }

    public void refreshBookingData() {
        reservationManager.removeAllReservations();
        ticketManager.removeAllTickets();

        ReservationService.loadReservations(reservationManager, flightManager);
        TicketService.loadTickets(ticketManager, reservationManager);
    }

    public void refreshAllData() {
        refreshFleetData();
        refreshFlightData();
        refreshBookingData();
    }

    public PlaneManager getPlaneManager() { return planeManager; }
    public SeatManager getSeatManager() { return seatManager; }
    public TicketManager getTicketManager() { return ticketManager; }
    public FlightManager getFlightManager() { return flightManager; }
    public ReservationManager getReservationManager() { return reservationManager; }
    public CalculatePrice getCalculatePrice() { return calculatePrice; }
}