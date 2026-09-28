package com.school.ui;

import com.school.model.Course;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;


public class CourseDialog extends Dialog<Course>{

    private final TextField codeField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField instructorField = new TextField();
    private final Spinner<Integer> creditsSpinner =
            new Spinner<>(1, 10, 3); // min, max, default

    private final Label errorLabel = new Label();
    private final Course existing;

    public CourseDialog(Window owner, Course existing) {
        this.existing = existing;

        initOwner(owner);
        setTitle(existing == null ? "Add Course" : "Edit Course");
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
            if(!validateAndShowErrors()) event.consume();
        });

        if (existing != null){
            codeField.setText(existing.getCode());
            nameField.setText(existing.getName());
            instructorField.setText(existing.getInstructor());
            creditsSpinner.getValueFactory().setValue(existing.getCredits());
        }

        setResultConverter(button ->{
            if (button == ButtonType.OK) return buildCourseFromFields();
            return null;
        });
    }

        private GridPane buildForm() {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(12);
            grid.setPadding(new Insets(20));

            codeField.setPrefWidth(300);
            nameField.setPrefWidth(300);
            instructorField.setPrefWidth(300);
            creditsSpinner.setPrefWidth(120);

            errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12;");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            grid.add(new Label("Code *"),         0, 0);
            grid.add(codeField,                   1, 0);

            grid.add(new Label("Name *"),         0, 1);
            grid.add(nameField,                   1, 1);

            grid.add(new Label("Instructor"),     0, 2);
            grid.add(instructorField,             1, 2);

            grid.add(new Label("Credits *"),      0, 3);
            grid.add(creditsSpinner,              1, 3);

            grid.add(errorLabel, 0, 4, 2, 1);
            return grid;
        }
        private boolean validateAndShowErrors(){
            StringBuilder errors = new StringBuilder();

            String code = codeField.getText().trim();
            String name = nameField.getText().trim();

            if (code.isEmpty()) {
                errors.append("• Course code is required\\n");
            } else if (!code.matches("^[A-Za-z]{2,4}\\d{3,4}$")) {
                errors.append("• Code should look like CS101 or MATH2001\n");
            }
            if (name.isEmpty()) {
                errors.append("• Course name is required\n");
            }

            if (errors.length() > 0) {
                showError(errors.toString().trim());
                return false;
            }
            hideError();
            return true;

        }
        private void showError(String message) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }

        private void hideError(){
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }

        private Course buildCourseFromFields(){
            String code =  codeField.getText().trim().toUpperCase();
            String name = nameField.getText().trim();
            String instructor = instructorField.getText().trim();
            int credits = creditsSpinner.getValue();

            if (existing != null){
                existing.setCode(code);
                existing.setName(name);
                existing.setInstructor(instructor);
                existing.setCredits(credits);
                return existing;
            }else {
                return new Course(code, name, instructor, credits);
            }
        }

    }


