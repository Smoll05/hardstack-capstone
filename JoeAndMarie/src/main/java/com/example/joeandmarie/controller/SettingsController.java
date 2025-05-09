package com.example.joeandmarie.controller;

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
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SettingsController implements Observer<SettingPreference> {
    @FXML private AnchorPane apContainer;
    @FXML private Slider slMusicVolume;
    @FXML private Slider slFXVolume;
    @FXML private CheckBox cbInfiJump;
    @FXML private CheckBox cbInfiClimb;
    @FXML private CheckBox cbInfiGrip;

    private final SettingPreferenceViewModel viewModel = SettingPreferenceViewModel.getInstance();
    private final SettingPreferenceDao dao = new SettingPreferenceDao();

    @FXML
    private void initialize() {

        viewModel.addObserver(this);
        update(viewModel.getSnapshot());

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

    }

    @FXML
    private void handleExitClick() {
        viewModel.saveSettingPreference();
        switchScreenToMainMenu();
    }

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

    @Override
    public void update(SettingPreference state) {

        slMusicVolume.setValue(state.getMusicVolume());
        slFXVolume.setValue(state.getFxVolume());

        cbInfiJump.setSelected(state.isInfiniteJump());
        cbInfiClimb.setSelected(state.isClimbWalls());
        cbInfiGrip.setSelected(state.isInfiniteGrip());

        System.out.println("Something Changed");

    }
}
