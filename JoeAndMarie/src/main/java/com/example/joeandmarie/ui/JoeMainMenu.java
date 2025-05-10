package com.example.joeandmarie.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.example.joeandmarie.MainApplication;
import com.example.joeandmarie.data.database.DatabaseInit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import com.almasb.fxgl.audio.Music;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAudioPlayer;

public class JoeMainMenu extends FXGLMenu {
    private Music mainMenuMusic;

    public JoeMainMenu() {
        super(MenuType.MAIN_MENU);
        MainApplication.setSfx_click(FXGL.getAssetLoader().loadSound("sound_meow.wav"));
        MainApplication.setSfx_hover(FXGL.getAssetLoader().loadSound("sound_button_hover.wav"));
        DatabaseInit.initialize();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/layouts/joe_main_menu.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/assets/layouts/stylesheets/style.css").toExternalForm());
            getContentRoot().getChildren().add(root);

            mainMenuMusic = getAssetLoader().loadMusic("music_main_menu.mp3");
            getAudioPlayer().loopMusic(mainMenuMusic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
