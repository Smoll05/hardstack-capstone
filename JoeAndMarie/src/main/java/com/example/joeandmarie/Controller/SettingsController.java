package com.example.joeandmarie.Controller;

import com.example.joeandmarie.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SettingsController {
    @FXML private Slider slMusicVolume;
    @FXML private Slider slFXVolume;
    @FXML private CheckBox cbInfiJump;
    @FXML private CheckBox cbInfiClimb;
    @FXML private CheckBox cbInfiGrip;
    @FXML private ImageView btnExit;

    @FXML
    public void initialize() {
        setupHoverEffect(btnExit);
    }

    @FXML
    private void handleExitClick() {
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
            });

            item.setOnMouseExited(e -> {
                item.setEffect(null);

                item.setScaleX(1.0);
                item.setScaleY(1.0);
            });
        }
    }
}
