package com.example.joeandmarie.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.example.joeandmarie.data.dao.SettingPreferenceDao;
import com.example.joeandmarie.data.database.DatabaseInit;
import com.example.joeandmarie.data.model.SettingPreference;
import com.example.joeandmarie.data.viewmodel.SettingPreferenceViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class JoeMainMenu extends FXGLMenu {
    private final SettingPreferenceViewModel viewModel = SettingPreferenceViewModel.getInstance();
    private final SettingPreferenceDao dao = new SettingPreferenceDao();

    public JoeMainMenu() {
        super(MenuType.MAIN_MENU);
        DatabaseInit.initialize();

        SettingPreference snapshot = dao.selectSettingPreference();

        if(snapshot == null) {
            snapshot = new SettingPreference();
            dao.insertSettingPreference(snapshot);
        }

        viewModel.setState(snapshot);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/layouts/joe_main_menu.fxml"));
            Parent root = loader.load();
            getContentRoot().getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
