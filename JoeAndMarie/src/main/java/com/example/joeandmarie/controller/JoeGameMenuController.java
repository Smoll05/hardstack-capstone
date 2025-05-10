package com.example.joeandmarie.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class JoeGameMenuController {
    @FXML private AnchorPane apGameMenuContainer;
    @FXML private ImageView btnPlay;
    @FXML private ImageView btnSettings;
    @FXML private ImageView btnExit;

    @FXML
    public void initialize() {
        setupHoverEffect(btnPlay);
        setupHoverEffect(btnExit);
        setupHoverEffect(btnSettings);
    }

    private void setupHoverEffect(ImageView item) {
        if (item != null) {
            item.setOnMouseEntered(e -> {
                item.setStyle("-fx-cursor: hand;");
                item.setScaleX(1.05);
                item.setScaleY(1.05);
                FXGL.getAudioPlayer().playSound(MainApplication.getSfx_hover());
            });

            item.setOnMouseExited(e -> {
                item.setScaleX(1.0);
                item.setScaleY(1.0);
            });
        }
    }

    @FXML
    private void handlePlayClick() {
        FXGL.getSceneService().popSubScene();
    }

    @FXML
    private void handleSettingsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/assets/layouts/game_menu_settings.fxml"));
            Parent newContent = null;
            newContent = loader.load();
            newContent.getStylesheets().add(MainApplication.class.getResource("/assets/layouts/stylesheets/style.css").toExternalForm());

            GameMenuSettingsController controller = loader.getController();
            controller.setParentContainer(apGameMenuContainer); // pass this pane to settings controller

            apGameMenuContainer.getChildren().setAll(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleExitClick() {
        FXGL.getGameController().gotoMainMenu();
    }
}
