package Managers;

import FlightManagement.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import FlightManagement.Seating;
import Managers.FlightManager;
import Managers.ReservationManager;
import Managers.SeatManager;
import Managers.TicketManager;


public class PlaneManager {

    private HashMap<String, Plane> planes;
    private int planeCounter;
    private Scanner scanner = new Scanner(System.in);
    private FlightSystemContext context;
    
    public PlaneManager() {
        this.planes = new HashMap<String,Plane>();
        this.planeCounter = 0;
        
    }
    
	public void setContext(FlightSystemContext context){
		this.context = context;
	}
    
    public boolean createPlane(String planeModel, Seating seatings) {

        if (planeModel == null || planeModel.isBlank())
            return false;

        if (seatings == null)
            return false;

        String planeId = "PL-" + (++planeCounter);

        Plane newPlane = new Plane(planeId, planeModel, seatings);
        planes.put(planeId, newPlane);
        return true;
    }
    
    public void addLoadedPlane(Plane plane) {
        planes.put(plane.getPlaneId(), plane);

        try {
            String idNumPart = plane.getPlaneId().replace("PL-", "");
            int idNum = Integer.parseInt(idNumPart);
            if (idNum > planeCounter) {
                planeCounter = idNum;
            }
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse ID number for counter update: " + plane.getPlaneId());
        }
    }
    
    public boolean updatePlane(String planeId, String newModel, Seating newSeating) {
        Plane plane = planes.get(planeId);
        
        if (plane == null) {
            System.out.println("Error: Plane " + planeId + " not found.");
            return false;
        }

        boolean success = true;

        if (newModel != null && !newModel.isBlank()) {
            changePlaneModel(plane, newModel);
        }

        if (newSeating != null) {
            if (plane.hasAnyReservedSeat()) {
                System.out.println("Error: Cannot change seating configuration because reservations exist on this plane.");
                success = false; 
            } else {
                changeSeatMatrix(plane, newSeating);
            }
        }

        return success;
    } 
    
    private boolean changePlaneModel(Plane plane , String newModel) {
    	plane.setPlaneModel(newModel);
    	return true;
    }
    private boolean changeSeatMatrix(Plane plane , Seating seating) {
    	plane.setSeatMatrix(seating);
    	return true;
    }
    
    public void deletePlane(String planeId, TicketManager ticketManager) {
        Plane plane = planes.get(planeId);
        
        if (plane == null) {
            System.out.println("Plane not found!");
            return;
        }

        context.getFlightManager().deleteFlightByPlane(plane);

        planes.remove(planeId);
    }
    
    public void removeAllPlanes() {
        planes.clear();
    }
    
    public Plane getPlane(String planeId) {
        return planes.get(planeId);
    }
    public List<String> getAllPlaneIds() {
        return new ArrayList<>(planes.keySet());
    }
    
    public List<Plane> getPlanesList() {
        return new ArrayList<>(planes.values());
    }
}
