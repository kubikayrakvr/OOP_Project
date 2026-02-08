package GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import Managers.FlightSystemContext;
import Services.AuthService;
import Services.FileHelper;

public class LoginApp extends Application {
    private static final String FILE_PATH = "resim.jpg";
    
    static {
        FileHelper.ensureFileExists(FILE_PATH);
    }
    
    @Override
    public void start(Stage stage) {
    	FlightSystemContext context = new FlightSystemContext();
    	context.setContexts();
    	
    	context.refreshAllData();
    	
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250); 

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold;");
        loginBtn.setMinWidth(120);
        
        Hyperlink forgotPassLink = new Hyperlink("Forgot Password?");
        forgotPassLink.setStyle("-fx-text-fill: #333;"); 
        forgotPassLink.setOnAction(e -> openForgotPasswordWindow());

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            LoginUser user = AuthService.login(username, password);

            if (user == null) {
                showAlert("Login Failed", "Invalid username or password");
                return;
            }

            try {
                if (user.getRole() == Role.ADMIN) new AdminMenu(user, context).start(new Stage());
                else if (user.getRole() == Role.STAFF) new StaffMenu(user, context).start(new Stage());
                else new UserMenu(user, context).start(new Stage());

                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button registerBtn = new Button("Register New Account");
        registerBtn.setMaxWidth(250);
        registerBtn.setOnAction(e -> openRegisterWindow());

        VBox rootLayout = new VBox();
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.setPadding(new Insets(20));

        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(350);
        
        formContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.85); " + 
            "-fx-background-radius: 10; " + 
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"
        );

        Label titleLabel = new Label("Flight System Login");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        formContainer.getChildren().addAll(
                titleLabel,
                new Separator(),
                new Label("Username:"),
                usernameField,
                new Label("Password:"),
                passwordField,
                new Label(""),
                loginBtn,
                forgotPassLink,
                new Separator(),
                registerBtn
        );

        rootLayout.getChildren().add(formContainer);


        
        Image image;
        File externalImage = new File(FILE_PATH);

        if (externalImage.exists()) {
            // Load from the file extracted to the Desktop/Folder
            image = new Image(externalImage.toURI().toString());
        } else {
            // Fallback: Load directly from the JAR root
            InputStream is = getClass().getResourceAsStream("/" + FILE_PATH);
            if (is == null) {
                throw new RuntimeException("Resource not found: " + FILE_PATH);
            }
            image = new Image(is);
        }

        BackgroundImage bgImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, false, false)
        );
        
        rootLayout.setBackground(new Background(bgImage));

        Scene scene = new Scene(rootLayout, 600, 500); 
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    private void openForgotPasswordWindow() {
        Stage resetStage = new Stage();
        resetStage.setTitle("Reset Password");
        resetStage.initModality(Modality.APPLICATION_MODAL);

        TextField userField = new TextField();
        userField.setPromptText("Enter your Username");
        Button findUserBtn = new Button("Find User");

        Label questionLabel = new Label();
        TextField answerField = new TextField();
        answerField.setPromptText("Security Answer");
        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("New Password");
        Button resetBtn = new Button("Reset Password");

        questionLabel.setVisible(false);
        answerField.setVisible(false);
        newPassField.setVisible(false);
        resetBtn.setVisible(false);

        findUserBtn.setOnAction(e -> {
            String username = userField.getText();
            String question = AuthService.getSecurityQuestionForUser(username);

            if (question != null) {
                questionLabel.setText("Question: " + question);
                questionLabel.setVisible(true);
                answerField.setVisible(true);
                newPassField.setVisible(true);
                resetBtn.setVisible(true);
                userField.setEditable(false);
            } else {
                showAlert("Error", "User not found.");
            }
        });

        resetBtn.setOnAction(e -> {
            boolean success = AuthService.resetPassword(
                userField.getText(), 
                answerField.getText(), 
                newPassField.getText()
            );

            if (success) {
                showAlert("Success", "Password updated! Please login.");
                resetStage.close();
            } else {
                showAlert("Failed", "Incorrect Security Answer.");
            }
        });

        VBox layout = new VBox(10, 
            new Label("Reset Password"),
            userField, findUserBtn,
            new Separator(),
            questionLabel, answerField, newPassField, resetBtn
        );
        layout.setPadding(new Insets(20));

        resetStage.setScene(new Scene(layout, 300, 350));
        resetStage.show();
    }

    private void openRegisterWindow() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register");
        registerStage.initModality(Modality.APPLICATION_MODAL);

        TextField regUserField = new TextField();
        regUserField.setPromptText("Choose a Username");

        PasswordField regPassField = new PasswordField();
        regPassField.setPromptText("Choose a Password");
        
        ComboBox<String> questionBox = new ComboBox<>();
        questionBox.getItems().addAll(
            "What is your pet's name?",
            "What city were you born in?",
            "What is your favorite color?"
        );
        questionBox.setPromptText("Select Security Question");
        
        TextField answerField = new TextField();
        answerField.setPromptText("Security Answer");

        Label roleLabel = new Label("Select Account Type:");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("User", "Staff"); 
        roleBox.setValue("User");

        Button submitBtn = new Button("Create Account");

        submitBtn.setOnAction(e -> {
            if(questionBox.getValue() == null || answerField.getText().isEmpty()) {
                showAlert("Error", "Please complete security questions.");
                return;
            }

            String selectedRoleString = roleBox.getValue();
            Role selectedRole = selectedRoleString.equals("Staff") ? Role.STAFF : Role.USER;

            LoginUser newUser = new LoginUser(
                    regUserField.getText(),
                    regPassField.getText(),
                    selectedRole,
                    "ID-" + System.currentTimeMillis()
            );

            RegistrationStatus status = AuthService.register(
                newUser, 
                questionBox.getValue(), 
                answerField.getText()
            );

            switch (status) {
                case SUCCESS:
                    showAlert("Success", "Account registered! Please Login.");
                    registerStage.close();
                    break;
                case INVALID_CHARACTERS:
                    showAlert("Error", "Username cannot contain special characters.");
                    break;
                case USERNAME_EXISTS:
                    showAlert("Error", "Username already taken.");
                    break;
                case FILE_ERROR:
                    showAlert("Error", "System error saving data.");
                    break;
            }
        });

        VBox layout = new VBox(10,
                new Label("Create New Account"),
                roleLabel, roleBox,
                regUserField, regPassField,
                questionBox, answerField, 
                submitBtn
        );
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 300, 400); 
        registerStage.setScene(scene);
        registerStage.show();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}