package GUI;

import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import TicketReservation.Ticket;
import TicketReservation.Reservation;
import FlightManagement.Flight;
import FlightManagement.Plane;
import FlightManagement.Route;
import Thread.Scenario1;
import Thread.Scenario2;
import Managers.CalculatePrice;
import Managers.FlightSystemContext;
import Services.AuthService;
import Services.FleetService;
import Services.FlightService;
import Services.PriceCalculationService;
import Services.ReservationService;
import Services.TicketService;

public class AdminMenu extends Application {

    private LoginUser currentUser;
    private TabPane tabPane;
    private FlightSystemContext context;



    public AdminMenu(LoginUser user, FlightSystemContext context) {
    	this.context = context;
        this.currentUser = user;
    }
    
    @Override
    public void start(Stage stage) {
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab homeTab = new Tab("Dashboard");
        Tab planesTab = new Tab("Planes & Fleet");
        Tab flightsTab = new Tab("Flights");
        Tab reservationsTab = new Tab("Reservations");
        Tab ticketsTab = new Tab("Tickets");
        Tab usersTab = new Tab("Users"); 
        Tab staffTab = new Tab("Staff Info");
        Tab pricesTab = new Tab("Price Settings"); 
        Tab scenariosTab = new Tab("Scenarios"); 

        usersTab.setContent(createUsersView(usersTab)); 
        staffTab.setContent(createStaffView(staffTab));
        planesTab.setContent(createPlanesView(planesTab));
        flightsTab.setContent(createFlightsView(flightsTab));
        reservationsTab.setContent(createReservationsView(reservationsTab));
        ticketsTab.setContent(createTicketsView(ticketsTab));
        pricesTab.setContent(createPriceSettingsView(pricesTab));
        scenariosTab.setContent(createScenariosView());

        homeTab.setContent(createHomeView(stage, planesTab, reservationsTab, ticketsTab, usersTab, staffTab, scenariosTab));

        tabPane.getTabs().addAll(
            homeTab, 
            planesTab, 
            flightsTab, 
            reservationsTab, 
            ticketsTab,
            pricesTab,
            usersTab,
            staffTab, 
            scenariosTab,
            createLogoutTab(stage)
        );

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 650); 
        stage.setScene(scene);
        stage.setTitle("Admin Panel - Flight System");
        stage.show();
    }

    private Node createHomeView(Stage stage, Tab planes, Tab res, Tab tickets, Tab users, Tab staff, Tab scenarios) {
        Label welcome = new Label("Admin Dashboard");
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Label user = new Label("Logged in as: " + currentUser.getUsername());

        Button btnPlanes = new Button("Manage Planes");
        btnPlanes.setMinWidth(200);
        btnPlanes.setOnAction(e -> tabPane.getSelectionModel().select(planes));

        Button btnRes = new Button("View Reservations");
        btnRes.setMinWidth(200);
        btnRes.setOnAction(e -> tabPane.getSelectionModel().select(res));

        Button btnTickets = new Button("Ticket Validator");
        btnTickets.setMinWidth(200);
        btnTickets.setOnAction(e -> tabPane.getSelectionModel().select(tickets));

        Button btnUsers = new Button("Manage Users");
        btnUsers.setMinWidth(200);
        btnUsers.setOnAction(e -> tabPane.getSelectionModel().select(users));

        Button btnStaff = new Button("Staff Directory");
        btnStaff.setMinWidth(200);
        btnStaff.setOnAction(e -> tabPane.getSelectionModel().select(staff));
        
        Button btnScenarios = new Button("Run Scenarios");
        btnScenarios.setMinWidth(200);
        btnScenarios.setOnAction(e -> tabPane.getSelectionModel().select(scenarios));

        Button btnLogout = new Button("Logout");
        btnLogout.setMinWidth(200);
        btnLogout.setStyle("-fx-background-color: #ffcccc; -fx-font-weight: bold;");
        btnLogout.setOnAction(e -> logout(stage));

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        
        layout.getChildren().addAll(welcome, user, new Separator(), 
                                    btnPlanes, btnRes, btnTickets, btnUsers, btnStaff, btnScenarios,
                                    new Separator(), btnLogout);
        return layout;
    }

    private Node createUsersView(Tab tab) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Registered Customer Accounts");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<LoginUser> userList = new ListView<>();

        Runnable refreshData = () -> {
            userList.getItems().clear();
            List<LoginUser> allUsers = AuthService.getAllRegisteredUsers();
            
            boolean found = false;
            for (LoginUser u : allUsers) {
                if (u.getRole() == Role.USER) { 
                    userList.getItems().add(u);
                    found = true;
                }
            }
            if (!found) {
                userList.setPlaceholder(new Label("No registered customers found."));
            }
        };

        refreshData.run();

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshData.run();
        });

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        
        Button btnRefresh = new Button("Refresh List");
        btnRefresh.setOnAction(e -> refreshData.run());

        Button btnRemoveUser = new Button("Remove User");
        btnRemoveUser.setStyle("-fx-background-color: #ffcccc;"); 

        btnRemoveUser.setOnAction(e -> {
            LoginUser selected = userList.getSelectionModel().getSelectedItem();
            
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Delete customer " + selected.getUsername() + "?\nThis cannot be undone.", 
                    ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    boolean success = AuthService.deleteUser(selected.getId());
                    if (success) {
                        refreshData.run();
                        showAlert("Success", "User deleted successfully.");
                    } else {
                        showAlert("Error", "Could not delete user.");
                    }
                }
            } else {
                showAlert("Selection Error", "Please select a user to remove.");
            }
        });

        controls.getChildren().addAll(btnRemoveUser, btnRefresh);
        layout.getChildren().addAll(title, userList, controls);
        return layout;
    }

    private Node createStaffView(Tab tab) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Staff & Admin Directory");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<LoginUser> staffList = new ListView<>();

        Runnable refreshData = () -> {
            staffList.getItems().clear();;
            List<LoginUser> allUsers = AuthService.getAllRegisteredUsers();
            
            for (LoginUser u : allUsers) {
                if (u.getRole() == Role.STAFF || u.getRole() == Role.ADMIN) {
                    staffList.getItems().add(u);
                }
            }
        };

        refreshData.run();

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshData.run();
        });

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        
        Button btnRefresh = new Button("Refresh Directory");
        btnRefresh.setOnAction(e -> refreshData.run());

        Button btnFire = new Button("Fire Staff");
        btnFire.setStyle("-fx-background-color: #ffcccc;");

        btnFire.setOnAction(e -> {
            LoginUser selected = staffList.getSelectionModel().getSelectedItem();
            
            if (selected != null) {
                if (selected.getId().equals(currentUser.getId())) {
                    showAlert("Action Denied", "You cannot delete your own account while logged in.");
                    return;
                }
                
                if (selected.getUsername().equals("admin")) {
                    showAlert("Action Denied", "Cannot delete the Master Admin.");
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Are you sure you want to remove " + selected.getUsername() + "?", 
                    ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    boolean success = AuthService.deleteUser(selected.getId());
                    if (success) {
                        refreshData.run();
                        showAlert("Success", "Staff member removed.");
                    } else {
                        showAlert("Error", "Could not remove staff member.");
                    }
                }
            } else {
                showAlert("Selection Error", "Please select a staff member to remove.");
            }
        });

        controls.getChildren().addAll(btnFire, btnRefresh);
        layout.getChildren().addAll(title, staffList, controls);
        return layout;
    }
    
    private Node createPlanesView(Tab tab) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Fleet Management");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Plane> planeList = new ListView<>();

        Runnable refreshData = () -> {
            context.refreshFleetData();
            
            planeList.getItems().clear();
            List<String> ids = context.getPlaneManager().getAllPlaneIds();
            
            if (ids.isEmpty()) {
                planeList.setPlaceholder(new Label("No planes found in file."));
            } else {
                for (String id : ids) {
                    Plane p = context.getPlaneManager().getPlane(id);
                    if (p != null) {
                        planeList.getItems().add(p);
                    }
                }
            }
        };

        refreshData.run();
        
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshData.run();
        });

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        
        Button btnAdd = new Button("Add Plane");
        btnAdd.setOnAction(e -> {
            showAddPlaneDialog();
            refreshData.run();
        });
        
        Button btnEdit = new Button("Edit Model");
        btnEdit.setOnAction(e -> {
            Plane selectedPlane = planeList.getSelectionModel().getSelectedItem();
            
            if (selectedPlane != null) {
                String targetId = selectedPlane.getPlaneId();

                context.refreshFleetData();

                if (context.getPlaneManager().getPlane(targetId) == null) {
                    showAlert("Sync Error", "This plane was deleted by another user/process.");
                    refreshData.run(); 
                    return; 
                }

                showEditPlaneDialog(targetId);
                refreshData.run();
                
            } else {
                showAlert("Selection Error", "Please select a plane to edit.");
            }
        });
        
        Button btnRemove = new Button("Remove Selected");
        btnRemove.setOnAction(e -> {
            Plane selectedPlane = planeList.getSelectionModel().getSelectedItem();
            
            if (selectedPlane != null) {
                String targetId = selectedPlane.getPlaneId();

                context.refreshFleetData();

                if (context.getPlaneManager().getPlane(targetId) == null) {
                    showAlert("Sync Error", "This plane was already deleted.");
                    refreshData.run();
                    return;
                }

                context.getPlaneManager().deletePlane(targetId, context.getTicketManager());

                FleetService.savePlanes(context.getPlaneManager()); 
                FlightService.saveFlights(context.getFlightManager());
                ReservationService.saveReservations(context.getReservationManager());
                TicketService.saveTickets(context.getTicketManager());
                
                refreshData.run();
                planeList.getSelectionModel().clearSelection();

            } else {
                showAlert("Selection Error", "Please select a plane to remove.");
            }
        });

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshData.run());

        controls.getChildren().addAll(btnAdd, btnEdit, btnRemove, btnRefresh);

        layout.getChildren().addAll(title, planeList, controls);
        return layout;
    }

    private void showAddPlaneDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Add New Plane");
        dialog.setHeaderText("Enter Plane Details & Seat Configuration");

        ButtonType addBtnType = new ButtonType("Add Plane", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField modelField = new TextField();
        modelField.setPromptText("e.g. Boeing 737");

        TextField bussRowField = new TextField("2");
        TextField bussColField = new TextField("2");
        
        TextField econRowField = new TextField("10");
        TextField econColField = new TextField("6");

        grid.add(new Label("Plane Model:"), 0, 0); grid.add(modelField, 1, 0);
        grid.add(new Separator(), 0, 1, 2, 1);
        grid.add(new Label("Business Rows:"), 0, 2); grid.add(bussRowField, 1, 2);
        grid.add(new Label("Business Cols:"), 0, 3); grid.add(bussColField, 1, 3);
        grid.add(new Separator(), 0, 4, 2, 1); 
        grid.add(new Label("Economy Rows:"), 0, 5); grid.add(econRowField, 1, 5);
        grid.add(new Label("Economy Cols:"), 0, 6); grid.add(econColField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                try {
                    String model = modelField.getText();
                    if (model.trim().isEmpty()) {
                        showAlert("Input Error", "Plane model cannot be empty.");
                        return false;
                    }

                    int bRow = Integer.parseInt(bussRowField.getText());
                    int bCol = Integer.parseInt(bussColField.getText());
                    int eRow = Integer.parseInt(econRowField.getText());
                    int eCol = Integer.parseInt(econColField.getText());

                    int[][] arrangement = {{bCol, bRow}, {eCol, eRow}};

                    context.refreshFleetData();

                    FlightManagement.Seating seating = context.getSeatManager().createSeatings(arrangement);
                    boolean success = context.getPlaneManager().createPlane(model, seating);

                    if (success) {
                        FleetService.savePlanes(context.getPlaneManager());
                    }
                    return success;

                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Rows and Columns must be valid integers.");
                    return false;
                } catch (Exception e) {
                    showAlert("Error", "An unexpected error occurred: " + e.getMessage());
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    private void showEditPlaneDialog(String planeId) {
        Plane initialSnapshot = context.getPlaneManager().getPlane(planeId);
        if (initialSnapshot == null) return;

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Plane: " + planeId);
        dialog.setHeaderText("Modify Model & Seat Configuration");

        ButtonType saveBtnType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        int curBRow = 0, curBCol = 0, curERow = 0, curECol = 0;
        
        if (initialSnapshot.getBussSeats() != null && initialSnapshot.getBussSeats().length > 0) {
            curBRow = initialSnapshot.getBussSeats().length;
            curBCol = initialSnapshot.getBussSeats()[0].length;
        }
        if (initialSnapshot.getEconSeats() != null && initialSnapshot.getEconSeats().length > 0) {
            curERow = initialSnapshot.getEconSeats().length;
            curECol = initialSnapshot.getEconSeats()[0].length;
        }

        TextField modelField = new TextField(initialSnapshot.getPlaneModel());
        TextField bussRowField = new TextField(String.valueOf(curBRow));
        TextField bussColField = new TextField(String.valueOf(curBCol));
        TextField econRowField = new TextField(String.valueOf(curERow));
        TextField econColField = new TextField(String.valueOf(curECol));

        grid.add(new Label("Plane Model:"), 0, 0); grid.add(modelField, 1, 0);
        grid.add(new Separator(), 0, 1, 2, 1);
        grid.add(new Label("Business Rows:"), 0, 2); grid.add(bussRowField, 1, 2);
        grid.add(new Label("Business Cols:"), 0, 3); grid.add(bussColField, 1, 3);
        grid.add(new Separator(), 0, 4, 2, 1);
        grid.add(new Label("Economy Rows:"), 0, 5); grid.add(econRowField, 1, 5);
        grid.add(new Label("Economy Cols:"), 0, 6); grid.add(econColField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        final int finalBRow = curBRow; final int finalBCol = curBCol;
        final int finalERow = curERow; final int finalECol = curECol;

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                try {
                    String newModel = modelField.getText();
                    if (newModel.trim().isEmpty()) {
                        showAlert("Input Error", "Model name cannot be empty.");
                        return false;
                    }

                    int bRow = Integer.parseInt(bussRowField.getText());
                    int bCol = Integer.parseInt(bussColField.getText());
                    int eRow = Integer.parseInt(econRowField.getText());
                    int eCol = Integer.parseInt(econColField.getText());

                    context.refreshFleetData();

                    if (context.getPlaneManager().getPlane(planeId) == null) {
                        showAlert("Sync Error", "Operation Failed.\nThis plane was deleted while you were editing it!");
                        return false; 
                    }

                    boolean dimensionsChanged = (bRow != finalBRow) || (bCol != finalBCol) || 
                                                (eRow != finalERow) || (eCol != finalECol);

                    FlightManagement.Seating newSeating = null;

                    if (dimensionsChanged) {
                        int[][] arrangement = {{bCol, bRow}, {eCol, eRow}};
                        newSeating = context.getSeatManager().createSeatings(arrangement);
                    }

                    boolean success = context.getPlaneManager().updatePlane(planeId, newModel, newSeating);

                    if (!success) {
                        showAlert("Update Failed", "Could not update plane.\nIf changing seats, ensure no active reservations exist.");
                        return false;
                    }

                    FleetService.savePlanes(context.getPlaneManager());
                    return true;

                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Rows and Columns must be valid integers.");
                    return false;
                } catch (Exception e) {
                    showAlert("Error", "Unexpected error: " + e.getMessage());
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    private Node createFlightsView(Tab tab) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Flight Schedule Management");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Flight> flightList = new ListView<>();

        Runnable refreshData = () -> {
            context.refreshFlightData();
            
            flightList.getItems().clear();
            List<Flight> flights = context.getFlightManager().getAllFlights();
            if (flights.isEmpty()) {
                flightList.setPlaceholder(new Label("No flights scheduled."));
            } else {
                flightList.getItems().addAll(flights);
            }
        };

        refreshData.run();
        
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshData.run();
        });

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        Button btnAdd = new Button("Schedule Flight");
        btnAdd.setOnAction(e -> {
            showAddFlightDialog();
            refreshData.run();
        });

        Button btnEdit = new Button("Edit Flight");
        btnEdit.setOnAction(e -> {
            Flight selected = flightList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int flightNum = selected.getFlightNum();
                
                context.refreshFlightData();
                
                Flight liveFlight = context.getFlightManager().getFlight(flightNum);
                if (liveFlight == null) {
                    showAlert("Sync Error", "This flight was deleted by another process.");
                    refreshData.run();
                    return;
                }
                
                showEditFlightDialog(liveFlight);
                refreshData.run();
            } else {
                showAlert("Selection Error", "Please select a flight to edit.");
            }
        });

        Button btnRemove = new Button("Cancel Flight");
        btnRemove.setOnAction(e -> {
            Flight selected = flightList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int flightNum = selected.getFlightNum();
                
                context.refreshFlightData();
                
                if (context.getFlightManager().getFlight(flightNum) == null) {
                    showAlert("Sync Error", "Flight already deleted.");
                    refreshData.run();
                    return;
                }
                
                context.getFlightManager().deleteFlight(flightNum);
                
                FlightService.saveFlights(context.getFlightManager());     
                ReservationService.saveReservations(context.getReservationManager());
                TicketService.saveTickets(context.getTicketManager());
                
                refreshData.run();
                flightList.getSelectionModel().clearSelection();
            } else {
                showAlert("Selection Error", "Please select a flight to cancel.");
            }
        });

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshData.run());

        controls.getChildren().addAll(btnAdd, btnEdit, btnRemove, btnRefresh);
        layout.getChildren().addAll(title, flightList, controls);
        return layout;
    }

    private void showAddFlightDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Schedule New Flight");
        dialog.setHeaderText("Enter Flight Details");

        ButtonType addBtn = new ButtonType("Schedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> planeBox = new ComboBox<>();
        context.refreshFleetData(); 
        planeBox.getItems().addAll(context.getPlaneManager().getAllPlaneIds());
        
        TextField depField = new TextField(); depField.setPromptText("IST");
        TextField arrField = new TextField(); arrField.setPromptText("LHR");
        
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("31/12/2025");
        configureDatePicker(datePicker); 

        TextField hourField = new TextField(); hourField.setPromptText("0-23");
        TextField durField = new TextField(); durField.setPromptText("Minutes");

        grid.add(new Label("Assigned Plane:"), 0, 0); grid.add(planeBox, 1, 0);
        grid.add(new Label("Departure:"), 0, 1); grid.add(depField, 1, 1);
        grid.add(new Label("Arrival:"), 0, 2); grid.add(arrField, 1, 2);
        grid.add(new Label("Date (dd/MM/yyyy):"), 0, 3); grid.add(datePicker, 1, 3);
        grid.add(new Label("Hour:"), 0, 4); grid.add(hourField, 1, 4);
        grid.add(new Label("Duration (min):"), 0, 5); grid.add(durField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                try {
                    String planeId = planeBox.getValue();
                    String dep = depField.getText();
                    String arr = arrField.getText();
                    LocalDate localDate = datePicker.getValue();
                    
                    if (planeId == null || dep.isEmpty() || arr.isEmpty() || localDate == null) {
                        showAlert("Error", "All fields are required.");
                        return false;
                    }

                    String dateStr = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    int hour = Integer.parseInt(hourField.getText());
                    int dur = Integer.parseInt(durField.getText());

                    context.refreshFlightData();

                    Route route = new Route(dep, arr);
                    boolean success = context.getFlightManager().createFlight(planeId, route, dateStr, hour, dur);

                    if (success) {
                        FlightService.saveFlights(context.getFlightManager());
                    } else {
                        showAlert("Error", "Flight creation failed (Conflict or Plane not found).");
                        return false;
                    }
                    return success;

                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Hour and Duration must be numbers.");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }
    
    private void showEditFlightDialog(Flight flight) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Flight: " + flight.getFlightNum());
        dialog.setHeaderText("Modify Flight Details");

        ButtonType saveBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> planeBox = new ComboBox<>();
        context.refreshFleetData();
        planeBox.getItems().addAll(context.getPlaneManager().getAllPlaneIds());
        planeBox.setValue(flight.getPlane().getPlaneId());

        TextField depField = new TextField(flight.getRoute().getDeparturePlace());
        TextField arrField = new TextField(flight.getRoute().getArrivalPlace());
        
        DatePicker datePicker = new DatePicker();
        configureDatePicker(datePicker);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate currentData = LocalDate.parse(flight.getDate(), formatter);
            datePicker.setValue(currentData);
        } catch (Exception e) {
        }

        TextField hourField = new TextField(String.valueOf(flight.getHour()));
        TextField durField = new TextField(String.valueOf(flight.getDuration()));

        grid.add(new Label("Assigned Plane:"), 0, 0); grid.add(planeBox, 1, 0);
        grid.add(new Label("Departure:"), 0, 1); grid.add(depField, 1, 1);
        grid.add(new Label("Arrival:"), 0, 2); grid.add(arrField, 1, 2);
        grid.add(new Label("Date (dd/MM/yyyy):"), 0, 3); grid.add(datePicker, 1, 3);
        grid.add(new Label("Hour:"), 0, 4); grid.add(hourField, 1, 4);
        grid.add(new Label("Duration (min):"), 0, 5); grid.add(durField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    String planeId = planeBox.getValue();
                    String dep = depField.getText();
                    String arr = arrField.getText();
                    LocalDate localDate = datePicker.getValue();
                    
                    if(localDate == null) {
                        showAlert("Error", "Date is required.");
                        return false;
                    }
                    
                    String dateStr = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    int hour = Integer.parseInt(hourField.getText());
                    int dur = Integer.parseInt(durField.getText());

                    context.refreshFlightData();

                    boolean success = context.getFlightManager().updateFlight(
                        flight.getFlightNum(), 
                        planeId, 
                        dep, 
                        arr, 
                        dateStr, 
                        hour, 
                        dur
                    );

                    if (success) {
                        FlightService.saveFlights(context.getFlightManager());
                        return true;
                    } else {
                        showAlert("Error", "Update failed. Check inputs (e.g. valid Plane ID, Hour 0-23).");
                        return false;
                    }

                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Hour and Duration must be numbers.");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    private void configureDatePicker(DatePicker datePicker) {
        String pattern = "dd/MM/yyyy";
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            @Override public String toString(LocalDate date) { return (date != null) ? dateFormatter.format(date) : ""; }
            @Override public LocalDate fromString(String string) { return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null; }
        });
    }
    
    private Node createReservationsView(Tab tab) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("All Reservations");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Reservation> resList = new ListView<>();

        Runnable refreshData = () -> {
            context.refreshBookingData();
            
            resList.getItems().clear();
            for (Reservation res : context.getReservationManager().getAllReservations()) {
                resList.getItems().add(res);
            }
            if (resList.getItems().isEmpty()) {
                resList.setPlaceholder(new Label("No active reservations found."));
            }
        };
        
        refreshData.run();
        
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshData.run();
        });

        // Controls
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        Button btnRefresh = new Button("Refresh List");
        btnRefresh.setOnAction(e -> refreshData.run());

        Button btnCancel = new Button("Cancel Reservation");
        btnCancel.setStyle("-fx-background-color: #ffcccc;");
        
        btnCancel.setOnAction(e -> {
            Reservation selected = resList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String code = selected.getReservationCode();
                
                boolean removed = context.getReservationManager().cancelReservation(code);
                
                if (removed) {
                    ReservationService.saveReservations(context.getReservationManager());
                    TicketService.saveTickets(context.getTicketManager());
                    
                    refreshData.run();
                    showAlert("Success", "Reservation " + code + " has been cancelled.");
                } else {
                    showAlert("Error", "Could not cancel reservation.");
                }
            } else {
                showAlert("Selection Error", "Please select a reservation to cancel.");
            }
        });

        controls.getChildren().addAll(btnCancel, btnRefresh);
        layout.getChildren().addAll(title, resList, controls);
        return layout;
    }
    private Node createTicketsView(Tab tab) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Issued Tickets");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Ticket> ticketList = new ListView<>();

        Runnable refreshData = () -> {
            context.refreshBookingData();
            
            // 2. Update UI
            ticketList.getItems().clear();
            Iterable<Ticket> tickets = context.getTicketManager().getAllTickets();
            
            int ticketCount = 0;
            for (Ticket t : tickets) {
                ticketList.getItems().add(t);
                ticketCount++;
            }
            if(ticketCount == 0) {
                ticketList.setPlaceholder(new Label("No tickets issued yet."));
            }
        };

        refreshData.run();
        
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) refreshData.run();
        });

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        Button btnRevoke = new Button("Revoke Ticket");
        btnRevoke.setStyle("-fx-background-color: #ffcccc;"); 
        
        btnRevoke.setOnAction(e -> {
            Ticket selected = ticketList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String id = selected.getTicketId();

                context.refreshBookingData();
                
                if (context.getTicketManager().getTicket(id) == null) {
                    showAlert("Sync Error", "Ticket already revoked.");
                    refreshData.run();
                    return;
                }
                
                context.getTicketManager().deleteTicket(id);
                TicketService.saveTickets(context.getTicketManager());
                
                refreshData.run();
            } else {
                showAlert("Selection Error", "Please select a ticket to revoke.");
            }
        });
        
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshData.run());

        controls.getChildren().addAll(btnRevoke, btnRefresh);
        layout.getChildren().addAll(title, ticketList, controls);
        return layout;
    }

    private Node createPriceSettingsView(Tab tab) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Global Price Configuration");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        TextField baseField = new TextField();
        TextField durField = new TextField();
        TextField bagField = new TextField();
        TextField taxField = new TextField();
        TextField multField = new TextField();

        int row = 0;
        grid.add(new Label("Base Flight Price:"), 0, row); grid.add(baseField, 1, row++);
        grid.add(new Label("Duration Cost (per min):"), 0, row); grid.add(durField, 1, row++);
        grid.add(new Label("Baggage Fee (per kg):"), 0, row); grid.add(bagField, 1, row++);
        grid.add(new Label("Fixed Tax:"), 0, row); grid.add(taxField, 1, row++);
        grid.add(new Label("Business Class Multiplier:"), 0, row); grid.add(multField, 1, row++);

        Runnable loadData = () -> {
            CalculatePrice current = PriceCalculationService.loadCalculator();
            
            baseField.setText(String.valueOf(current.getBasePrice()));
            durField.setText(String.valueOf(current.getDurationCostPerMinute()));
            bagField.setText(String.valueOf(current.getBaggageFeePerKg()));
            taxField.setText(String.valueOf(current.getTax()));
            multField.setText(String.valueOf(current.getBusinessMultiplier()));
        };

        loadData.run();
        
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) loadData.run();
        });

        Button btnSave = new Button("Save Configuration");
        btnSave.setStyle("-fx-background-color: #d4efdf; -fx-font-weight: bold;");
        btnSave.setMinWidth(200);

        btnSave.setOnAction(e -> {
            try {
                double base = Double.parseDouble(baseField.getText());
                double dur = Double.parseDouble(durField.getText());
                double bag = Double.parseDouble(bagField.getText());
                double tax = Double.parseDouble(taxField.getText());
                double mult = Double.parseDouble(multField.getText());

                if (base < 0 || dur < 0 || bag < 0 || tax < 0 || mult < 1) {
                    showAlert("Input Error", "Values cannot be negative. Multiplier must be >= 1.");
                    return;
                }

                boolean saved = PriceCalculationService.saveParameters(base, tax, bag, mult, dur);

                if (saved) {
                	context.setCalculatePrice(PriceCalculationService.loadCalculator());
                	context.setContexts();
                    
                    showAlert("Success", "Price parameters updated successfully!");
                } else {
                    showAlert("Error", "Could not save parameters to file.");
                }

            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter valid numbers.");
            }
        });

        layout.getChildren().addAll(title, grid, new Separator(), btnSave);
        return layout;
    }
    
    private Node createScenariosView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Simulation Scenarios");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Button btnScenario1 = new Button("Launch Scenario 1");
        btnScenario1.setMinWidth(250);
        btnScenario1.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        btnScenario1.setOnAction(e -> openScenario1Window());

        Button btnScenario2 = new Button("Launch Scenario 2");
        btnScenario2.setMinWidth(250);
        btnScenario2.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        btnScenario2.setOnAction(e -> openScenario2Window());

        layout.getChildren().addAll(title, new Separator(), btnScenario1, btnScenario2);
        return layout;
    }
    
    private void openScenario1Window() {
        Stage stage = new Stage();
        stage.setTitle("Scenario 1");
        
        Node view = createScenario1View(); 
        
        Scene scene = new Scene((javafx.scene.Parent) view, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    private Node createScenario1View() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f8ff;");

        VBox resultContainer = new VBox(15);
        resultContainer.setAlignment(Pos.TOP_CENTER);
        resultContainer.setPadding(new Insets(20));

        CheckBox chkSync = new CheckBox("Enable Synchronization (Safe Mode)");
        chkSync.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        chkSync.setSelected(true);

        Label lblStats = new Label("Occupied: 0 | Empty: 180");
        lblStats.setStyle("-fx-font-weight: bold;");

        VBox topControls = new VBox(10);
        topControls.setPadding(new Insets(15));
        topControls.setAlignment(Pos.CENTER);
        topControls.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        Label header = new Label("Scenario 1: Seat Reservation Simulation");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00008B;");

        Label desc = new Label("90 Passengers attempting to book 180 seats simultaneously.");

        Button btnRun = new Button("Run Simulation");
        btnRun.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRun.setMinWidth(150);

        btnRun.setOnAction(e -> {
            boolean isSync = chkSync.isSelected();

            Plane simulatedPlane = Scenario1.start(isSync);

            PlaneMapRenderer.renderSimulationMap(simulatedPlane, resultContainer);

            int[] seats = simulatedPlane.availableSeats();
            int totalEmpty = seats[0] + seats[1];
            int totalOccupied = 180 - totalEmpty;

            lblStats.setText(String.format("Occupied: %d | Empty: %d | Mode: %s",
                    totalOccupied, totalEmpty, (isSync ? "Sync" : "Async")));

            if (isSync && totalOccupied == 90) {
                lblStats.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else if (!isSync && totalOccupied != 90) {
                lblStats.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                lblStats.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
            }
        });

        topControls.getChildren().addAll(header, desc, new Separator(), chkSync, btnRun);
        root.setTop(topControls);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: #f0f8ff;");
        
        Label lblPlaceholder = new Label("Press 'Run Simulation' to see results.");
        lblPlaceholder.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
        resultContainer.getChildren().add(lblPlaceholder);
        
        scrollPane.setContent(resultContainer);
        root.setCenter(scrollPane);

        HBox bottomPanel = new HBox(20);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");

        bottomPanel.getChildren().addAll(
                lblStats,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                PlaneMapRenderer.createLegendItem("Occupied", "#F44336"),
                PlaneMapRenderer.createLegendItem("Empty", "#4CAF50")
        );
        root.setBottom(bottomPanel);

        return root;
    }

    private void openScenario2Window() {
        Stage stage = new Stage();
        stage.setTitle("Scenario 2");
        Node view = createScenario2View();
        Scene scene = new Scene((javafx.scene.Parent) view, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    private Node createScenario2View() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #fcf3cf;");
        root.setPadding(new Insets(20));

        // --- Header Section ---
        Label header = new Label("Scenario 2: Mass Simulation Report");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #d35400;");

        Label desc = new Label("Simulates randomly assigned 3,000,000 passengers and 500,000 flights.\n" +
                               "Calculates total revenue in a background thread.");
        desc.setStyle("-fx-text-alignment: left; -fx-text-fill: #555;");

        Button btnRun = new Button("Run Large Simulation");
        btnRun.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnRun.setMinWidth(200);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(25, 25);
        spinner.setVisible(false);

        Label statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-font-style: italic;");

        HBox controls = new HBox(15, btnRun, spinner, statusLabel);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15));

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPromptText("Results will appear here...");
        reportArea.setFont(javafx.scene.text.Font.font("Monospaced", 13));

        root.setTop(new VBox(10, header, desc, new Separator(), controls));
        root.setCenter(reportArea);

        btnRun.setOnAction(e -> {
            btnRun.setDisable(true);
            spinner.setVisible(true);
            reportArea.clear();
            statusLabel.setText("Preparing report... (This may take a while)");
            statusLabel.setStyle("-fx-text-fill: #d35400; -fx-font-weight: bold;");

            java.util.function.Consumer<String> onFinished = (report) -> {
                javafx.application.Platform.runLater(() -> {
                    reportArea.setText(report);
                    statusLabel.setText("Simulation Complete.");
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    btnRun.setDisable(false);
                    spinner.setVisible(false);
                });
            };

            Scenario2 simulationTask = new Scenario2(onFinished);
            Thread thread = new Thread(simulationTask);
            thread.setDaemon(true); // Ensure thread dies if app closes
            thread.start();
        });

        return root;
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
    
    private String parseIdFromString(String selection) {
        try {
            int start = selection.indexOf("planeId='") + 9;
            int end = selection.indexOf("'", start);
            return selection.substring(start, end);
        } catch (Exception e) {
            return "";
        }
    }
}