package com.school.ui;

import com.school.model.Student;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import java.time.LocalDate;

public class StudentDialog extends Dialog<Student> {

    private final TextField nameField  = new TextField();
    private final TextField emailField = new TextField();
    private final TextField phoneField = new TextField();
    private final DatePicker dobPicker = new DatePicker();
    private final DatePicker enrollPicker = new DatePicker();

    private final Label errorLabel = new Label();

    private final Student existing;  // null when adding

    public StudentDialog(Window owner, Student existing) {
        this.existing = existing;

        initOwner(owner);
        setTitle(existing == null ? "Add Student" : "Edit Student");
        setHeaderText(null);

        // Build content
        getDialogPane().setContent(buildForm());
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        // Style the dialog pane with CSS
        getDialogPane().getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        // Rename "OK" to "Save" and style buttons
        Button okBtn = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Save");
        okBtn.getStyleClass().add("primary-button");

        // Validate before closing — event filter stops the OK button if invalid
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateAndShowErrors()) {
                event.consume();  // stops the dialog from closing
            }
        });

        // Pre-fill fields when editing
        if (existing != null) {
            nameField.setText(existing.getName());
            emailField.setText(existing.getEmail());
            phoneField.setText(existing.getPhone());
            dobPicker.setValue(existing.getDateOfBirth());
            enrollPicker.setValue(existing.getEnrollmentDate());
        } else {
            enrollPicker.setValue(LocalDate.now());  // default for new students
        }

        // Convert dialog result to a Student object
        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return buildStudentFromFields();
            }
            return null;  // Cancel
        });
    }

    // ---------- Form layout ----------
    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        nameField.setPrefWidth(300);
        emailField.setPrefWidth(300);
        phoneField.setPrefWidth(300);
        dobPicker.setPrefWidth(150);
        enrollPicker.setPrefWidth(150);

        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Row, col, node
        grid.add(new Label("Name *"),         0, 0);
        grid.add(nameField,                   1, 0);

        grid.add(new Label("Email *"),        0, 1);
        grid.add(emailField,                  1, 1);

        grid.add(new Label("Phone"),          0, 2);
        grid.add(phoneField,                  1, 2);

        grid.add(new Label("Date of Birth"),  0, 3);
        grid.add(dobPicker,                   1, 3);

        grid.add(new Label("Enrollment Date *"), 0, 4);
        grid.add(enrollPicker,                1, 4);

        // Error label spans both columns
        grid.add(errorLabel, 0, 5, 2, 1);  // (col, row, colspan, rowspan)

        return grid;
    }

    // Validation
    private boolean validateAndShowErrors() {
        StringBuilder errors = new StringBuilder();

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty()) {
            errors.append("• Name is required\n");
        }
        if (email.isEmpty()) {
            errors.append("• Email is required\n");
        } else if (!isValidEmail(email)) {
            errors.append("• Email format looks invalid\n");
        }
        if (enrollPicker.getValue() == null) {
            errors.append("• Enrollment date is required\n");
        }
        if (dobPicker.getValue() != null && dobPicker.getValue().isAfter(LocalDate.now())) {
            errors.append("• Date of birth can't be in the future\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString().trim());
            return false;
        }
        hideError();
        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    // Convert form → Student
    private Student buildStudentFromFields() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        LocalDate enroll = enrollPicker.getValue();

        if (existing != null) {
            // EDIT: mutate the existing object (keeps its ID)
            existing.setName(name);
            existing.setEmail(email);
            existing.setPhone(phone);
            existing.setDateOfBirth(dob);
            existing.setEnrollmentDate(enroll);
            return existing;
        } else {
            // ADD: create a fresh Student
            return new Student(name, email, phone, dob, enroll);
        }
    }
}