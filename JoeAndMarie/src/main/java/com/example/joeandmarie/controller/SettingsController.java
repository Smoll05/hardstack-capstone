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

public class SettingsController implements Observer<SettingPreference> {
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

    @FXML
    private void initialize() {

        viewModel.addObserver(this);

        if(!viewModel.hasCurrentState()) {
            SettingPreference snapshot = dao.selectSettingPreference();

            if(snapshot == null) {
                snapshot = new SettingPreference();
                dao.insertSettingPreference(snapshot);
            }

            viewModel.setState(snapshot);
            update(snapshot);
        } else {
            update(viewModel.getSnapshot());
        }

        slMusicVolume.valueProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_MUSIC_VOLUME, newValue.floatValue());
        });

        slFXVolume.valueProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_FX_VOLUME, newValue.floatValue());
        });

        cbInfiJump.selectedProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_INFINITE_JUMP, newValue);
        });

        cbInfiClimb.selectedProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_CLIMB_WALLS, newValue);
        });

        cbInfiGrip.selectedProperty().addListener((_, _, newValue) -> {
            viewModel.onEvent(SettingPreferenceEvent.UPDATE_INFINITE_GRIP, newValue);
        });

        setupHoverEffect(btnExit);
    }

    @FXML
    private void handleExitClick() {
        viewModel.saveSettingPreference();
        ScreenManager.switchScreen("/assets/layouts/joe_main_menu.fxml");
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
            });

            item.setOnMouseExited(e -> {
                item.setEffect(null);

                item.setScaleX(1.0);
                item.setScaleY(1.0);
            });
        }
    }
}
