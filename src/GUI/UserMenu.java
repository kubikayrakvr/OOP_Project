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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.function.Consumer;

import FlightManagement.Flight;
import FlightManagement.Seat;
import TicketReservation.Reservation;
import TicketReservation.Ticket;
import TicketReservation.Passenger;

import Managers.*;
import Services.ReservationService;
import Services.TicketService;

public class UserMenu extends Application {

    private LoginUser currentUser;
    private TabPane tabPane;

    private FlightSystemContext context;

    private static final String CSS_FONT_BASE = "-fx-font-family: 'Segoe UI';";
    private static final String COLOR_PRIMARY = "#1976D2";
    private static final String COLOR_SUCCESS = "#388E3C";
    private static final String COLOR_TEXT    = "#37474F";
    
    public UserMenu(LoginUser user, FlightSystemContext context) {
        this.currentUser = user;

        this.context = context;

    }

    public void start(Stage stage) {
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab homeTab = new Tab("Home");
        Tab searchTab = new Tab("Search Flights");
        Tab reservationsTab = new Tab("My Reservations");
        Tab ticketsTab = new Tab("My Tickets");

        homeTab.setContent(createHomeView(stage, searchTab, ticketsTab, reservationsTab));
        searchTab.setContent(createSearchView(searchTab));
        reservationsTab.setContent(createReservationsView(reservationsTab)); 
        ticketsTab.setContent(createTicketsView(ticketsTab));

        tabPane.getTabs().addAll(homeTab, searchTab, reservationsTab, ticketsTab, createLogoutTab(stage));

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Flight Reservations System - " + currentUser.getUsername());
        stage.show();
    }
    
