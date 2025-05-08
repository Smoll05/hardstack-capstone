package com.example.joeandmarie.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import com.example.joeandmarie.data.dao.GameProgressDao;
import com.example.joeandmarie.data.dao.SaveProgressDao;
import com.example.joeandmarie.data.model.GameProgress;
import com.example.joeandmarie.utils.FileChooserUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Window;

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

    private final SaveProgressDao saveDao = new SaveProgressDao();
    private final GameProgressDao gameDao = new GameProgressDao();

    @FXML
    public void initialize() {

        resetFileSlotUi();

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

        // Check if there exist some saved slots
        setSavedFiles();
    }

//    To implement: importing and exporting files, loading game onclick of AnchorPane Background
    @FXML
    private void handleFileClick(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();

        switch (clickedButton.getId()) {
            case "btnPlay1":
                // TODO("Use a singleton ViewModel to save state")
                setPlay(1);
                break;

            case "btnPlay2":
                setPlay(3);
                break;

            case "btnPlay3":
                setPlay(3);
                break;

            default:
                break;
        }

        FXGL.getGameController().startNewGame();
        switchScreenToMainMenu();
    }

    @FXML
    private void handleExportClick(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();

        switch (clickedButton.getId()) {
            case "btnExport1":
                saveFile(1, event);
                break;

            case "btnExport2":
                saveFile(2, event);
                break;

            case "btnExport3":
                saveFile(3, event);
                break;

            default:
                break;
        }
    }

    @FXML
    private void handleNewFileClick(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();

        switch (clickedButton.getId()) {
            case "btnNewGame1":
                setNewGame(1);
                break;

            case "btnNewGame2":
                setNewGame(2);
                break;

            case "btnNewGame3":
                setNewGame(3);
                break;

            default:
                break;
        }

        FXGL.getGameController().startNewGame();
        switchScreenToMainMenu();
    }

    @FXML
    private void handleImportClick(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();

        switch (clickedButton.getId()) {
            case "btnImport1":
                chooseFile(1, event);
                break;

            case "btnImport2":
                chooseFile(2, event);
                break;

            case "btnImport3":
                chooseFile(3, event);
                break;

            default:
                break;
        }
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


    private void setSavedFiles() {
        if(saveDao.selectSaveProgress(1)) {
            setHasSaveUi(1);
        } else {
            setHasNoSaveUi(1);
        }

        if(saveDao.selectSaveProgress(2)) {
            setHasSaveUi(2);
        } else {
            setHasNoSaveUi(2);
        }

        if(saveDao.selectSaveProgress(3)) {
            setHasSaveUi(3);
        } else {
            setHasNoSaveUi(3);
        }
    }

    private void setHasSaveUi(int saveSlotNum) {

        // Export Button
        Button btnExport = new Button("Export Save File");
        btnExport.setLayoutX(182.0);
        btnExport.setLayoutY(492.0);
        btnExport.setFont(Font.font(15));
        btnExport.setOnMouseClicked(this::handleExportClick);
        btnExport.setId("btnExport" + saveSlotNum);

        // Play Button
        Button btnPlay = new Button("Play");
        btnPlay.setLayoutX(183.0);
        btnPlay.setLayoutY(449.0);
        btnPlay.setPrefWidth(124.0);
        btnPlay.setPrefHeight(31.0);
        btnPlay.setFont(Font.font(15));
        btnPlay.setOnMouseClicked(this::handleFileClick);
        btnPlay.setId("btnPlay" + saveSlotNum);

        // File Name Label
        Label lblFileName = new Label("File 1");
        lblFileName.setLayoutX(208.0);
        lblFileName.setLayoutY(258.0);
        lblFileName.setTextFill(Color.WHITE);
        lblFileName.setFont(Font.font("System", FontWeight.BOLD, 30));
        lblFileName.setId("lblFileName");

        // Progress Label
        Label lblProgress = new Label("0m");
        lblProgress.setLayoutX(232.0);
        lblProgress.setLayoutY(303.0);
        lblProgress.setTextFill(Color.WHITE);
        lblProgress.setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 15));
        lblProgress.setId("lblProgress");

        switch (saveSlotNum) {
            case 1:
                apFile1.getChildren().addAll(
                    ivContainerBackground1,
                    btnExport,
                    btnPlay,
                    lblFileName,
                    lblProgress
                );
                break;

            case 2:
                apFile2.getChildren().addAll(
                    ivContainerBackground2,
                    btnExport,
                    btnPlay,
                    lblFileName,
                    lblProgress
                );
                break;

            case 3:
                apFile3.getChildren().addAll(
                    ivContainerBackground3,
                    btnExport,
                    btnPlay,
                    lblFileName,
                    lblProgress
                );
                break;

            default:
                break;
        }

    }

    private void setHasNoSaveUi(int saveSlotNum) {

        // New Game Button
        Button btnNewGame = new Button("+");
        btnNewGame.setLayoutX(162.0);
        btnNewGame.setLayoutY(358.0);
        btnNewGame.setFont(Font.font(30));
        btnNewGame.setOnMouseClicked(this::handleNewFileClick);
        btnNewGame.setId("btnNewGame" + saveSlotNum);

        // Import Game Button
        Button btnImportGame = new Button("↓");
        btnImportGame.setLayoutX(273.0);
        btnImportGame.setLayoutY(358.0);
        btnImportGame.setFont(Font.font(30));
        btnImportGame.setOnMouseClicked(this::handleImportClick);
        btnImportGame.setId("btnImport" + saveSlotNum);

        switch (saveSlotNum) {
            case 1:
                apFile1.getChildren().addAll(ivContainerBackground1, btnNewGame, btnImportGame);
                break;

            case 2:
                apFile2.getChildren().addAll(ivContainerBackground2, btnNewGame, btnImportGame);
                break;

            case 3:
                apFile3.getChildren().addAll(ivContainerBackground3, btnNewGame, btnImportGame);
                break;

            default:
                break;
        }

    }

    private void chooseFile(int saveSlot, MouseEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        String filePath = FileChooserUtils.chooseFile(window);

        if (filePath != null) {
            System.out.println("Imported file: " + filePath);
        } else {
            System.out.println("No file selected.");
        }
    }

    private void saveFile(int saveSlot, MouseEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        String filePath = FileChooserUtils.chooseFile(window);

        if (filePath != null) {
            System.out.println("Exported file: " + filePath);
        } else {
            System.out.println("No file selected.");
        }
    }


    private void setPlay(int saveSlot) {

    }

    private void setNewGame(int saveSlot) {
        saveDao.insertSaveProgress(saveSlot);
        gameDao.insertGameProgress(new GameProgress(saveSlot, saveSlot));
    }

    private void resetFileSlotUi() {
        apFile1.getChildren().clear();
        apFile2.getChildren().clear();
        apFile3.getChildren().clear();
    }
}
