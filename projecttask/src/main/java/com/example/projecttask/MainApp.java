package com.example.projecttask;

import com.example.projecttask.utils.FileUtils;
import com.example.projecttask.utils.LoggerUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/main.fxml"));
            BorderPane root = loader.load();
            primaryStage.setTitle("Course Manager");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {
                LoggerUtil.logInfo("Application closing");
            });

        } catch (Exception e) {
            LoggerUtil.logError("Error starting application", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}