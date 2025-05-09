package com.example.joeandmarie.Controller;

import com.example.joeandmarie.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ScreenManager {

    private static AnchorPane mainContainer;

    public static void setMainContainer(AnchorPane container) {
        mainContainer = container;
    }

    public static AnchorPane getMainContainer() {
        return mainContainer;
    }

    public static void switchScreen(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath));
            Parent newContent = loader.load();
            newContent.getStylesheets().add(MainApplication.class.getResource("/assets/layouts/css/style.css").toExternalForm());

            mainContainer.getChildren().setAll(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
