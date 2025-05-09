package com.example.joeandmarie;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class JoeMainMenu extends FXGLMenu {
    public JoeMainMenu() {
        super(MenuType.MAIN_MENU);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/layouts/joe_main_menu.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/assets/layouts/css/style.css").toExternalForm());

            getContentRoot().getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
