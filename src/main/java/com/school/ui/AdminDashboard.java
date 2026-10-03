package com.school.ui;

import com.school.dao.UserDAO;
import com.school.model.User;
import com.school.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.school.dao.StudentDAO;
import com.school.model.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import com.school.dao.CourseDAO;
import com.school.model.Course;

import com.school.dao.EnrollmentDAO;
import com.school.model.Enrollment;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

public class AdminDashboard {

    private final Stage stage;
    private final User currentUser;
    private final BorderPane root;
    private final StudentDAO studentDAO = new StudentDAO();
    private final ObservableList<Student> studentsList = FXCollections.observableArrayList();
    private TableView<Student> studentsTable;
    private TextField searchField;
    private final CourseDAO courseDAO = new CourseDAO();
    private final ObservableList<Course> coursesList = FXCollections.observableArrayList();
    private TableView<Course> coursesTable;
    private TextField courseSearchField;
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final ObservableList<Enrollment> enrollmentsList = FXCollections.observableArrayList();
    private TableView<Enrollment> enrollmentsTable;
    private TextField enrollmentSearchField;
    private final UserDAO userDAO = new UserDAO();
    private final AuthService authService = new AuthService();
    private final ObservableList<User> usersList = FXCollections.observableArrayList();
    private TableView<User> accountsTable;


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

        Tab coursesTab = new Tab("Courses");
        coursesTab.setContent(buildCoursesTab());

        Tab enrollmentsTab = new Tab("Enrollments");
        enrollmentsTab.setContent(buildEnrollmentsTab());
        Tab accountsTab = new Tab("Accounts");
        accountsTab.setContent(buildAccountsTab());

