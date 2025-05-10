package com.example.joeandmarie.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;

public class CreditsController {
    @FXML private ImageView btnExit;

    @FXML
    public void initialize() {
        setupHoverEffect(btnExit);
    }

    @FXML
    private void handleExitClick() {
        FXGL.getAudioPlayer().playSound(MainApplication.getSfx_click());
        ScreenManager.switchScreen("/assets/layouts/joe_main_menu.fxml");
    }

    private void setupHoverEffect(ImageView item) {
        if (item != null) {
            ColorAdjust colorAdjust = new ColorAdjust();

            item.setOnMouseEntered(e -> {
                item.setStyle("-fx-cursor: hand;");
                colorAdjust.setContrast(0.05);
                item.setEffect(colorAdjust);

                item.setScaleX(1.05);
                item.setScaleY(1.05);

                FXGL.getAudioPlayer().playSound(MainApplication.getSfx_hover());
            });

            item.setOnMouseExited(e -> {
                item.setEffect(null);

                item.setScaleX(1.0);
                item.setScaleY(1.0);
            });
        }
    }
}