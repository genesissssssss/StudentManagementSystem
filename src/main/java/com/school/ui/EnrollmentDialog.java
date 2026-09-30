package com.school.ui;

import com.school.dao.CourseDAO;
import com.school.dao.StudentDAO;
import com.school.model.Course;
import com.school.model.Enrollment;
import com.school.model.Student;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.math.BigDecimal;

public class EnrollmentDialog extends Dialog<Enrollment> {

    private final ComboBox<Student> studentCombo = new ComboBox<>();
    private final ComboBox<Course> courseCombo = new ComboBox<>();
    private final TextField gradeField = new TextField();

    private final Label errorLabel = new Label();
    private final Enrollment existing;

    public EnrollmentDialog(Window owner, Enrollment existing) {
        this.existing = existing;

        initOwner(owner);
        setTitle(existing == null ? "Enroll Student" : "Edit Enrollment");
        setHeaderText(null);

        getDialogPane().setContent(buildForm());
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        getDialogPane().getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        Button okBtn = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Save");
        okBtn.getStyleClass().add("primary-button");

        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateAndShowErrors()) event.consume();
        });

        loadDropdowns();

        // Editing: lock the student/course (can't change enrollment target)
        if (existing != null) {
            studentCombo.setValue(findStudent(existing.getStudentId()));
            courseCombo.setValue(findCourse(existing.getCourseId()));
            studentCombo.setDisable(true);
            courseCombo.setDisable(true);
            if (existing.getGrade() != null) {
                gradeField.setText(existing.getGrade().toPlainString());
            }
        }

        setResultConverter(button -> {
            if (button == ButtonType.OK) return buildEnrollmentFromFields();
            return null;
        });
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        studentCombo.setPrefWidth(300);
        courseCombo.setPrefWidth(300);
        gradeField.setPrefWidth(150);
        gradeField.setPromptText("e.g. 1.75 (blank = ungraded)");

        // Custom display in the dropdowns (else it shows Enrollment@4f3f5b24)
        studentCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName() + " (" + s.getEmail() + ")");
            }
        });
        studentCombo.setButtonCell(studentCombo.getCellFactory().call(null));

        courseCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Course c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getCode() + " — " + c.getName());
            }
        });
        courseCombo.setButtonCell(courseCombo.getCellFactory().call(null));

        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        grid.add(new Label("Student *"),  0, 0);
        grid.add(studentCombo,            1, 0);
        grid.add(new Label("Course *"),   0, 1);
        grid.add(courseCombo,             1, 1);
        grid.add(new Label("Grade"),      0, 2);
        grid.add(gradeField,              1, 2);
        grid.add(errorLabel, 0, 3, 2, 1);
        return grid;
    }

    private void loadDropdowns() {
        studentCombo.getItems().setAll(new StudentDAO().getAllStudents());
        courseCombo.getItems().setAll(new CourseDAO().getAllCourses());
    }

    private Student findStudent(int id) {
        return studentCombo.getItems().stream()
                .filter(s -> s.getId() == id)
                .findFirst().orElse(null);
    }

    private Course findCourse(int id) {
        return courseCombo.getItems().stream()
                .filter(c -> c.getId() == id)
                .findFirst().orElse(null);
    }

    private boolean validateAndShowErrors() {
        StringBuilder errors = new StringBuilder();

        if (studentCombo.getValue() == null) errors.append("• Please select a student\n");
        if (courseCombo.getValue() == null) errors.append("• Please select a course\n");

        String gradeText = gradeField.getText().trim();
        if (!gradeText.isEmpty()) {
            try {
                BigDecimal g = new BigDecimal(gradeText);
                if (g.compareTo(BigDecimal.ONE) < 0 || g.compareTo(new BigDecimal("5.00")) > 0) {
                    errors.append("• Grade must be between 1.00 and 5.00\n");
                }
            } catch (NumberFormatException ex) {
                errors.append("• Grade must be a number like 1.75\n");
            }
        }

        if (errors.length() > 0) {
            showError(errors.toString().trim());
            return false;
        }
        hideError();
        return true;
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private Enrollment buildEnrollmentFromFields() {
        Student s = studentCombo.getValue();
        Course c = courseCombo.getValue();
        String gradeText = gradeField.getText().trim();

        BigDecimal grade = gradeText.isEmpty() ? null : new BigDecimal(gradeText);

        if (existing != null) {
            existing.setGrade(grade);
            return existing;
        }
        return new Enrollment(s.getId(), c.getId(), grade);
    }
}