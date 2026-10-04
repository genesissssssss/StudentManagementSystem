package com.school.ui;

import com.school.model.Student;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;

public class StudentProfileView extends VBox {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy");

    public StudentProfileView(Student s) {
        setSpacing(10);
        setPadding(new Insets(15));

        Label heading = new Label("My Profile");
        heading.setFont(Font.font("System", FontWeight.BOLD, 16));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(10, 0, 0, 0));

        addRow(grid, 0, "Name",      s.getName());
        addRow(grid, 1, "Email",     s.getEmail());
        addRow(grid, 2, "Phone",     s.getPhone() == null ? "—" : s.getPhone());
        addRow(grid, 3, "Birthdate", s.getDateOfBirth() == null ? "—"
                : s.getDateOfBirth().format(DATE_FMT));
        addRow(grid, 4, "Enrolled",  s.getEnrollmentDate() == null ? "—"
                : s.getEnrollmentDate().format(DATE_FMT));

        getChildren().addAll(heading, grid);
        setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e1e4e8; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6;");
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label key = new Label(label + ":");
        key.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");
        key.setMinWidth(80);   // ← ensures column width consistency

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 13; -fx-text-fill: #222;");

        grid.add(key, 0, row);
        grid.add(val, 1, row);
    }
}