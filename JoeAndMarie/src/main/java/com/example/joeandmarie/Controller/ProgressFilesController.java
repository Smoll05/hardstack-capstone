package com.example.joeandmarie.Controller;

import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ProgressFilesController {
    @FXML private AnchorPane apFile1; //anchorpanes for files, has click handlers to continue playing game for now
    @FXML private AnchorPane apFile2;
    @FXML private AnchorPane apFile3;
    @FXML private ImageView ivBackground;
    @FXML private AnchorPane apContainer;

    @FXML private ImageView ivContainerBackground1; // temporary container backgrounds
    @FXML private ImageView ivContainerBackground2;
    @FXML private ImageView ivContainerBackground3;

    @FXML private Label lblFileName; // file number label for container 1
    @FXML private Label lblProgress; // height progress label for container 1
    @FXML private Button btnPlay; // play button to continue playing
    @FXML private Button btnExport; // export current game file

    @FXML private Button btnNewGame; // new game button for file 2
    @FXML private Button btnImportGame; // import file button for file 2

    @FXML private Button btnNewGame2; // new game button for file 3
    @FXML private Button btnImportGame2; // import file button for file 3

    @FXML
    public void initialize() {
        Image image = new Image(getClass().getResource("/assets/textures/menu_bg4_pixelated.png").toExternalForm());
        ivBackground.setImage(image);
        ivBackground.setPreserveRatio(true);
        ivBackground.setSmooth(true);

        Image containerImage = new Image(getClass().getResource("/assets/textures/temp_file_container.png").toExternalForm());
        ivContainerBackground1.setImage(containerImage);
        ivContainerBackground1.setPreserveRatio(true);
        ivContainerBackground1.setSmooth(true);

        ivContainerBackground2.setImage(containerImage);
        ivContainerBackground2.setPreserveRatio(true);
        ivContainerBackground2.setSmooth(true);

        ivContainerBackground3.setImage(containerImage);
        ivContainerBackground3.setPreserveRatio(true);
        ivContainerBackground3.setSmooth(true);

        javafx.application.Platform.runLater(() -> {
            if (ivBackground.getScene() != null) {
                ivBackground.fitWidthProperty().bind(ivBackground.getScene().widthProperty());
                ivBackground.fitHeightProperty().bind(ivBackground.getScene().heightProperty());
            }
        });
    }

//    To implement: importing and exporting files, loading game onclick of AnchorPane Background
    @FXML
    private void handleFileClick() {
        FXGL.getGameController().startNewGame();
        switchScreenToMainMenu();
    }

    @FXML
    private void handleExitClick() {
        switchScreenToMainMenu();
    }


//    Switches back the contents of the menu screen to the main menu
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
