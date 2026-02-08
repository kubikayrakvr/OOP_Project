package Managers;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;

import FlightManagement.Seat;
import FlightManagement.Flight;
import FlightManagement.Plane;
import TicketReservation.Reservation;
import TicketReservation.Passenger;



public class ReservationManager {
	private HashMap<String, Reservation> reservations;
    private FlightSystemContext context;
	
	public ReservationManager() {
        this.reservations = new HashMap<String, Reservation>();
    }
	
	public void setContext(FlightSystemContext context){
		this.context = context;
	}
	
	public boolean createReservation(Flight flight, Passenger passenger, Seat seat) {
		if(flight == null) {
			System.out.println("This flight does not exist");
			return false;
		}
		if(passenger == null) {
			System.out.println("This passenger does not exist");
			return false;
		}
		if(seat == null) {
			System.out.println("This flight does not exist");
			return false;
		}
		Plane plane = flight.getPlane();

	    boolean seatFound = false;
	    
	    Seat[][] bussSeats = plane.getBussSeats();
	    if (bussSeats != null) {
	        for (Seat[] row : bussSeats) {
	            for (Seat s : row) {
	                if (s != null && s.getSeatNum().equals(seat.getSeatNum())) {
	                    seatFound = true;
	                    break;
	                }
	            }
	            if (seatFound) break;
	        }
	    }

	    if (!seatFound) {
	        Seat[][] econSeats = plane.getEconSeats();
	        if (econSeats != null) {
	            for (Seat[] row : econSeats) {
	                for (Seat s : row) {
	                    if (s != null && s.getSeatNum().equals(seat.getSeatNum())) {
	                        seatFound = true;
	                        break;
	                    }
	                }
	                if (seatFound) break;
	            }
	        }
	    }

	    if (!seatFound) {
	        throw new IllegalArgumentException(
	            "Seat " + seat.getSeatNum() + " does not exist on this plane"
	        );
	    }

	 
		if(seat.getReserveStatus()) {
			System.out.println("This seat is already reserved");
			return false;
		}

		String date = LocalDate.now().toString();
		String reservationCode = flight.getFlightNum() + seat.getSeatNum() + passenger.getName() + passenger.getSurname() + date;
		Reservation oldValue = reservations.putIfAbsent(reservationCode, new Reservation(reservationCode, flight, passenger, seat, date));
		
		seat.setReserveStatus(true);
		if(oldValue != null) {
			System.out.println("A reservation with this code already exists");
			return false;
		}
		return true;
	}
	public boolean createRandomReservationAsync(Flight flight, Passenger passenger) {
		
		if(flight == null) {
			System.out.println("This flight does not exist");
			return false;
		}
		Seat seat = flight.getRandomAvailableEconomySeat();
		if(passenger == null) {
			System.out.println("This passenger does not exist");
			return false;
		}
		if(seat == null) {
			System.out.println("This flight does not exist");
			return false;
		}
		Plane plane = flight.getPlane();

	    boolean seatFound = false;

	    Seat[][] bussSeats = plane.getBussSeats();
	    if (bussSeats != null) {
	        for (Seat[] row : bussSeats) {
	            for (Seat s : row) {
	                if (s != null && s.getSeatNum().equals(seat.getSeatNum())) {
	                    seatFound = true;
	                    break;
	                }
	            }
	            if (seatFound) break;
	        }
	    }

	    if (!seatFound) {
	        Seat[][] econSeats = plane.getEconSeats();
	        if (econSeats != null) {
	            for (Seat[] row : econSeats) {
	                for (Seat s : row) {
	                    if (s != null && s.getSeatNum().equals(seat.getSeatNum())) {
	                        seatFound = true;
	                        break;
	                    }
	                }
	                if (seatFound) break;
	            }
	        }
	    }

	    if (!seatFound) {
	        throw new IllegalArgumentException(
	            "Seat " + seat.getSeatNum() + " does not exist on this plane"
	        );
	    }

	 
		if(seat.getReserveStatus()) {
			System.out.println("This seat is already reserved");
			return false;
		}

		String date = LocalDate.now().toString();
		String reservationCode = flight.getFlightNum() + seat.getSeatNum() + passenger.getName() + passenger.getSurname() + date;
		Reservation oldValue = reservations.put(reservationCode, new Reservation(reservationCode, flight, passenger, seat, date));
		try {
	        Thread.sleep(10);  // 10ms bekle
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }

		seat.setReserveStatus(true);
		if(oldValue != null) {
			System.out.println("A reservation with this code already exists");
			return false;
		}
		return true;
	}
public synchronized boolean createRandomReservationSync(Flight flight, Passenger passenger) {
		
		if(flight == null) {
			System.out.println("This flight does not exist");
			return false;
		}
		Seat seat = flight.getRandomAvailableEconomySeat();
		if(passenger == null) {
			System.out.println("This passenger does not exist");
			return false;
		}
		if(seat == null) {
			System.out.println("This seat does not exist");
			return false;
		}
		Plane plane = flight.getPlane();

	    boolean seatFound = false;

	    Seat[][] bussSeats = plane.getBussSeats();
	    if (bussSeats != null) {
	        for (Seat[] row : bussSeats) {
	            for (Seat s : row) {
	                if (s != null && s.getSeatNum().equals(seat.getSeatNum())) {
	                    seatFound = true;
	                    break;
	                }
	            }
	            if (seatFound) break;
	        }
	    }

	    if (!seatFound) {
	        Seat[][] econSeats = plane.getEconSeats();
	        if (econSeats != null) {
	            for (Seat[] row : econSeats) {
	                for (Seat s : row) {
	                    if (s != null && s.getSeatNum().equals(seat.getSeatNum())) {
	                        seatFound = true;
	                        break;
	                    }
	                }
	                if (seatFound) break;
	            }
	        }
	    }

	    if (!seatFound) {
	        throw new IllegalArgumentException(
	            "Seat " + seat.getSeatNum() + " does not exist on this plane"
	        );
	    }

	 
		if(seat.getReserveStatus()) {
			System.out.println("This seat is already reserved");
			return false;
		}

		String date = LocalDate.now().toString();
		String reservationCode = flight.getFlightNum() + seat.getSeatNum() + passenger.getName() + passenger.getSurname() + date;
		Reservation oldValue = reservations.put(reservationCode, new Reservation(reservationCode, flight, passenger, seat, date));
		
		seat.setReserveStatus(true);
		if(oldValue != null) {
			System.out.println("A reservation with this code already exists");
			return false;
		}
		return true;
	}

