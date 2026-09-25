package com.school;

import com.school.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);

        Scene scene = new Scene(loginView.getRoot(), 420, 500);
        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        primaryStage.setTitle("Student Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}