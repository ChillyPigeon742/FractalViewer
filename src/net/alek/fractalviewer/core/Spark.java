package net.alek.fractalviewer.core;

import net.alek.fractalviewer.ui.CreateWindow;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Spark {
    private static String version = "null";

    public static void main(String[] args) {
        String resourcePath = "/assets/fractalviewer/config/Maven.properties";
        try (InputStream in = Spark.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.err.println("Resource not found: " + resourcePath);
            } else {
                Properties props = new Properties();
                props.load(in);
                version = props.getProperty("app.version", "null");
            }
        } catch (IOException e) {
            System.err.println("Failed to read version tag!");
            System.err.println(e.getMessage() != null ? e.getMessage() : e.toString());
        }

        CreateWindow.createWindow();
    }

    public static String getVersion() {
        return version;
    }
}
