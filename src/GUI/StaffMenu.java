package GUI;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import FlightManagement.Flight;
import Managers.FlightSystemContext;
import TicketReservation.Reservation;
import TicketReservation.Ticket;

public class StaffMenu extends Application {

    private LoginUser currentUser;
    private TabPane tabPane;

    private FlightSystemContext context;

    public StaffMenu(LoginUser user, FlightSystemContext context) {
        this.currentUser = user;
        this.context = context;
    }

    @Override
    public void start(Stage stage) {
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab homeTab = new Tab("Dashboard");
        Tab manifestTab = new Tab("Passenger Manifests");
        Tab checkInTab = new Tab("Boarding & Check-In");

        homeTab.setContent(createHomeView(stage, manifestTab, checkInTab));
        manifestTab.setContent(createManifestView(manifestTab));
        checkInTab.setContent(createCheckInView(checkInTab));

        tabPane.getTabs().addAll(homeTab, manifestTab, checkInTab, createLogoutTab(stage));

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 700, 550);
        stage.setScene(scene);
        stage.setTitle("Staff Portal - Flight System");
        stage.show();
    }

    private Node createHomeView(Stage stage, Tab manifest, Tab checkIn) {
        Label welcome = new Label("Staff Portal");
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label user = new Label("Logged in as: " + currentUser.getUsername());

        Button btnManifest = new Button("View Passenger Lists");
        btnManifest.setMinWidth(200);
        btnManifest.setOnAction(e -> tabPane.getSelectionModel().select(manifest));

        Button btnCheckIn = new Button("Boarding Gate / Check-In");
        btnCheckIn.setMinWidth(200);
        btnCheckIn.setOnAction(e -> tabPane.getSelectionModel().select(checkIn));

        Button btnLogout = new Button("Logout");
        btnLogout.setMinWidth(200);
        btnLogout.setStyle("-fx-background-color: #fadbd8;");
        btnLogout.setOnAction(e -> logout(stage));

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getChildren().addAll(welcome, user, new Separator(), btnManifest, btnCheckIn, 
                                    new Separator(), btnLogout);
        return layout;
    }

    private Node createManifestView(Tab tab) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Flight Passenger Manifest");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox selector = new HBox(10);
        selector.setAlignment(Pos.CENTER);
        ComboBox<Flight> flightCombo = new ComboBox<>();
        flightCombo.setPromptText("Select Flight");
        flightCombo.setMinWidth(200);
        
        Button loadBtn = new Button("Load List");
        selector.getChildren().addAll(new Label("Flight:"), flightCombo, loadBtn);

        TableView<Reservation> table = new TableView<>();
        
        TableColumn<Reservation, String> colSeat = new TableColumn<>("Seat");
        colSeat.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSeat().getSeatNum()));
        
        TableColumn<Reservation, String> colName = new TableColumn<>("Passenger Name");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getPassenger().getName() + " " + cell.getValue().getPassenger().getSurname()));
        
        TableColumn<Reservation, String> colResId = new TableColumn<>("Res Code");
        colResId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReservationCode()));

        table.getColumns().addAll(colSeat, colName, colResId);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Select a flight to view passengers."));

        Runnable refreshFlights = () -> {
            context.refreshAllData(); 
            flightCombo.getItems().clear();
            flightCombo.getItems().addAll(context.getFlightManager().getAllFlights()); 
        };

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshFlights.run();
        });

        loadBtn.setOnAction(e -> {
            Flight selected = flightCombo.getValue();
            if (selected == null) {
                showAlert("Error", "Please select a flight first.");
                return;
            }

            context.refreshAllData();

            List<Reservation> flightRes = new ArrayList<>();
            for (Reservation r : context.getReservationManager().getAllReservations()) {
                if (r.getFlight().getFlightNum() == selected.getFlightNum()) {
                    flightRes.add(r);
                }
            }
            
            table.setItems(FXCollections.observableArrayList(flightRes));
            
            if (flightRes.isEmpty()) {
                table.setPlaceholder(new Label("No passengers booked on Flight " + selected.getFlightNum()));
            }
        });

        layout.getChildren().addAll(title, selector, table);
        return layout;
    }

    private Node createCheckInView(Tab tab) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Boarding & Ticket Verification");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField ticketField = new TextField();
        ticketField.setPromptText("Enter Ticket ID (e.g. TCKT-1)");
        ticketField.setMaxWidth(300);

        Button verifyBtn = new Button("Verify & Board");
        verifyBtn.setStyle("-fx-background-color: #d4efdf; -fx-border-color: #a9dfbf;"); 
        verifyBtn.setMinWidth(150);

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setMaxWidth(400);
        resultArea.setMaxHeight(150);
        resultArea.setPromptText("Scan result will appear here...");

        verifyBtn.setOnAction(e -> {
            String id = ticketField.getText().trim();
            if (id.isEmpty()) {
                showAlert("Input Error", "Please enter a Ticket ID.");
                return;
            }

            context.refreshBookingData(); 

            Ticket ticket = context.getTicketManager().getTicket(id);

            if (ticket != null) {
                String info = "✅ VALID TICKET\n" +
                              "--------------------------\n" +
                              "Passenger : " + ticket.getReservation().getPassenger().getName() + " " + 
                                               ticket.getReservation().getPassenger().getSurname() + "\n" +
                              "Flight    : " + ticket.getReservation().getFlight().toString() + "\n" +
                              "Seat      : " + ticket.getReservation().getSeat().getSeatNum() + "\n" +
                              "Baggage   : " + ticket.getBaggageWeight() + " kg\n" +
                              "--------------------------\n" +
                              "Status: BOARDING APPROVED";
                
                resultArea.setText(info);
                resultArea.setStyle("-fx-control-inner-background: #e8f8f5;"); 
            } 
            else {
                resultArea.setText("❌ INVALID TICKET\n\nTicket ID '" + id + "' not found in system.\nPlease check the number or contact Admin.");
                resultArea.setStyle("-fx-control-inner-background: #fdedec;"); 
            }
        });

        tab.setOnSelectionChanged(e -> {
            if(tab.isSelected()) context.refreshAllData();
        });

        layout.getChildren().addAll(title, ticketField, verifyBtn, resultArea);
        return layout;
    }

    private Tab createLogoutTab(Stage stage) {
        Tab logoutTab = new Tab("Logout");
        logoutTab.setOnSelectionChanged(e -> {
            if (logoutTab.isSelected()) logout(stage);
        });
        return logoutTab;
    }

    private void logout(Stage stage) {
        stage.close();
        new LoginApp().start(new Stage());
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}