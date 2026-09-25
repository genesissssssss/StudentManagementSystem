package com.school.ui;

import com.school.model.User;
import com.school.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {

    private final Stage stage;
    private final AuthService authService = new AuthService();
    private final VBox root;

    // UI components we need to access from other methods
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label errorLabel = new Label();

    public LoginView(Stage stage) {
        this.stage = stage;
        this.root = buildUI();
    }

    /**
     * The main layout — returned to Main.java to place in a Scene.
     */
    public Parent getRoot() {
        return root;
    }

    private VBox buildUI() {
        // --- Title ---
        Label title = new Label("🎓 Student Management");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        Label subtitle = new Label("Sign in to continue");
        subtitle.setFont(Font.font("System", 13));
        subtitle.setStyle("-fx-text-fill: #666;");

        // --- Username ---
        Label userLabel = new Label("Username");
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(38);

        // --- Password ---
        Label passLabel = new Label("Password");
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(38);

        // --- Error label (hidden until login fails) ---
        errorLabel.setStyle("-fx-text-fill: #d32f2f;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);  // don't take up space when hidden

        // --- Login button ---
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(300);
        loginButton.setPrefHeight(42);
        loginButton.setDefaultButton(true);  // Enter key triggers this
        loginButton.setOnAction(e -> handleLogin());

        // --- Assemble vertically ---
        VBox box = new VBox(10);   // 10 px spacing between children
        box.setPadding(new Insets(50, 60, 50, 60));
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(
                title,
                subtitle,
                spacer(20),
                userLabel, usernameField,
                spacer(10),
                passLabel, passwordField,
                spacer(10),
                errorLabel,
                spacer(10),
                loginButton
        );
        return box;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = authService.login(username, password);

        if (user == null) {
            showError("Invalid username or password");
            return;
        }


        System.out.println("Logged in as: " + user);

        if (user.isAdmin()) {
            AdminDashboard dashboard = new AdminDashboard(stage, user);
            javafx.scene.Scene scene = new javafx.scene.Scene(dashboard.getRoot(), 1000, 700);
            scene.getStylesheets().add(
                    getClass().getResource("/style.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard — " + user.getUsername());
            stage.setResizable(true);
        } else {
            // Student dashboard comes in a later stage
            showError("Student dashboard coming soon");
        }
    }

        private void showError (String message){
            errorLabel.setText("⚠ " + message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }

        private VBox spacer ( double height){
            VBox v = new VBox();
            v.setPrefHeight(height);
            return v;
        }
    }
