package com.example.joeandmarie.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.example.joeandmarie.controller.ScreenManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class JoeGameMenu extends FXGLMenu {
    public JoeGameMenu() {
        super(MenuType.GAME_MENU);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/layouts/joe_game_menu.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/assets/layouts/stylesheets/style.css").toExternalForm());
            getContentRoot().getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}