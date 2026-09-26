package com.school.ui;

import com.school.model.User;
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
import javafx.scene.input.KeyCode;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public class AdminDashboard {

    private final Stage stage;
    private final User currentUser;
    private final BorderPane root;
    private final StudentDAO studentDAO = new StudentDAO();
    private final ObservableList<Student> studentsList = FXCollections.observableArrayList();
    private TableView<Student> studentsTable;
    private TextField searchField;

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