	public void addLoadedReservation(Reservation res) {
	    reservations.put(res.getReservationCode(), res);
	}
	
	public synchronized boolean cancelReservation(String reservationCode) {
		Reservation reservation = getReservation(reservationCode);
		if(reservation == null) {
			System.out.println("Reservation does not exist");
			return false;
		}
		
		context.getTicketManager().deleteTicketByReservation(reservation);
		reservation.getSeat().setReserveStatus(false);
		reservations.remove(reservationCode);
		return true;
	}
	
	public synchronized boolean deleteReservationByFlight(Flight flight) {
	    if (flight == null) {
	        return false;
	    }

	    List<String> idsToCancel = new ArrayList<>();
	    
	    for (Map.Entry<String, Reservation> entry : reservations.entrySet()) {
	        Reservation reservation = entry.getValue();
	        if (reservation.getFlight().equals(flight)) {
	            idsToCancel.add(entry.getKey());
	        }
	    }

	    boolean anyDeleted = false;
	    for (String id : idsToCancel) {
	        if (cancelReservation(id)) { 
	            anyDeleted = true;
	        }
	    }

	    return anyDeleted;
	}
	
    public void removeAllReservations() {
        reservations.clear();
    }
	
	public Reservation getReservation(String reservationCode) {
		return reservations.get(reservationCode);
	}
	
	public Iterable<Reservation> getAllReservations() {
	    return reservations.values();
	}

}
