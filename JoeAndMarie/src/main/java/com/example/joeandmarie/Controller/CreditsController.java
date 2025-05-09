package com.example.joeandmarie.Controller;

import com.example.joeandmarie.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreditsController {
    @FXML private AnchorPane apContainer;
    @FXML private ImageView ivPeepsAvatar;
    @FXML private ImageView ivBrentAvatar;
    @FXML private ImageView ivIvannAvatar;
    @FXML private ImageView ivRainAvatar;
    @FXML private ImageView ivNateAvatar;

    @FXML
    private void handleExitClick() {
        switchScreenToMainMenu();
    }

    private void switchScreenToMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/layouts/joe_main_menu.fxml"));
            Parent newContent = fxmlLoader.load();

            // Clear the current content and add the new content
            apContainer.getChildren().clear();
            apContainer.getChildren().add(newContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
