package com.example.joeandmarie.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import com.example.joeandmarie.data.dao.SettingPreferenceDao;
import com.example.joeandmarie.data.database.DatabaseInit;
import com.example.joeandmarie.data.model.SettingPreference;
import com.example.joeandmarie.data.viewmodel.SettingPreferenceViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAudioPlayer;

public class JoeMainMenu extends FXGLMenu {
    private final SettingPreferenceViewModel viewModel = SettingPreferenceViewModel.getInstance();
    private final SettingPreferenceDao dao = new SettingPreferenceDao();
    private static Music mainMenuMusic;

    public JoeMainMenu() {
        super(MenuType.MAIN_MENU);
        DatabaseInit.initialize();

        MainApplication.setSfx_click(FXGL.getAssetLoader().loadSound("sound_meow.wav"));
        MainApplication.setSfx_hover(FXGL.getAssetLoader().loadSound("sound_button_hover.wav"));

        SettingPreference snapshot = dao.selectSettingPreference();

        if(snapshot == null) {
            snapshot = new SettingPreference();
            dao.insertSettingPreference(snapshot);

            FXGL.getSettings().setGlobalSoundVolume(snapshot.getMusicVolume() / 100);
            FXGL.getSettings().setGlobalSoundVolume(snapshot.getFxVolume() / 100);
        }

        viewModel.setState(snapshot);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/layouts/joe_main_menu.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/assets/layouts/stylesheets/style.css").toExternalForm());
            getContentRoot().getChildren().add(root);

            mainMenuMusic = getAssetLoader().loadMusic("music_main_menu.mp3");
            getAudioPlayer().loopMusic(mainMenuMusic);        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Music getMainMenuMusic() {
        return mainMenuMusic;
    }
}
