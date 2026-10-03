package com.school.ui;

import com.school.dao.StudentDAO;
import com.school.model.Student;
import com.school.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

public class AccountDialog extends  Dialog<AccountDialog.AccountData>{

    // What dialog returns when the user clicks Save.
    public static class AccountData{
        public final String username;
        public final String password;
        public final User.Role role;
        public final Integer studentId;

        public AccountData(String username, String password, User.Role role, Integer studentId){
            this.username = username;
            this.password = password;
            this.role = role;
            this.studentId = studentId;
        }
    }

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField =  new PasswordField();
    private final ComboBox<User.Role> roleCombo = new ComboBox<>();
    private final ComboBox<Student> studentCombo = new ComboBox<>();

    private final Label studentLabel = new Label(" Link to Student *");
    private final Label errorLabel = new Label();

    public AccountDialog(Window owner){
        initOwner(owner);
        setTitle("Create Account");
        setHeaderText(null);

        getDialogPane().setContent(buildForm());
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        getDialogPane().getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        Button okBtn = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okBtn.getStyleClass().add("primary-button");
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateAndShowErrors()) event.consume();
        });

        setResultConverter(button -> {
            if (button != ButtonType.OK) return null;
            User.Role role = roleCombo.getValue();
            Integer sid = role == User.Role.STUDENT
                    ? (studentCombo.getValue() == null ? null : studentCombo.getValue().getId())
                    : null;

            return new AccountData(
                    usernameField.getText().trim(),
                    passwordField.getText(),
                    role,
                    sid
            );
        });
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        usernameField.setPrefWidth(260);
        passwordField.setPrefWidth(260);
        roleCombo.setPrefWidth(260);
        studentCombo.setPrefWidth(260);

        //Role dropdown
        roleCombo.getItems().setAll(User.Role.values());
        roleCombo.setValue(User.Role.STUDENT);

        //Student dropdown
        studentCombo.getItems().setAll(new StudentDAO().getAllStudents());
        studentCombo.setCellFactory(lv -> new ListCell<>(){
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null  ? null
                        : s.getName() + " (" + s.getEmail() + ")");
            }
        });
        studentCombo.setButtonCell(studentCombo.getCellFactory().call(null));

        // show/hide student based on role
        roleCombo.valueProperty().addListener((obs, oldV, newV) -> {
            boolean isStudent = newV == User.Role.STUDENT;
            studentLabel.setVisible(isStudent);
            studentLabel.setManaged(isStudent);
            studentCombo.setVisible(isStudent);
            studentCombo.setManaged(isStudent);
        });

        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        grid.add(new Label("Username *"),   0, 0);
        grid.add(usernameField,             1, 0);
        grid.add(new Label("Password *"),   0, 1);
        grid.add(passwordField,             1, 1);
        grid.add(new Label("Role *"),       0, 2);
        grid.add(roleCombo,                 1, 2);
        grid.add(studentLabel,              0, 3);
        grid.add(studentCombo,              1, 3);
        grid.add(errorLabel, 0, 4, 2, 1);


        return grid;
    }

    private boolean validateAndShowErrors() {
        StringBuilder errors = new StringBuilder();

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()){
            errors.append("• Username is required\n ");
        } else if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            errors.append("• Username must be 3–20 characters (letters, digits, _)\n");
        }
        if (password.length() < 6) {
            errors.append("• Password must be at least 6 characters\n");
        }
        if (roleCombo.getValue() == null) {
            errors.append("• Pick which student to link\n");
        }
        if (errors.length() > 0) {
            showError(errors.toString().trim());
            return false;
        }
        hideError();
        return true;
    }
    private void showError(String msg){
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }


}
