package GUI;

import FlightManagement.Plane;
import FlightManagement.Seat;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlaneMapRenderer {
	
    private static final String SEAT_SVG_PATH = "M12,2 C8,2 6,4 6,7 L6,14 L4,14 C2.9,14 2,14.9 2,16 L2,20 L4,20 L4,18 L20,18 L20,20 L22,20 L22,16 C22,14.9 21.1,14 20,14 L18,14 L18,7 C18,4 16,2 12,2 Z M8,14 L16,14 L16,7 C16,5 14.5,4 12,4 C9.5,4 8,5 8,7 L8,14 Z";

    private static final String COLOR_BUSINESS = "#FFD700"; 
    private static final String COLOR_ECONOMY  = "#4CAF50"; 
    private static final String COLOR_TAKEN    = "#9E9E9E"; 
    private static final String COLOR_SELECTED = "#2196F3"; 
    private static final String COLOR_SCENARIO_TAKEN = "#F44336"; 

    public static VBox createInteractiveFuselage(Plane plane, Consumer<Seat> onSeatSelected) {
        VBox fuselage = new VBox(10);
        fuselage.setAlignment(Pos.TOP_CENTER);
        fuselage.setStyle("-fx-background-color: #f0f2f5; -fx-padding: 30 50; -fx-border-color: #cfd8dc; -fx-border-width: 0 2 0 2;");
        fuselage.setMaxWidth(600);

        Label lblFront = new Label("▲ FRONT ▲");
        lblFront.setStyle("-fx-font-weight: bold; -fx-text-fill: #aaa; -fx-padding: 0 0 10 0;");
        fuselage.getChildren().add(lblFront);

        List<Button> allButtons = new ArrayList<>();

        if (plane.getBussSeats() != null) {
            fuselage.getChildren().add(new Label("BUSINESS CLASS"));
            fuselage.getChildren().add(
                createInteractiveGrid(plane.getBussSeats(), true, allButtons, onSeatSelected)
            );
        }

        if (plane.getBussSeats() != null && plane.getEconSeats() != null) {
            fuselage.getChildren().add(new Separator());
        }

        if (plane.getEconSeats() != null) {
            fuselage.getChildren().add(new Label("ECONOMY CLASS"));
            fuselage.getChildren().add(
                createInteractiveGrid(plane.getEconSeats(), false, allButtons, onSeatSelected)
            );
        }

        return fuselage;
    }

    private static GridPane createInteractiveGrid(Seat[][] seats, boolean isBusiness, List<Button> allButtons, Consumer<Seat> onSeatSelected) {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(12);
        grid.setAlignment(Pos.TOP_CENTER);

        int numCols = seats[0].length;
        int aisleIndex = (numCols > 4) ? numCols / 2 : -1;

        for (int r = 0; r < seats.length; r++) {
            // Row label
            Label rowLbl = new Label(String.valueOf(r + 1));
            rowLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px; -fx-padding: 10 5 0 0;");
            grid.add(rowLbl, 0, r);

            int currentGridCol = 1;

            for (int c = 0; c < numCols; c++) {
                // Aisle gap
                if (c == aisleIndex) {
                    Region aisle = new Region();
                    aisle.setPrefWidth(40);
                    grid.add(aisle, currentGridCol++, r);
                }

                Seat s = seats[r][c];
                Button seatBtn = createSeatButton(s, isBusiness, allButtons, onSeatSelected);
                grid.add(seatBtn, currentGridCol++, r);
            }
        }
        return grid;
    }

    private static Button createSeatButton(Seat s, boolean isBusiness, List<Button> allButtons, Consumer<Seat> onSelection) {
        Button seatBtn = new Button();
        seatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");

        SVGPath icon = new SVGPath();
        icon.setContent(SEAT_SVG_PATH);
        icon.setScaleX(1.3);
        icon.setScaleY(1.3);

        String defaultColor = isBusiness ? COLOR_BUSINESS : COLOR_ECONOMY;
        
        if (s.getReserveStatus()) {
            icon.setStyle("-fx-fill: " + COLOR_TAKEN + "; -fx-stroke: #666;");
            seatBtn.setDisable(true);
            Tooltip.install(seatBtn, new Tooltip("Occupied"));
            seatBtn.setUserData("taken"); 
        } else {
            icon.setStyle("-fx-fill: " + defaultColor + "; -fx-stroke: #666;");
            String tooltip = (isBusiness ? "Business" : "Economy") + " - " + s.getSeatNum();
            Tooltip.install(seatBtn, new Tooltip(tooltip));
            seatBtn.setUserData(defaultColor); 
        }

        seatBtn.setGraphic(icon);
        
        if (!s.getReserveStatus()) {
            allButtons.add(seatBtn);
            seatBtn.setOnAction(e -> {
                for (Button b : allButtons) {
                    SVGPath bIcon = (SVGPath) b.getGraphic();
                    String bDefaultColor = (String) b.getUserData();
                    bIcon.setStyle("-fx-fill: " + bDefaultColor + "; -fx-stroke: #666;");
                    bIcon.setScaleX(1.3); bIcon.setScaleY(1.3);
                }

                icon.setStyle("-fx-fill: " + COLOR_SELECTED + "; -fx-stroke: darkblue;");
                icon.setScaleX(1.5); icon.setScaleY(1.5);

                onSelection.accept(s);
            });
        }

        return seatBtn;
    }

    public static void renderSimulationMap(Plane plane, VBox container) {
        container.getChildren().clear();
        VBox fuselage = new VBox(0);
        fuselage.setAlignment(Pos.TOP_CENTER);
        fuselage.setStyle("-fx-background-color: white; -fx-padding: 30 50; -fx-border-color: #cfd8dc; -fx-border-width: 2;");
        fuselage.setMaxWidth(600);

        Label lblFront = new Label("▲ COCKPIT ▲");
        lblFront.setStyle("-fx-font-weight: bold; -fx-text-fill: #aaa; -fx-padding: 0 0 20 0;");
        fuselage.getChildren().add(lblFront);

        if (plane.getEconSeats() != null) {
            fuselage.getChildren().add(createSimGrid(plane.getEconSeats()));
        }
        container.getChildren().add(fuselage);
    }

    private static GridPane createSimGrid(Seat[][] seats) {
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        
        int numCols = seats[0].length;
        int aisleIndex = (numCols > 4) ? numCols / 2 : -1;

        for (int r = 0; r < seats.length; r++) {
            grid.add(new Label(String.valueOf(r+1)), 0, r);
            int col = 1;
            for (int c = 0; c < numCols; c++) {
                if (c == aisleIndex) {
                    Region aisle = new Region(); aisle.setPrefWidth(30);
                    grid.add(aisle, col++, r);
                }
                Seat s = seats[r][c];
                SVGPath icon = new SVGPath();
                icon.setContent(SEAT_SVG_PATH);
                icon.setScaleX(1.2); icon.setScaleY(1.2);

                if (s.getReserveStatus()) {
                    icon.setStyle("-fx-fill: " + COLOR_SCENARIO_TAKEN + ";"); 
                }
                else {
                    icon.setStyle("-fx-fill: " + COLOR_ECONOMY + ";"); 
                }
                grid.add(icon, col++, r);
            }
        }
        return grid;
    }

    public static HBox createLegendItem(String text, String colorHex) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        SVGPath icon = new SVGPath();
        icon.setContent(SEAT_SVG_PATH);
        icon.setStyle("-fx-fill: " + colorHex + "; -fx-stroke: #777; -fx-stroke-width: 0.5;");
        icon.setScaleX(0.8); icon.setScaleY(0.8); 
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px;");
        box.getChildren().addAll(icon, lbl);
        return box;
    }
}