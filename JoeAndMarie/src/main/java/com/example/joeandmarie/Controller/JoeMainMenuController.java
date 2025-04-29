package com.example.joeandmarie.Controller;

import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.awt.*;

public class JoeMainMenuController {

    @FXML
    private ImageView bgImage;
    @FXML
    private StackPane coopCampaign;

    @FXML
    private StackPane multiplayer;

    @FXML
    private StackPane collection;

    @FXML
    private StackPane settings;

    @FXML
    private StackPane quit;

    @FXML
    public void initialize() {

        Image image = new Image(getClass().getResource("/assets/textures/bgimages.png").toExternalForm());
        bgImage.setImage(image);


        setupHoverEffect(coopCampaign);
        setupHoverEffect(multiplayer);
        setupHoverEffect(collection);
        setupHoverEffect(settings);
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
    private void handleCoopCampaignClick() {
        FXGL.getGameController().startNewGame();
    }

    @FXML
    private void handleMultiplayerClick() {
        System.out.println("Multiplayer clicked");
    }

    @FXML
    private void handleCollectionClick() {
        System.out.println("Collection clicked");
    }

    @FXML
    private void handleSettingsClick() {
        System.out.println("Settings clicked");
    }

    @FXML
    private void handleQuitClick() {
        FXGL.getGameController().exit();
    }
}
