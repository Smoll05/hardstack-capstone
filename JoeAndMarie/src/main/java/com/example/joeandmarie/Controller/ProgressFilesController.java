package com.example.joeandmarie.Controller;

import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ProgressFilesController {
    @FXML private Label lblFileName; // file number label for container 1
    @FXML private Label lblProgress; // height progress label for container 1

    @FXML private ImageView btnPlay; // play button to continue playing
    @FXML private ImageView btnExport; // export current game file

    @FXML private ImageView btnNewGame; // new game button for file 2
    @FXML private ImageView btnImportGame; // import file button for file 2

    @FXML private ImageView btnNewGame2; // new game button for file 3
    @FXML private ImageView btnImportGame2; // import file button for file 3

    @FXML private ImageView btnExit;

    @FXML
    public void initialize() {
        setupHoverEffect(btnExit);
        setupHoverEffect(btnPlay);
        setupHoverEffect(btnExport);
        setupHoverEffect(btnNewGame);
        setupHoverEffect(btnNewGame2);
        setupHoverEffect(btnImportGame);
        setupHoverEffect(btnImportGame2);
    }

//    To implement: importing and exporting files, loading game onclick of AnchorPane Background
    @FXML
    private void handleFileClick() {
        FXGL.getGameController().startNewGame();
        ScreenManager.switchScreen("/assets/layouts/joe_main_menu.fxml");
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