        tabPane.getTabs().addAll(studentsTab, coursesTab, enrollmentsTab, accountsTab);
        return tabPane;
    }

    private VBox buildAccountsTab(){
        Button createBtn = new Button("+ Create Account");
        Button resetBtn = new Button("🔑 Reset Password");
        Button deleteBtn = new Button("🗑 Delete");

        createBtn.getStyleClass().add("primary-button");
        deleteBtn.getStyleClass().add("danger-button");

        createBtn.setOnAction(e -> handleCreateAccount());
        resetBtn.setOnAction(e -> handleResetPassword());
        deleteBtn.setOnAction(e -> handleDeleteAccount());

        HBox toolbar = new HBox(10, createBtn, resetBtn, deleteBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(12));

        accountsTable = new TableView<>();
        accountsTable.setItems(usersList);
        accountsTable.setPlaceholder(new Label("No users yet."));

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(60);

        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setPrefWidth(180);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole().name()));
        roleCol.setPrefWidth(120);

        TableColumn<User, String> studentCol = new TableColumn<>("Linked Student");
        studentCol.setCellValueFactory(cell -> {
            User u = cell.getValue();
            if (u.getRole() == User.Role.ADMIN) return  new SimpleStringProperty("—");
            String name = u.getStudentName();
            return new SimpleStringProperty(name == null ? "(unlinked)" : name);
        });
        studentCol.setPrefWidth(300);

        accountsTable.getColumns().addAll(idCol, userCol, roleCol, studentCol);

        VBox content = new VBox(10, toolbar, accountsTable);
        content.setPadding(new Insets(10, 15, 15, 15));
        VBox.setVgrow(accountsTable, Priority.ALWAYS);

        loadAccounts();
        return content;
    }

    private  void loadAccounts(){
        usersList.setAll(userDAO.getAllUsers());
    }

    private void handleCreateAccount(){
        AccountDialog dialog = new AccountDialog(stage);
        dialog.showAndWait().ifPresent(data ->{
            boolean ok = authService.register(data.username, data.password, data.role, data.studentId);
            if (ok){
                loadAccounts();
            }else {
                new Alert(Alert.AlertType.ERROR,
                        "Failed to create account. Username may already exist, \n" +
                        "or the password doesn't meet requirements.").showAndWait();
            }
        });
    }

    private void handleResetPassword(){
        User selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null){
            new Alert(Alert.AlertType.WARNING, "Select an account first").showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(stage);
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Reset password for '" + selected.getUsername() + "'");
        dialog.setContentText("New password (min 6 chars):");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.length() < 6){
                new Alert(Alert.AlertType.ERROR, "Password must be at least 6 characters.").showAndWait();
                return;
            }
            if (authService.changePassword(selected.getId(), newPassword)){
                new Alert(Alert.AlertType.INFORMATION, "Password updated for '" + selected.getUsername() + "' .").showAndWait();
            }else {
                new Alert(Alert.AlertType.ERROR, "Failed to update password.").showAndWait();
            }
        });
    }

    private void handleDeleteAccount(){
        User selected = accountsTable.getSelectionModel().getSelectedItem();

        if (selected == null){
            new Alert(Alert.AlertType.WARNING, "Select an account first").showAndWait();
            return;
        }
        // Safety: don't let the current admin delete themselves
        if (selected.getId() == currentUser.getId()) {
            new Alert(Alert.AlertType.WARNING,
                    "You can't delete your own account while logged in.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(stage);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete account '" + selected.getUsername() + "'?\n\n" +
                (selected.getRole() == User.Role.STUDENT
                ? "The student record itself will NOT be deleted."
                        : "This cannot be undone."));
        Optional<ButtonType> answer = confirm.showAndWait();

        if (answer.isPresent() && answer.get() == ButtonType.OK){
            if (userDAO.deleteUser(selected.getId())){
                loadAccounts();
            }else {

                new Alert(Alert.AlertType.ERROR, "Failed to delete account").showAndWait();

            }
        }
    }



    private VBox buildEnrollmentsTab() {
        Button addBtn    = new Button("+ Enroll");
        Button editBtn   = new Button("✎ Edit Grade");
        Button deleteBtn = new Button("🗑 Delete");
        Button clearBtn  = new Button("Clear");

        addBtn.getStyleClass().add("primary-button");
        deleteBtn.getStyleClass().add("danger-button");

        addBtn.setOnAction(e -> handleAddEnrollment());
        editBtn.setOnAction(e -> handleEditEnrollment());
        deleteBtn.setOnAction(e -> handleDeleteEnrollment());
        clearBtn.setOnAction(e -> {
            enrollmentSearchField.clear();
            loadEnrollments();
        });

        enrollmentSearchField = new TextField();
        enrollmentSearchField.setPromptText("Search student or course…");
        enrollmentSearchField.setPrefWidth(260);
        enrollmentSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isBlank()) loadEnrollments();
            else enrollmentsList.setAll(enrollmentDAO.searchEnrollments(newVal.trim()));
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10,
                addBtn, editBtn, deleteBtn,
                spacer,
                new Label("🔍"), enrollmentSearchField, clearBtn
        );
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(12));

        enrollmentsTable = new TableView<>();
        enrollmentsTable.setItems(enrollmentsList);
        enrollmentsTable.setPlaceholder(new Label("No enrollments yet. Click '+ Enroll' to create one."));

        TableColumn<Enrollment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Enrollment, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentCol.setPrefWidth(200);

        TableColumn<Enrollment, String> courseCodeCol = new TableColumn<>("Course");
        courseCodeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseCodeCol.setPrefWidth(100);

        TableColumn<Enrollment, String> courseNameCol = new TableColumn<>("Course Name");
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseNameCol.setPrefWidth(260);

        TableColumn<Enrollment, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(cellData -> {
            BigDecimal g = cellData.getValue().getGrade();
            return new SimpleStringProperty(g == null ? "—" : g.toPlainString());
        });
        gradeCol.setPrefWidth(80);

        TableColumn<Enrollment, String> dateCol = new TableColumn<>("Enrolled");
        dateCol.setCellValueFactory(cellData -> {
            var d = cellData.getValue().getEnrolledAt();
            return new SimpleStringProperty(d == null ? "—"
                    : d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        });
        dateCol.setPrefWidth(120);

        enrollmentsTable.getColumns().addAll(
                idCol, studentCol, courseCodeCol, courseNameCol, gradeCol, dateCol
        );

        enrollmentsTable.setRowFactory(tv -> {
            TableRow<Enrollment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) handleEditEnrollment();
            });
            return row;
        });

        VBox content = new VBox(10, toolbar, enrollmentsTable);
        content.setPadding(new Insets(10, 15, 15, 15));
        VBox.setVgrow(enrollmentsTable, Priority.ALWAYS);

        loadEnrollments();
        return content;
    }

    private void loadEnrollments() {
        enrollmentsList.setAll(enrollmentDAO.getAllEnrollments());
    }

    private void handleAddEnrollment() {
        // Guard: need at least one student and one course
        if (new StudentDAO().getAllStudents().isEmpty() ||
                new CourseDAO().getAllCourses().isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Add at least one student and one course first.").showAndWait();
            return;
        }

        EnrollmentDialog dialog = new EnrollmentDialog(stage, null);
        dialog.showAndWait().ifPresent(e -> {
            int newId = enrollmentDAO.addEnrollment(e);
            if (newId > 0) {
                loadEnrollments();
                enrollmentsTable.getSelectionModel().select(
                        enrollmentsList.stream()
                                .filter(en -> en.getId() == newId)
                                .findFirst().orElse(null)
                );
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Failed to enroll. Student may already be enrolled in this course.").showAndWait();
            }
        });
    }

    private void handleEditEnrollment() {
        Enrollment selected = enrollmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select an enrollment first").showAndWait();
            return;
        }

        EnrollmentDialog dialog = new EnrollmentDialog(stage, selected);
        dialog.showAndWait().ifPresent(updated -> {
            if (enrollmentDAO.updateGrade(updated.getId(), updated.getGrade())) {
                loadEnrollments();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update grade.").showAndWait();
            }
        });
    }

    private void handleDeleteEnrollment() {
        Enrollment selected = enrollmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select an enrollment first").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Enrollment");
        confirm.setHeaderText(null);
        confirm.setContentText("Remove " + selected.getStudentName() +
                " from " + selected.getCourseCode() + "?");
        Optional<ButtonType> answer = confirm.showAndWait();

        if (answer.isPresent() && answer.get() == ButtonType.OK) {
            if (enrollmentDAO.deleteEnrollment(selected.getId())) {
                loadEnrollments();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete enrollment.").showAndWait();
            }
        }
    }

    private VBox buildCoursesTab(){

        //ToolBar

        Button addBtn = new Button("+ Add");
        Button editBtn = new Button("✎ Edit");
        Button deleteBtn = new Button("🗑 Delete");
        Button clearBtn  = new Button("Clear");

        addBtn.getStyleClass().add("primary-button");
        deleteBtn.getStyleClass().add("danger-button");

        addBtn.setOnAction(e -> handleAddCourse());
        editBtn.setOnAction(e -> handleEditCourse());
        deleteBtn.setOnAction(e -> handleDeleteCourse());
        clearBtn.setOnAction(e -> {
            courseSearchField.clear();
            loadCourses();
        });

        courseSearchField = new TextField();
        courseSearchField.setPromptText("Search code, name, or instructor...");
        courseSearchField.setPrefWidth(260);
        courseSearchField.textProperty().addListener((obs, oldVal, newVal) -> {

            if(newVal.isBlank()) loadCourses();
            else coursesList.setAll(courseDAO.searchCourses(newVal.trim()));

        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10,
                addBtn, editBtn, deleteBtn,
                spacer,
                new Label("🔍"), courseSearchField, clearBtn);

        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(12));

        //Table
        coursesTable = new TableView<>();
        coursesTable.setItems(coursesList);
        coursesTable.setPlaceholder(new Label("No courses yet. Click '+ Add' to create one."));

        TableColumn<Course, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setPrefWidth(100);

        TableColumn<Course, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(280);

        TableColumn<Course, String> instrCol = new TableColumn<>("Instructor");
        instrCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        instrCol.setPrefWidth(200);

        TableColumn<Course, Integer> credCol = new TableColumn<>("Credits");
        credCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        credCol.setPrefWidth(80);

        coursesTable.getColumns().addAll(idCol, codeCol, nameCol, instrCol, credCol);

        coursesTable.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                    if(event.getClickCount() == 2 && !row.isEmpty()) {
                        handleEditCourse();

                    }

            });
            return row;
        });

        VBox content = new VBox(10, toolbar, coursesTable);
        content.setPadding(new Insets(10, 15, 15, 15));
        VBox.setVgrow(coursesTable, Priority.ALWAYS);

        loadCourses();
        return content;
    }

    private void loadCourses() {
        coursesList.setAll(courseDAO.getAllCourses());
    }

    private void handleAddCourse(){
        CourseDialog dialog = new CourseDialog(stage, null);
        dialog.showAndWait().ifPresent(course -> {
            int newId = courseDAO.addCourse(course);
            if (newId > 0) {
                loadCourses();
                coursesTable.getSelectionModel().select(
                        coursesList.stream()
                                .filter(c -> c.getId() == newId)
                                .findFirst()
                                .orElse(null)
                );
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Failed to add course. Code may already exist.").showAndWait();

            }
        });
    }

    private void handleEditCourse() {
        Course selected = coursesTable.getSelectionModel().getSelectedItem();
        if (selected == null){
            new Alert(Alert.AlertType.WARNING, "Select a course first").showAndWait();
            return;
        }

        CourseDialog dialog = new CourseDialog(stage, selected);
        dialog.showAndWait().ifPresent(updated -> {
            if(courseDAO.updateCourse(updated)) {
                loadCourses();
            }else{
                new Alert(Alert.AlertType.ERROR, "Failed to update course.").showAndWait();
            }
        });
    }

    private void handleDeleteCourse(){
        Course selected = coursesTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.WARNING, "Select a course first").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Course");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete " + selected.getCode() + " - " + selected.getName()
        + "?\n\n" + "This will also remove all enrollments for this course.");
        Optional<ButtonType> answer = confirm.showAndWait();

        if (answer.isPresent() && answer.get() == ButtonType.OK) {
            if (courseDAO.deleteCourse(selected.getId())) {
                loadCourses();
            }else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete course.").showAndWait();

            }
        }
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
        // Toolbar
        Button addBtn    = new Button("+ Add");
        Button editBtn   = new Button("✎ Edit");
        Button deleteBtn = new Button("🗑 Delete");
        Button clearBtn  = new Button("Clear");

        addBtn.getStyleClass().add("primary-button");
        deleteBtn.getStyleClass().add("danger-button");

        addBtn.setOnAction(e -> handleAddStudent());
        editBtn.setOnAction(e -> handleEditStudent());
        deleteBtn.setOnAction(e -> handleDeleteStudent());
        clearBtn.setOnAction(e -> {
            searchField.clear();
            loadStudents();
        });

        searchField = new TextField();
        searchField.setPromptText("Search by name…");
        searchField.setPrefWidth(220);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isBlank()) loadStudents();
            else studentsList.setAll(studentDAO.searchByName(newVal.trim()));
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10,
                addBtn, editBtn, deleteBtn,
                spacer,
                new Label("🔍"), searchField, clearBtn
        );
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(12));

        //Table
        studentsTable = new TableView<>();
        studentsTable.setItems(studentsList);
        studentsTable.setPlaceholder(new Label("No students yet. Click '+ Add' to create one."));

        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);

        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(140);

        // DOB column — needs a custom cellValueFactory because LocalDate isn't a String
        TableColumn<Student, String> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(cellData -> {
            Student s = cellData.getValue();
            String text = s.getDateOfBirth() == null
                    ? "—"
                    : s.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(text);
        });
        dobCol.setPrefWidth(120);

        // Enrollment column
        TableColumn<Student, String> enrolledCol = new TableColumn<>("Enrolled");
        enrolledCol.setCellValueFactory(cellData -> {
            Student s = cellData.getValue();
            String text = s.getEnrollmentDate() == null
                    ? "—"
                    : s.getEnrollmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(text);
        });
        enrolledCol.setPrefWidth(120);

        studentsTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, dobCol, enrolledCol);

        // Double-click a row = Edit
        studentsTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleEditStudent();
                }
            });
            return row;
        });

        // Assemble
        VBox content = new VBox(10, toolbar, studentsTable);
        content.setPadding(new Insets(10, 15, 15, 15));
        VBox.setVgrow(studentsTable, Priority.ALWAYS);

        // Initial load
        loadStudents();
        return content;
    }


    private void handleAddStudent() {
        StudentDialog dialog = new StudentDialog(stage, null);
        Optional<Student> result = dialog.showAndWait();

        result.ifPresent(student -> {
            int newId = studentDAO.addStudent(student);
            if (newId > 0) {
                loadStudents();
                // Select the newly-added student (nice UX)
                studentsTable.getSelectionModel().select(
                        studentsList.stream()
                                .filter(s -> s.getId() == newId)
                                .findFirst()
                                .orElse(null)
                );
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Failed to add student. Email may already exist.").showAndWait();
            }
        });
    }

    private void handleEditStudent() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a student first").showAndWait();
            return;
        }

        StudentDialog dialog = new StudentDialog(stage, selected);
        Optional<Student> result = dialog.showAndWait();

        result.ifPresent(updated -> {
            boolean ok = studentDAO.updateStudent(updated);
            if (ok) {
                loadStudents();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update student.").showAndWait();
            }
        });
    }

    private void handleDeleteStudent(){
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null){
            new Alert (Alert.AlertType.WARNING, "Select a student first").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Student");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete" + selected.getName() + "?\n" + "?\n\n" +
                "This will remove their enrollments. This cannot be undone");
        Optional<ButtonType> answer = confirm.showAndWait();

        if (answer.isPresent() && answer.get() == ButtonType.OK) {
            boolean ok = studentDAO.deleteStudent(selected.getId());
            if (ok){
                loadStudents();
            }else{
                new Alert(Alert.AlertType.ERROR, "Failed to delete student.").showAndWait();
            }
        }
    }


    private void loadStudents() {
        studentsList.setAll(studentDAO.getAllStudents());
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