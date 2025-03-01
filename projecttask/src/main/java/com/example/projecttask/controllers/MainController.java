package com.example.projecttask.controllers;

import com.example.projecttask.models.Course;
import com.example.projecttask.utils.FileUtils;
import com.example.projecttask.utils.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField instructorField;
    @FXML private ListView<Course> courseListView;
    @FXML private TableView<Course> customerTable;
    @FXML private TableColumn<Course, String> idColumn;
    @FXML private TableColumn<Course, String> nameColumn;
    @FXML private TableColumn<Course, String> emailColumn;
    @FXML private Label statusLabel;
    @FXML private Button editButton;
    @FXML private Button addButton;
    @FXML private Button removeButton;

    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    private Course selectedCourse = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {

        courseList.addAll(FileUtils.loadCourses());

        courseListView.setItems(courseList);
        if (customerTable != null) {
            customerTable.setItems(courseList);
        }

        courseListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedCourse = newValue;
                        populateFields(newValue);
                        if (editButton != null) {
                            editButton.setDisable(false);
                        }
                        if (removeButton != null) {
                            removeButton.setDisable(false);
                        }
                    } else {
                        if (editButton != null) {
                            editButton.setDisable(true);
                        }
                        if (removeButton != null) {
                            removeButton.setDisable(true);
                        }
                    }
                });


        if (editButton != null) {
            editButton.setDisable(true);
        }
        if (removeButton != null) {
            removeButton.setDisable(true);
        }

        updateStatus("Application started, loaded " + courseList.size() + " courses");
    }

    private void populateFields(Course course) {
        courseIdField.setText(course.getCourseId());
        courseNameField.setText(course.getCourseName());
        instructorField.setText(course.getInstructor());
    }

    private void clearFields() {
        courseIdField.setText("");
        courseNameField.setText("");
        instructorField.setText("");
        selectedCourse = null;
        editMode = false;
        updateButtonLabels();
    }

    private void updateButtonLabels() {
        if (addButton != null) {
            addButton.setText(editMode ? "Update" : "Add");
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
        LoggerUtil.logInfo(message);
    }

    @FXML
    private void handleAddCourse() {
        String courseId = courseIdField.getText();
        String courseName = courseNameField.getText();
        String instructor = instructorField.getText();

        if (!courseId.isEmpty() && !courseName.isEmpty() && !instructor.isEmpty()) {
            if (editMode && selectedCourse != null) {

                int index = courseList.indexOf(selectedCourse);
                selectedCourse.setCourseId(courseId);
                selectedCourse.setCourseName(courseName);
                selectedCourse.setInstructor(instructor);
                courseList.set(index, selectedCourse);
                updateStatus("Course updated successfully");
            } else {

                Course course = new Course(courseId, courseName, instructor);
                courseList.add(course);
                updateStatus("Course added successfully");
            }

            FileUtils.saveCourses(courseList);

            clearFields();
            courseListView.getSelectionModel().clearSelection();
        } else {
            updateStatus("Failed to " + (editMode ? "update" : "add") + " course: Invalid input");
        }
    }

    @FXML
    private void handleRemoveCourse() {
        if (selectedCourse != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Course");
            alert.setContentText("Are you sure you want to delete this course?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                courseList.remove(selectedCourse);
                FileUtils.saveCourses(courseList);
                clearFields();
                updateStatus("Course removed successfully");
            }
        } else {
            updateStatus("Failed to remove course: No course selected");
        }
    }

    @FXML
    private void handleEditCourse() {
        if (selectedCourse != null) {
            editMode = true;
            updateButtonLabels();
            updateStatus("Editing course: " + selectedCourse.getCourseId());
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
        courseListView.getSelectionModel().clearSelection();
        updateStatus("Form cleared");
    }

    @FXML
    private void handleAbout() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/about.fxml"));
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("About");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            if (courseListView != null && courseListView.getScene() != null &&
                    courseListView.getScene().getWindow() != null) {
                dialogStage.initOwner(courseListView.getScene().getWindow());
            }

            dialogStage.show();
            updateStatus("Opened About window");
        } catch (IOException e) {
            LoggerUtil.logError("Failed to open About window: " + e.getMessage(), e);
            updateStatus("Failed to open About window: " + e.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load About dialog");
            alert.setContentText("An error occurred while trying to load the About dialog.\n" +
                    "Error details: " + e.getMessage());
            alert.showAndWait();
        }
    }
}