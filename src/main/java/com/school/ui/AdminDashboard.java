package com.school.ui;

import com.school.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class AdminDashboard {

    private final Stage stage;
    private final User currentUser;
    private final BorderPane root;

    public AdminDashboard(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.root = buildUI();
    }

    public Parent getRoot() {
        return root;
    }

    private BorderPane buildUI() {
        BorderPane layout = new BorderPane();
        layout.setTop(buildHeader());
        layout.setCenter(buildTabs());
        return layout;
    }

    //Header (top bar)
    private HBox buildHeader() {
        Label appName = new Label(" Student Management System");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); //push next item to right

        Label userInfo = new Label(currentUser.getUsername() + " • " + currentUser.getRole());
        userInfo.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> handleLogout());

        HBox header = new HBox(15, appName, spacer, userInfo, logoutBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2c3e50;");
        return header;
    }

    //TABS (center)
    private TabPane buildTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab studentsTab = new Tab("Students");
        studentsTab.setContent(buildStudentsTab());

        Tab courseTab = new Tab("Courses");
        courseTab.setContent(placeholder("Courses"));

        Tab enrollmentsTab = new Tab("Enrollments");
        enrollmentsTab.setContent(placeholder("Enrollments"));
        tabPane.getTabs().addAll(studentsTab, courseTab, enrollmentsTab);
        return tabPane;
    }

    private VBox placeholder(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #888; -fx-font-size: 14;");
        VBox box = new VBox(lbl);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    //Students tab
    private VBox buildStudentsTab() {
        // Filled in next sub-step
        return new VBox();
    }

    private void handleLogout() {
        LoginView loginView = new LoginView(stage);
        javafx.scene.Scene scene = new javafx.scene.Scene(loginView.getRoot(), 420, 500);
        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Student Management System");
    }
}