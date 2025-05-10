package com.example.joeandmarie.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import com.example.joeandmarie.data.dao.GameProgressDao;
import com.example.joeandmarie.data.dao.SaveProgressDao;
import com.example.joeandmarie.data.model.GameProgress;
import com.example.joeandmarie.data.viewmodel.GameProgressViewModel;
import com.example.joeandmarie.utils.FileChooserUtils;
import com.example.joeandmarie.utils.SerializationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
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

    @FXML private ImageView ivContainerBackground1; // temporary container backgrounds
    @FXML private ImageView ivContainerBackground2;
    @FXML private ImageView ivContainerBackground3;

    @FXML
    private ImageView btnExit;

    private final SaveProgressDao saveDao = new SaveProgressDao();
    private final GameProgressDao gameDao = new GameProgressDao();

    private final GameProgressViewModel viewModel = GameProgressViewModel.getInstance();

    @FXML
    public void initialize() {
        Image containerImage = new Image(getClass().getResource("/assets/textures/save_files_container.png").toExternalForm());
        ivContainerBackground1.setImage(containerImage);
        ivContainerBackground1.setPreserveRatio(true);
        ivContainerBackground1.setSmooth(true);

        ivContainerBackground2.setImage(containerImage);
        ivContainerBackground2.setPreserveRatio(true);
        ivContainerBackground2.setSmooth(true);

        ivContainerBackground3.setImage(containerImage);
        ivContainerBackground3.setPreserveRatio(true);
        ivContainerBackground3.setSmooth(true);

        resetFileSlotUi();
        setSavedFiles();

        setupHoverEffect(btnExit);
    }

    //    To implement: importing and exporting files, loading game onclick of AnchorPane Background
    @FXML
    private void handleFileClick(MouseEvent event) {
        ImageView clickedButton = (ImageView) event.getSource();

        switch (clickedButton.getId()) {
            case "btnPlay1":
                setPlay(1);
                break;

            case "btnPlay2":
                setPlay(2);
                break;

            case "btnPlay3":
                setPlay(3);
                break;

            default:
                break;
        }

        FXGL.getAudioPlayer().playSound(MainApplication.getSfx_click());
        FXGL.getGameController().startNewGame();
        ScreenManager.switchScreen("/assets/layouts/joe_main_menu.fxml");
    }

    @FXML
    private void handleExportClick(MouseEvent event) {
        ImageView clickedButton = (ImageView) event.getSource();

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
    private void handleDeleteClick(MouseEvent event) {
        ImageView clickedButton = (ImageView) event.getSource();

        switch (clickedButton.getId()) {
            case "btnDelete1":
                deleteSaveFile(1);
                resetFileSlotUi();
                setSavedFiles();
                break;

            case "btnDelete2":
                deleteSaveFile(2);
                resetFileSlotUi();
                setSavedFiles();
                break;

            case "btnDelete3":
                deleteSaveFile(3);
                resetFileSlotUi();
                setSavedFiles();
                break;

            default:
                break;
        }
    }

    private void deleteSaveFile(int saveSlot) {
        saveDao.deleteSaveProgress(saveSlot);
        gameDao.deleteGameProgress(saveSlot);
    }

    @FXML
    private void handleNewFileClick(MouseEvent event) {
        ImageView clickedButton = (ImageView) event.getSource();

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
        ScreenManager.switchScreen("/assets/layouts/joe_main_menu.fxml");
    }

    @FXML
    private void handleImportClick(MouseEvent event) {
        ImageView clickedButton = (ImageView) event.getSource();

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
        ScreenManager.switchScreen("/assets/layouts/joe_main_menu.fxml");
    }

    private void setSavedFiles() {
        if (saveDao.selectSaveProgress(1)) {
            setHasSaveUi(1);
        } else {
            setHasNoSaveUi(1);
        }

        if (saveDao.selectSaveProgress(2)) {
            setHasSaveUi(2);
        } else {
            setHasNoSaveUi(2);
        }

        if (saveDao.selectSaveProgress(3)) {
            setHasSaveUi(3);
        } else {
            setHasNoSaveUi(3);
        }
    }

    private void setHasSaveUi(int saveSlotNum) {
        // File Name Label
        Label lblFileName = new Label("File " + saveSlotNum);
        lblFileName.setLayoutX(132.0);
        lblFileName.setLayoutY(138.0);
        lblFileName.setTextFill(Color.WHITE);
        lblFileName.setFont(Font.font("System", FontWeight.BOLD, 64));
        lblFileName.setId("lblFileName" + saveSlotNum);

        // Progress Label
        int progress = gameDao.selectHeightGameProgress(saveSlotNum); // assumes you have a DAO returning height
        Label lblProgress = new Label(progress + "m");
        lblProgress.setLayoutX(175.0);
        lblProgress.setLayoutY(206.0);
        lblProgress.setTextFill(Color.WHITE);
        lblProgress.setFont(Font.font("System", FontPosture.ITALIC, 48));
        lblProgress.setId("lblProgress" + saveSlotNum);

        // Play Button
        ImageView btnPlay = new ImageView(new Image(getClass().getResource("/assets/textures/button_play.png").toExternalForm()));
        btnPlay.setFitWidth(84);
        btnPlay.setFitHeight(84);
        btnPlay.setLayoutX(221.0);
        btnPlay.setLayoutY(293.0);
        btnPlay.setPreserveRatio(true);
        btnPlay.setPickOnBounds(true);
        btnPlay.setOnMouseClicked(this::handleFileClick);
        btnPlay.setId("btnPlay" + saveSlotNum);

        // Export Button
        ImageView btnExport = new ImageView(new Image(getClass().getResource("/assets/textures/button_export.png").toExternalForm()));
        btnExport.setFitWidth(84);
        btnExport.setFitHeight(84);
        btnExport.setLayoutX(114.0);
        btnExport.setLayoutY(293.0);
        btnExport.setPreserveRatio(true);
        btnExport.setPickOnBounds(true);
        btnExport.setOnMouseClicked(this::handleExportClick);
        btnExport.setId("btnExport" + saveSlotNum);

        // Delete Button
        ImageView btnDelete = new ImageView(new Image(getClass().getResource("/assets/textures/button_delete.png").toExternalForm()));
        btnDelete.setFitWidth(84);
        btnDelete.setFitHeight(84);
        btnDelete.setLayoutX(167.0);
        btnDelete.setLayoutY(377.0);
        btnDelete.setPreserveRatio(true);
        btnDelete.setPickOnBounds(true);
        btnDelete.setOnMouseClicked(this::handleDeleteClick);
        btnDelete.setId("btnDelete" + saveSlotNum);

        // Add to appropriate Pane
        switch (saveSlotNum) {
            case 1:
                apFile1.getChildren().addAll(
                        ivContainerBackground1,
                        lblFileName,
                        lblProgress,
                        btnPlay,
                        btnExport,
                        btnDelete
                );
                break;
            case 2:
                apFile2.getChildren().addAll(
                        ivContainerBackground2,
                        lblFileName,
                        lblProgress,
                        btnPlay,
                        btnExport,
                        btnDelete
                );
                break;
            case 3:
                apFile3.getChildren().addAll(
                        ivContainerBackground3,
                        lblFileName,
                        lblProgress,
                        btnPlay,
                        btnExport,
                        btnDelete
                );
                break;
        }

        setupHoverEffect(btnPlay);
        setupHoverEffect(btnExport);
        setupHoverEffect(btnDelete);
    }

    private void setHasNoSaveUi(int saveSlotNum) {
        // New Game Button (ImageView)
        ImageView btnNewGame = new ImageView(new Image(getClass().getResource("/assets/textures/button_play.png").toExternalForm()));
        btnNewGame.setFitWidth(84);
        btnNewGame.setFitHeight(84);
        btnNewGame.setLayoutX(223);
        btnNewGame.setLayoutY(258);
        btnNewGame.setPickOnBounds(true);
        btnNewGame.setPreserveRatio(true);
        btnNewGame.setOnMouseClicked(this::handleNewFileClick);
        btnNewGame.setId("btnNewGame" + saveSlotNum);

        // Import Button (ImageView)
        ImageView btnImportGame = new ImageView(new Image(getClass().getResource("/assets/textures/button_import.png").toExternalForm()));
        btnImportGame.setFitWidth(84);
        btnImportGame.setFitHeight(84);
        btnImportGame.setLayoutX(113);
        btnImportGame.setLayoutY(258);
        btnImportGame.setPickOnBounds(true);
        btnImportGame.setPreserveRatio(true);
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
        }

        setupHoverEffect(btnNewGame);
        setupHoverEffect(btnImportGame);
    }

    private void chooseFile(int saveSlot, MouseEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        String filePath = FileChooserUtils.chooseFile(window);

        if (filePath == null) return;

        GameProgress object = SerializationUtils.deserialize(filePath);
        object.setGameProgressId(saveSlot);
        object.setSaveProgressId(saveSlot);

        saveDao.insertSaveProgress(saveSlot);
        gameDao.insertGameProgress(object);

        switch (saveSlot) {
            case 1:
                apFile1.getChildren().clear();
                break;
            case 2:
                apFile2.getChildren().clear();
                break;
            case 3:
                apFile3.getChildren().clear();
                break;
        }

        setHasSaveUi(saveSlot);
        System.out.println("Imported file: " + filePath);
    }

    private void saveFile(int saveSlot, MouseEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        String filePath = FileChooserUtils.saveFile(window);

        if (filePath == null) return;

        GameProgress object = gameDao.selectGameProgress(saveSlot);
        SerializationUtils.serialize(filePath, object);

        System.out.println("Exported file: " + filePath);
    }


    private void setPlay(int saveSlot) {
        viewModel.setState(gameDao.selectGameProgress(saveSlot));
    }

    private void setNewGame(int saveSlot) {
        GameProgress state = new GameProgress(saveSlot, saveSlot);
        saveDao.insertSaveProgress(saveSlot);
        gameDao.insertGameProgress(state);
        viewModel.setState(state);
    }

    private void resetFileSlotUi() {
        apFile1.getChildren().clear();
        apFile2.getChildren().clear();
        apFile3.getChildren().clear();
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