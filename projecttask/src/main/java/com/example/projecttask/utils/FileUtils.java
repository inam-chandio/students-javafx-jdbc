package com.example.projecttask.utils;

import com.example.projecttask.models.Course;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String DATA_FILE = "courses.dat";

    public static void saveCourses(List<Course> courses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(new ArrayList<>(courses));
            com.example.projecttask.utils.LoggerUtil.logInfo("Saved " + courses.size() + " courses to file");
        } catch (IOException e) {
            com.example.projecttask.utils.LoggerUtil.logError("Error saving courses to file", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        File file = new File(DATA_FILE);

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                courses = (List<Course>) ois.readObject();
                com.example.projecttask.utils.LoggerUtil.logInfo("Loaded " + courses.size() + " courses from file");
            } catch (IOException | ClassNotFoundException e) {
                com.example.projecttask.utils.LoggerUtil.logError("Error loading courses from file", e);
            }
        }

        return courses;
    }
}