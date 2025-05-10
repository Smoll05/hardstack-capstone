package com.example.joeandmarie.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import com.example.joeandmarie.data.dao.SettingPreferenceDao;
import com.example.joeandmarie.data.event.SettingPreferenceEvent;
import com.example.joeandmarie.data.model.SettingPreference;
import com.example.joeandmarie.data.viewmodel.Observer;
import com.example.joeandmarie.data.viewmodel.SettingPreferenceViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class GameMenuSettingsController implements Observer<SettingPreference> {
    @FXML private Slider slMusicVolume;
    @FXML private Slider slFXVolume;
    @FXML private CheckBox cbInfiJump;
    @FXML private CheckBox cbInfiClimb;
    @FXML private CheckBox cbInfiGrip;
    @FXML private ImageView btnExit;
    @FXML private Label lblMusicVolume;
    @FXML private Label lblFxVolume;

    private final SettingPreferenceViewModel viewModel = SettingPreferenceViewModel.getInstance();
    private final SettingPreferenceDao dao = new SettingPreferenceDao();
    private SettingPreference snapshot;

    private AnchorPane parentContainer;

    @FXML
    private void initialize() {
        viewModel.addObserver(this);
        update(viewModel.getSnapshot());

        slMusicVolume.valueProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_MUSIC_VOLUME, newValue.floatValue());
            snapshot = viewModel.getSnapshot();
            FXGL.getSettings().setGlobalMusicVolume(snapshot.getMusicVolume() / 100);
        });

        slFXVolume.valueProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_FX_VOLUME, newValue.floatValue());
            snapshot = viewModel.getSnapshot();
            FXGL.getSettings().setGlobalSoundVolume(snapshot.getFxVolume() / 100);
        });

        cbInfiJump.selectedProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_INFINITE_JUMP, newValue);
            FXGL.getAudioPlayer().playSound(MainApplication.getSfx_click());
        });

        cbInfiClimb.selectedProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_CLIMB_WALLS, newValue);
            FXGL.getAudioPlayer().playSound(MainApplication.getSfx_click());
        });

        cbInfiGrip.selectedProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_INFINITE_GRIP, newValue);
            FXGL.getAudioPlayer().playSound(MainApplication.getSfx_click());
        });

        setupHoverEffect(btnExit);
    }

    @FXML
    private void handleExitClick() {
        viewModel.saveSettingPreference();
        FXGL.getAudioPlayer().playSound(MainApplication.getSfx_click());
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/assets/layouts/joe_game_menu.fxml"));
            Parent newContent = loader.load();
            newContent.getStylesheets().add(MainApplication.class.getResource("/assets/layouts/stylesheets/style.css").toExternalForm());

            if (parentContainer != null) {
                parentContainer.getChildren().setAll(newContent);
            } else {
                System.err.println("Parent container is null!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(SettingPreference state) {

        slMusicVolume.setValue(state.getMusicVolume());
        slFXVolume.setValue(state.getFxVolume());

        lblMusicVolume.setText(String.format("%.0f", state.getMusicVolume()));
        lblFxVolume.setText(String.format("%.0f", state.getFxVolume()));

        cbInfiJump.setSelected(state.isInfiniteJump());
        cbInfiClimb.setSelected(state.isClimbWalls());
        cbInfiGrip.setSelected(state.isInfiniteGrip());

        System.out.println("Something Changed");

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

    public void setParentContainer(AnchorPane container) {
        this.parentContainer = container;
    }
}
