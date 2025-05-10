package com.example.joeandmarie.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class JoeMainMenuController {
    @FXML
    private AnchorPane apContainer;
    @FXML
    public ImageView bgTitle;
    @FXML
    private ImageView bgImage;
    @FXML
    private ImageView menu_play;
    @FXML
    private ImageView menu_settings;
    @FXML
    private ImageView menu_exit;
    @FXML
    private ImageView menu_credits;

    @FXML
    public void initialize() {
        if(ScreenManager.getMainContainer() == null) {
            ScreenManager.setMainContainer(apContainer);
        }

        Image image = new Image(getClass().getResource("/assets/textures/menu_bg.png").toExternalForm());
        bgImage.setImage(image);
        bgImage.setPreserveRatio(true);
        bgImage.setSmooth(true);

        Image title = new Image(getClass().getResource("/assets/textures/menu_title_noshadow.png").toExternalForm());
        bgTitle.setImage(title);
        bgTitle.setPreserveRatio(true);
        bgTitle.setSmooth(true);

        Image play = new Image(getClass().getResource("/assets/textures/menu_new_play.png").toExternalForm());
        menu_play.setImage(play);

        Image setting = new Image(getClass().getResource("/assets/textures/menu_new_settings.png").toExternalForm());
        menu_settings.setImage(setting);

        Image exit = new Image(getClass().getResource("/assets/textures/menu_new_exit.png").toExternalForm());
        menu_exit.setImage(exit);

        Image credits = new Image(getClass().getResource("/assets/textures/menu_new_credits.png").toExternalForm());
        menu_credits.setImage(credits);

        javafx.application.Platform.runLater(() -> {
            if (bgImage.getScene() != null) {
                bgImage.fitWidthProperty().bind(bgImage.getScene().widthProperty());
                bgImage.fitHeightProperty().bind(bgImage.getScene().heightProperty());

            }
        });

        setupHoverEffect(menu_play);
        setupHoverEffect(menu_settings);
        setupHoverEffect(menu_credits);
        setupHoverEffect(menu_exit);
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
        ScreenManager.switchScreen("/assets/layouts/progress_files.fxml");
    }

    @FXML
    private void handleCreditsClick() {
        ScreenManager.switchScreen("/assets/layouts/credits.fxml");
    }

    @FXML
    private void handleSettingsClick() {
        ScreenManager.switchScreen("/assets/layouts/settings.fxml");
    }

    @FXML
    private void handleQuitClick() {
        FXGL.getGameController().exit();
    }
}
