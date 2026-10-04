package com.school.ui;

import com.school.dao.EnrollmentDAO;
import com.school.dao.StudentDAO;
import com.school.model.Enrollment;
import com.school.model.Student;
import com.school.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class StudentDashboard {

    private final Stage stage;
    private final User currentUser;
    private final Student student;
    private final BorderPane root;

    private final StudentDAO studentDAO = new StudentDAO();
    private final EnrollmentDAO enrollmentDAO  = new EnrollmentDAO();
    private final ObservableList<Enrollment> myEnrollments = FXCollections.observableArrayList();

    public StudentDashboard(Stage stage, User user){
        this.stage = stage;
        this.currentUser = user;
        this.student = studentDAO.getStudentById(user.getStudentId());

        if(student == null){
            throw new IllegalStateException(
                    "No student record linked to user " + user.getUsername());
        }
        this.root = buildUI();
    }
    public  Parent getRoot() {return root;}

    private BorderPane buildUI() {
        BorderPane layout = new BorderPane();
        layout.setTop(buildHeader());
        layout.setCenter(buildContent());
        return layout;
    }

    //HEADER
    private HBox buildHeader() {
        Label appName = new Label("🎓 Student Portal");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label(student.getName() + "  •  " + currentUser.getRole());
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

    //center content
    private VBox buildContent() {
        // Load enrollments
        List<Enrollment> list = enrollmentDAO.getEnrollmentsByStudent(student.getId());
        myEnrollments.setAll(list);

        // Profile card
        StudentProfileView profile = new StudentProfileView(student);

        // Courses section header row
        Label coursesHeading = new Label("My Courses (" + myEnrollments.size() + ")");
        coursesHeading.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label gwaLabel = new Label();
        gwaLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #2c3e50;");
        updateGwaLabel(gwaLabel);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox coursesHeaderRow = new HBox(10, coursesHeading, headerSpacer, gwaLabel);
        coursesHeaderRow.setAlignment(Pos.CENTER_LEFT);

        // Courses table
        TableView<Enrollment> table = buildCoursesTable();
        table.setPrefHeight(300);            // ← ensure it has a visible minimum size
        VBox coursesBox = new VBox(10, coursesHeaderRow, table);
        coursesBox.setPadding(new Insets(15));
        coursesBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e1e4e8; " +
                        "-fx-border-radius: 6; " +
                        "-fx-background-radius: 6;"
        );
        VBox.setVgrow(table, Priority.ALWAYS);

        // Outer VBox holds profile + courses section
        VBox center = new VBox(20, profile, coursesBox);
        center.setPadding(new Insets(20));
        VBox.setVgrow(coursesBox, Priority.ALWAYS);  // ← coursesBox grows inside center
        return center;
    }

    private TableView<Enrollment> buildCoursesTable() {
        TableView<Enrollment> table = new TableView<>();
        table.setItems(myEnrollments);
        table.setPlaceholder(new Label("You are not enrolled in any courses yet."));

        TableColumn<Enrollment, String> codeCol = new TableColumn<>();
        codeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCourseCode()));
        codeCol.setPrefWidth(100);

        TableColumn<Enrollment, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCourseName()));
        nameCol.setPrefWidth(320);

        TableColumn<Enrollment, Integer> creditsCol = new TableColumn<>("Credits");
        creditsCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCourseCredits()));
        creditsCol.setPrefWidth(80);

        TableColumn<Enrollment, String> gradeCol = new TableColumn<>("Grade");

        gradeCol.setCellValueFactory(cell -> {
            BigDecimal g = cell.getValue().getGrade();
            return new SimpleStringProperty(g == null ? "-" : g.toPlainString());
        });
        gradeCol.setPrefWidth(80);

        TableColumn<Enrollment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> {
            BigDecimal g = cell.getValue().getGrade();
            String text = g == null ? "In progress" : (g.compareTo(new BigDecimal("3.00")) <= 0 ? "Passed " : "Failed");
            return new SimpleStringProperty(text);
        });
        statusCol.setPrefWidth(110);

        table.getColumns().addAll(codeCol, nameCol, creditsCol, gradeCol, statusCol);
        return table;
    }

    //GWA CALCULATION

    private void updateGwaLabel(Label gwaLabel){

        BigDecimal gwa = computeGwa();
        gwaLabel.setText(gwa == null ? "GWA: —" : "GWA: " + gwa.toPlainString());

    }

    private  BigDecimal computeGwa(){
        BigDecimal weighted = BigDecimal.ZERO;
        int totalCredits = 0;

        for (Enrollment e : myEnrollments){
            if (e.getGrade() == null) continue;
            weighted = weighted.add(e.getGrade().multiply(BigDecimal.valueOf(e.getCourseCredits()))
            );
            totalCredits += e.getCourseCredits();
        }
        if (totalCredits == 0) return null;

        return weighted.divide(
                BigDecimal.valueOf(totalCredits),
                2,
                RoundingMode.HALF_UP
        );
    }

    //LOGOUT
    private void handleLogout(){
        LoginView login = new LoginView(stage);
        Scene scene = new Scene(login.getRoot(), 420, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Student Management System");
        stage.setResizable(false);
    }




}
