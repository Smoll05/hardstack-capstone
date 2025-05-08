package com.example.joeandmarie.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.example.joeandmarie.data.database.DatabaseInit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class JoeMainMenu extends FXGLMenu {
    public JoeMainMenu() {
        super(MenuType.MAIN_MENU);

        DatabaseInit.initialize();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/joe_main_menu.fxml"));
            Parent root = loader.load();
            getContentRoot().getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