    private VBox createHomeView(Stage stage, Tab searchTab, Tab ticketsTab, Tab reservationsTab) {
        Label welcomeLabel = new Label("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subLabel = new Label("Ready to fly?");
        subLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");

        Button searchFlightsBtn = new Button("Go to Search");
        searchFlightsBtn.setMinWidth(160);
        searchFlightsBtn.setStyle("-fx-background-color: #e6f7ff; -fx-border-color: #b3e0ff;");
        searchFlightsBtn.setOnAction(e -> tabPane.getSelectionModel().select(searchTab));

        Button myTicketsBtn = new Button("View My Tickets");
        myTicketsBtn.setMinWidth(160);
        myTicketsBtn.setOnAction(e -> tabPane.getSelectionModel().select(ticketsTab));

        Button myReservationsBtn = new Button("View Reservations");
        myReservationsBtn.setMinWidth(160);
        myReservationsBtn.setOnAction(e -> tabPane.getSelectionModel().select(reservationsTab));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMinWidth(160);
        logoutBtn.setStyle("-fx-background-color: #ffcccc;");
        logoutBtn.setOnAction(e -> logout(stage));

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getChildren().addAll(welcomeLabel, subLabel, new Separator(), 
                                    searchFlightsBtn, myTicketsBtn, myReservationsBtn, 
                                    new Separator(), logoutBtn);
        return layout;
    }
    
    private Tab createLogoutTab(Stage stage) {
        Tab logoutTab = new Tab("Logout");
        logoutTab.setOnSelectionChanged(e -> {
            if (logoutTab.isSelected()) {
                logout(stage);
            }
        });
        return logoutTab;
    }

    private void logout(Stage stage) {
        stage.close();
        new LoginApp().start(new Stage()); 
    }

    private Node createSearchView(Tab tab) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Find Your Flight");
        title.setStyle(CSS_FONT_BASE + " -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_TEXT + ";");

        // --- Search Bar ---
        HBox searchFields = new HBox(15);
        searchFields.setAlignment(Pos.CENTER);
        searchFields.setPadding(new Insets(15));
        searchFields.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        TextField depField = styleTextField(new TextField(), "From (e.g. IST)");
        TextField arrField = styleTextField(new TextField(), "To (e.g. LHR)");
        
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date");
        datePicker.setStyle(CSS_FONT_BASE + " -fx-font-size: 13px;");
        configureDatePicker(datePicker);

        Button searchBtn = new Button("Search");
        styleButton(searchBtn, COLOR_PRIMARY);
        
        Button clearBtn = new Button("Show All");
        styleButton(clearBtn, "#78909C");

        searchFields.getChildren().addAll(depField, arrField, datePicker, searchBtn, clearBtn);

        TableView<Flight> table = new TableView<>();
        styleTable(table); 

        TableColumn<Flight, String> colNum = new TableColumn<>("Flight #");
        colNum.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getFlightNum())));

        TableColumn<Flight, String> colRoute = new TableColumn<>("Route");
        colRoute.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getRoute().getDeparturePlace() + " ➝ " + cell.getValue().getRoute().getArrivalPlace()));

        TableColumn<Flight, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate()));

        TableColumn<Flight, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%02d:00", cell.getValue().getHour())));

        TableColumn<Flight, String> colPlane = new TableColumn<>("Aircraft");
        colPlane.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPlane().getPlaneModel()));

        table.getColumns().addAll(colNum, colRoute, colDate, colTime, colPlane);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> new TableRow<Flight>() {
            protected void updateItem(Flight item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item == null || empty) {
                    setStyle("");
                } 
                else {
                    if (isSelected()) {
                        setStyle(""); 
                    } 
                    else if (context.getFlightManager().hasDepartureTimePassed(item)) {
                        setStyle("-fx-background-color: #FFEBEE; -fx-text-background-color: #B71C1C;"); 
                    } 
                    else {
                        setStyle("");
                    }
                }
            }
        });

        Button btnBook = new Button("Book Selected Flight");
        styleButton(btnBook, COLOR_SUCCESS);
        btnBook.setPrefWidth(200);
        btnBook.setDisable(true); 

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                btnBook.setDisable(true);
            } else {
                if (context.getFlightManager().hasDepartureTimePassed(newVal)) {
                    btnBook.setDisable(true);
                    btnBook.setText("Flight Departed");
                    btnBook.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                } else {
                    btnBook.setDisable(false);
                    btnBook.setText("Book Selected Flight");
                    styleButton(btnBook, COLOR_SUCCESS);
                }

                table.refresh(); 
            }
        });

        Runnable showAll = () -> {
            depField.clear(); arrField.clear(); datePicker.setValue(null);
            context.refreshFlightData();
            
            List<Flight> allFlights = context.getFlightManager().getAllFlights();
            table.setItems(FXCollections.observableArrayList(allFlights));
           
        };

        clearBtn.setOnAction(e -> showAll.run());

        Runnable doSearch = () -> {
            context.refreshFlightData();
            
            String dep = depField.getText().trim();
            String arr = arrField.getText().trim();
            String dateStr = (datePicker.getValue() != null) ? datePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;

            if (dep.isEmpty() && arr.isEmpty() && dateStr == null) {
                showAlert("Search Warning", "Enter at least one search criteria.");
                return;
            }
            
            List<Flight> filtered = context.getFlightManager().searchFlights(dep, arr, dateStr);
            table.setItems(FXCollections.observableArrayList(filtered));
            
            if (filtered.isEmpty()) showAlert("Info", "No upcoming flights found.");
        };
        
        searchBtn.setOnAction(e -> doSearch.run());

        tab.setOnSelectionChanged(e -> { if (tab.isSelected()) showAll.run(); });

        btnBook.setOnAction(e -> {
            Flight selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (context.getFlightManager().hasDepartureTimePassed(selected)) {
                    showAlert("Error", "Cannot book a flight that has already departed.");
                    return;
                }

                context.refreshFlightData();
                context.refreshBookingData();
                
                Flight live = context.getFlightManager().getFlight(selected.getFlightNum());
                if (live == null) {
                    showAlert("Error", "Flight cancelled."); showAll.run(); return;
                }
                showBookingDialog(live);
            }
        });

        showAll.run();
        layout.getChildren().addAll(title, searchFields, table, btnBook);
        return layout;
    }
    private void showBookingDialog(Flight flight) {
        Dialog<Seat> dialog = new Dialog<>();
        dialog.setTitle("Step 1: Select Seat");
        dialog.setHeaderText("Flight: " + flight.toString());
        dialog.getDialogPane().setPrefSize(1000, 700);
        dialog.setResizable(true);

        ButtonType nextBtnType = new ButtonType("Next: Passenger Info", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(nextBtnType, ButtonType.CANCEL);
        Node nextButton = dialog.getDialogPane().lookupButton(nextBtnType);
        nextButton.setDisable(true);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #e0e0e0;");

        VBox infoPanel = new VBox(20);
        infoPanel.setPadding(new Insets(20));
        infoPanel.setPrefWidth(300);
        infoPanel.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");
        
        Label lblSeatNum = new Label("None");
        lblSeatNum.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        Label lblClass = new Label("Class: -");
        Label lblPriceInfo = new Label("Price: -");
        lblPriceInfo.setStyle("-fx-font-size: 14px;");

        VBox detailsBox = new VBox(5, new Label("Selected Seat:"), lblSeatNum, lblClass, lblPriceInfo);
        detailsBox.setAlignment(Pos.CENTER);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #eee;");
        
        infoPanel.getChildren().addAll(detailsBox, new Label("Legend:"), 
            PlaneMapRenderer.createLegendItem("Business", "#FFD700"),
            PlaneMapRenderer.createLegendItem("Economy", "#4CAF50"),
            PlaneMapRenderer.createLegendItem("Occupied", "#9E9E9E"),
            PlaneMapRenderer.createLegendItem("Selected", "#2196F3")
        );
        mainLayout.setRight(infoPanel);

        final Seat[] selectedSeatWrapper = {null};

        Consumer<Seat> onSeatClick = (seat) -> {
            selectedSeatWrapper[0] = seat;
            nextButton.setDisable(false);

            lblSeatNum.setText(seat.getSeatNum());
            lblClass.setText(seat.getFlightClass().toString());

            double total = context.getCalculatePrice().priceCalculation(flight, seat);

            lblPriceInfo.setText(String.format("Price: $%.2f", total));

            if (seat.getFlightClass().toString().equalsIgnoreCase("BUSINESS")) {
                lblPriceInfo.setStyle("-fx-text-fill: #b7950b; -fx-font-weight: bold; -fx-font-size: 15px;");
            } else {
                lblPriceInfo.setStyle("-fx-text-fill: black; -fx-font-size: 15px;");
            }
        };

        VBox fuselage = PlaneMapRenderer.createInteractiveFuselage(flight.getPlane(), onSeatClick);

        StackPane centerStack = new StackPane(fuselage);
        centerStack.setStyle("-fx-background-color: #e0e0e0;");
        
        ScrollPane scrollPane = new ScrollPane(centerStack);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        
        mainLayout.setCenter(scrollPane);
        dialog.getDialogPane().setContent(mainLayout);

        dialog.setResultConverter(btn -> {
            if (btn == nextBtnType) return selectedSeatWrapper[0];
            return null;
        });

        java.util.Optional<Seat> result = dialog.showAndWait();
        if (result.isPresent()) {
            showPassengerInfoDialog(flight, result.get());
        }
    }

    private void showPassengerInfoDialog(Flight flight, Seat seat) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Step 2: Passenger Details");
        dialog.setHeaderText("Enter Passenger Information");

        ButtonType confirmBtnType = new ButtonType("Confirm & Book", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(currentUser.getUsername());
        TextField surnameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextArea addressArea = new TextArea();
        addressArea.setPrefHeight(60);

        grid.add(new Label("Seat:"), 0, 0); 
        grid.add(new Label(seat.getSeatNum() + " (" + seat.getFlightClass() + ")"), 1, 0);
        
        grid.add(new Label("Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Surname:"), 0, 2); grid.add(surnameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3); grid.add(emailField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4); grid.add(phoneField, 1, 4);
        grid.add(new Label("Address:"), 0, 5); grid.add(addressArea, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == confirmBtnType) {
                if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() || 
                    emailField.getText().isEmpty() || phoneField.getText().isEmpty() || 
                    addressArea.getText().isEmpty()) {
                    showAlert("Input Error", "All fields are required.");
                    return false;
                }

                context.refreshBookingData();
                
                Flight liveFlight = context.getFlightManager().getFlight(flight.getFlightNum());
                Seat liveSeat = findSeatObject(liveFlight, seat.getSeatNum());

                if (liveSeat == null || liveSeat.getReserveStatus()) {
                    showAlert("Sync Error", "Seat was taken while you were typing!");
                    return false;
                }

                Passenger passenger = new Passenger(
                    currentUser.getId(),
                    nameField.getText(),
                    surnameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressArea.getText()
                );

                boolean success = context.getReservationManager().createReservation(liveFlight, passenger, liveSeat);
                if (success) {
                    ReservationService.saveReservations(context.getReservationManager());
                    showAlert("Success", "Booking Confirmed! Reservation Code Generated.");
                    return true;
                } else {
                    showAlert("Error", "Booking failed.");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }
    
    private Node createReservationsView(Tab tab) { 
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("My Reservations");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Reservation> resList = new ListView<>();

        Runnable refreshList = () -> {
            context.refreshBookingData(); 
            resList.getItems().clear();
            
            for (Reservation r : context.getReservationManager().getAllReservations()) {
                if (r.getPassenger().getPassengerID().equals(currentUser.getId())) {
                    resList.getItems().add(r);
                }
            }
            if (resList.getItems().isEmpty()) resList.setPlaceholder(new Label("No active reservations."));
        };

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) {
                refreshList.run();
            }
        });
        
        refreshList.run();

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshList.run());
        
        Button btnCancel = new Button("Cancel Reservation");
        btnCancel.setStyle("-fx-background-color: #ffcccc;");
        btnCancel.setOnAction(e -> {
            Reservation selected = resList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String resCode = selected.getReservationCode();

                context.refreshBookingData();

                if (context.getReservationManager().getReservation(resCode) == null) {
                    showAlert("Sync Error", "This reservation was already cancelled.");
                    refreshList.run();
                    return;
                }

                boolean success = context.getReservationManager().cancelReservation(resCode);
                if (success) {
                    ReservationService.saveReservations(context.getReservationManager());
                    TicketService.saveTickets(context.getTicketManager());
                    refreshList.run();
                    showAlert("Success", "Reservation cancelled.");
                }
            } else {
                showAlert("Error", "Select a reservation to cancel.");
            }
        });

        Button btnBuyTicket = new Button("Buy Ticket");
        btnBuyTicket.setStyle("-fx-background-color: #e6f7ff;");
        btnBuyTicket.setOnAction(e -> {
            Reservation selected = resList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showBuyTicketDialog(selected);
                refreshList.run();
            } else {
                showAlert("Error", "Select a reservation to buy a ticket for.");
            }
        });

        HBox buttons = new HBox(10, btnRefresh, btnBuyTicket, btnCancel);
        layout.getChildren().addAll(title, resList, buttons);
        return layout;
    }

    private void showBuyTicketDialog(Reservation targetReservation) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Buy Ticket");
        dialog.setHeaderText("Step 1: Enter Baggage Details");

        ButtonType nextBtnType = new ButtonType("Next", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(nextBtnType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField flightField = new TextField(targetReservation.getFlight().toString());
        flightField.setEditable(false);
        flightField.setDisable(true); 

        TextField seatField = new TextField(targetReservation.getSeat().getSeatNum());
        seatField.setEditable(false);
        seatField.setDisable(true);

        TextField baggageField = new TextField("0");
        baggageField.setPromptText("e.g. 15.5");

        grid.add(new Label("Flight:"), 0, 0);
        grid.add(flightField, 1, 0);
        
        grid.add(new Label("Seat:"), 0, 1);
        grid.add(seatField, 1, 1);

        grid.add(new Separator(), 0, 2, 2, 1); 

        grid.add(new Label("Baggage Weight (kg):"), 0, 3);
        grid.add(baggageField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == nextBtnType) {
                try {
                    double weight = Double.parseDouble(baggageField.getText());
                    if (weight < 0) {
                        showAlert("Input Error", "Baggage weight cannot be negative.");
                        return false;
                    }

                    context.refreshBookingData();

                    Reservation liveRes = context.getReservationManager().getReservation(targetReservation.getReservationCode());
                    if (liveRes == null) {
                        showAlert("Sync Error", "Purchase failed.\nThis reservation was cancelled (perhaps by Admin or timeout).");
                        return false;
                    }

                    boolean alreadyExists = false;
                    for(Ticket t : context.getTicketManager().getAllTickets()) {
                        if(t.getReservation().getReservationCode().equals(liveRes.getReservationCode())) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (alreadyExists) {
                        showAlert("Error", "You already have a ticket for this reservation.");
                        return false;
                    }

                    double calculatedPrice = context.getCalculatePrice().priceCalculation(liveRes, weight);

                    ButtonType confirmBtn = new ButtonType("Confirm Purchase", ButtonBar.ButtonData.YES);
                    ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
                    
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "", confirmBtn, cancelBtn);
                    confirmAlert.setTitle("Confirm Purchase");
                    confirmAlert.setHeaderText("Please review your purchase details");

                    String details = String.format(
                        "Flight: %s\n" +
                        "Seat: %s (%s)\n" +
                        "Baggage: %.1f kg\n\n" +
                        "--------------------------------\n" +
                        "TOTAL PRICE: %.2f TL\n" +
                        "--------------------------------",
                        liveRes.getFlight().toString(),
                        liveRes.getSeat().getSeatNum(),
                        liveRes.getSeat().getFlightClass().toString(), 
                        weight,
                        calculatedPrice
                    );
                    
                    confirmAlert.setContentText(details);

                    var result = confirmAlert.showAndWait();

                    if (result.isPresent() && result.get() == confirmBtn) {
                        boolean success;
                        if (weight > 0) {
                            success = context.getTicketManager().createTicket(liveRes, weight);
                        } 
                        else {
                            success = context.getTicketManager().createTicket(liveRes);
                        }

                        if (success) {
                            TicketService.saveTickets(context.getTicketManager());
                            showAlert("Success", "Ticket Purchased Successfully!\nCheck 'My Tickets' tab.");
                            return true;
                        }
                        else {
                            showAlert("Error", "Could not generate ticket ID.");
                            return false;
                        }
                    } 
                    else {
                        return false; 
                    }

                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Baggage weight must be a valid number.");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    private Node createTicketsView(Tab tab) { 
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("My Purchased Tickets");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Ticket> ticketList = new ListView<>();

        Runnable refreshList = () -> {
            context.refreshBookingData();
            ticketList.getItems().clear();
            
            for (Ticket t : context.getTicketManager().getAllTickets()) {
                if (t.getReservation().getPassenger().getPassengerID().equals(currentUser.getId())) {
                    ticketList.getItems().add(t);
                }
            }
            if (ticketList.getItems().isEmpty()) ticketList.setPlaceholder(new Label("No tickets purchased."));
        };
        
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshList.run();
        });

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshList.run());

        Button btnRefund = new Button("Refund Ticket");
        btnRefund.setStyle("-fx-background-color: #fcf3cf; -fx-border-color: #f1c40f;"); 

        btnRefund.setOnAction(e -> {
            Ticket selected = ticketList.getSelectionModel().getSelectedItem();
            if (selected != null) {

                String ticketId = selected.getTicketId();
                double amount = selected.getPrice();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Are you sure you want to refund this ticket?\n" +
                    "Refund Amount: " + amount + " TL\n", 
                    ButtonType.YES, ButtonType.NO);
                alert.setTitle("Confirm Refund");
                alert.setHeaderText(null);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    context.refreshBookingData();
                    if (context.getTicketManager().getTicket(ticketId) == null) {
                        showAlert("Sync Error", "Ticket already processed or invalid.");
                        refreshList.run();
                        return;
                    }

                    context.getTicketManager().deleteTicket(ticketId);
                    TicketService.saveTickets(context.getTicketManager());

                    refreshList.run();
                    showAlert("Success", "Ticket refunded successfully.\nAmount returned: " + amount + " TL");
                }
            } 
            else {
                showAlert("Selection Error", "Please select a ticket to refund.");
            }
        });

        controls.getChildren().addAll(btnRefund, btnRefresh);
        layout.getChildren().addAll(title, ticketList, controls);
        return layout;
    }

    private void configureDatePicker(DatePicker datePicker) {
        String pattern = "dd/MM/yyyy";
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            public String toString(LocalDate date) { return (date != null) ? dateFormatter.format(date) : ""; }
            public LocalDate fromString(String string) { return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null; }
        });
    }
    
    private void styleButton(Button btn, String colorHex) {
        btn.setStyle(
            "-fx-background-color: " + colorHex + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            CSS_FONT_BASE + 
            " -fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + colorHex + ", 20%); -fx-text-fill: white; -fx-font-weight: bold; " + CSS_FONT_BASE + " -fx-background-radius: 5; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; " + CSS_FONT_BASE + " -fx-background-radius: 5; -fx-cursor: hand;"));
    }
    
    private TextField styleTextField(TextField tf, String prompt) {
        tf.setPromptText(prompt);
        tf.setStyle(CSS_FONT_BASE + " -fx-font-size: 13px; -fx-background-radius: 5; -fx-padding: 8;");
        return tf;
    }

    private void styleTable(TableView<?> table) {
        table.setStyle(CSS_FONT_BASE + " -fx-font-size: 13px; -fx-base: white; -fx-background-color: white;");
    }
    
    
    private Seat findSeatObject(Flight f, String num) {
        if(f.getPlane().getBussSeats() != null) {
            for(Seat[] row : f.getPlane().getBussSeats()) {
                for(Seat s : row) if(s.getSeatNum().equals(num)) return s;
            }
        }
        if(f.getPlane().getEconSeats() != null) {
            for(Seat[] row : f.getPlane().getEconSeats()) {
                for(Seat s : row) if(s.getSeatNum().equals(num)) return s;
            }
        }
        return null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}