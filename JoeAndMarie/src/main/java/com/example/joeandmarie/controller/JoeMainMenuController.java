package com.example.joeandmarie.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class JoeMainMenuController {
    @FXML
    private Pane apContainer;
    @FXML
    public ImageView bgTitle;
    @FXML
    private ImageView bgImage;
    @FXML
    private StackPane coopCampaign;
    @FXML
    private StackPane credit;
    @FXML
    private ImageView menu_play;
    @FXML
    private ImageView menu_settings;
    @FXML
    private ImageView menu_exit;
    @FXML
    private ImageView menu_credits;
//
//    @FXML
//    private StackPane multiplayer;
//
//    @FXML
//    private StackPane collection;

    @FXML
    private StackPane settings;

    @FXML
    private StackPane quit;

    @FXML
    public void initialize() {
        Image image = new Image(getClass().getResource("/assets/textures/menu_bg4_pixelated.png").toExternalForm()); // just testing pixelated backgrounds
        bgImage.setImage(image);
        bgImage.setPreserveRatio(true);
        bgImage.setSmooth(true);

        Image title = new Image(getClass().getResource("/assets/textures/menu_title.png").toExternalForm());
        bgTitle.setImage(title);
        bgTitle.setPreserveRatio(true);
        bgTitle.setSmooth(true);

        Image play = new Image(getClass().getResource("/assets/textures/menu_play.png").toExternalForm());
        menu_play.setImage(play);

        Image setting = new Image(getClass().getResource("/assets/textures/menu_settings.png").toExternalForm());
        menu_settings.setImage(setting);

        Image exit = new Image(getClass().getResource("/assets/textures/menu_exit.png").toExternalForm());
        menu_exit.setImage(exit);

        Image credits = new Image(getClass().getResource("/assets/textures/menu_credits.png").toExternalForm());
        menu_credits.setImage(credits);

        javafx.application.Platform.runLater(() -> {
            if (bgImage.getScene() != null) {
                bgImage.fitWidthProperty().bind(bgImage.getScene().widthProperty());
                bgImage.fitHeightProperty().bind(bgImage.getScene().heightProperty());

                // Example: Make title width 50% of window, height 20%
//                bgTitle.fitWidthProperty().bind(bgImage.getScene().widthProperty().multiply(0.5));
//                bgTitle.fitHeightProperty().bind(bgImage.getScene().heightProperty().multiply(0.2));
            }
        });

        setupHoverEffect(coopCampaign);
        setupHoverEffect(settings);
        setupHoverEffect(credit);
        setupHoverEffect(quit);
    }

    private void setupHoverEffect(StackPane item) {
        if (item != null) {
            item.setOnMouseEntered(e -> item.setStyle("-fx-cursor: hand; -fx-opacity: 0.8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-opacity: 1.0;"));
        }
//        if(item != null){
//
//        }
    }

    @FXML
    private void handlePlayClick() {
        switchScreenToProgressFiles();
    }

    @FXML
    private void handleCreditsClick() {
        switchScreenToCredits();
    }

    private void switchScreenToProgressFiles() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/layouts/progress_files.fxml"));
            Parent newContent = fxmlLoader.load();

            // Clear the current content and add the new content
            apContainer.getChildren().clear();
            apContainer.getChildren().add(newContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchScreenToSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/layouts/settings.fxml"));
            Parent newContent = fxmlLoader.load();

            // Clear the current content and add the new content
            apContainer.getChildren().clear();
            apContainer.getChildren().add(newContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchScreenToCredits() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/layouts/credits.fxml"));
            Parent newContent = fxmlLoader.load();

            // Clear the current content and add the new content
            apContainer.getChildren().clear();
            apContainer.getChildren().add(newContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//
//    @FXML
//    private void handleCollectionClick() {
//        System.out.println("Collection clicked");
//    }

    @FXML
    private void handleSettingsClick() {
        switchScreenToSettings();
//        System.out.println("Settings clicked");

    }

    @FXML
    private void handleQuitClick() {
        FXGL.getGameController().exit();
    }
}
