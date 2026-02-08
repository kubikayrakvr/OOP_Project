package Services;

import java.io.*;
import java.util.List;

import FlightManagement.Plane;
import FlightManagement.Seating;
import Managers.PlaneManager;
import Managers.SeatManager;

public class FleetService {

    private static final String FILE_PATH = "planes.txt";

    static {
        FileHelper.ensureFileExists(FILE_PATH);
    }
    
    public static void loadPlanes(PlaneManager planeManager, SeatManager seatManager) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");

                if (parts.length == 6) {
                    String id = parts[0];
                    String model = parts[1];

                    int bussRow = Integer.parseInt(parts[2]);
                    int bussCol = Integer.parseInt(parts[3]);
                    int econRow = Integer.parseInt(parts[4]);
                    int econCol = Integer.parseInt(parts[5]);

                    int[][] arrangement = {
                        {bussCol, bussRow},
                        {econCol, econRow}
                    };
                    Seating seating = seatManager.createSeatings(arrangement);

                    Plane plane = new Plane(id, model, seating);
                    planeManager.addLoadedPlane(plane);
                }
            }
            System.out.println("Planes loaded successfully.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading planes: " + e.getMessage());
        }
    }

    public static void savePlanes(PlaneManager planeManager) {
        List<Plane> allPlanes = planeManager.getPlanesList();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Plane plane : allPlanes) {
                
                int bussRow = 0, bussCol = 0;
                if (plane.getBussSeats() != null && plane.getBussSeats().length > 0) {
                    bussRow = plane.getBussSeats().length;
                    bussCol = plane.getBussSeats()[0].length;
                }

                int econRow = 0, econCol = 0;
                if (plane.getEconSeats() != null && plane.getEconSeats().length > 0) {
                    econRow = plane.getEconSeats().length;
                    econCol = plane.getEconSeats()[0].length;
                }

                String line = String.format("%s;%s;%d;%d;%d;%d",
                        plane.getPlaneId(),   
                        plane.getPlaneModel(), 
                        bussRow, bussCol,
                        econRow, econCol);

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving planes: " + e.getMessage());
        }
    }
}