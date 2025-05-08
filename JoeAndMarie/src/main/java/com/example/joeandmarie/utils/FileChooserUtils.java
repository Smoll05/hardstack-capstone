package com.example.joeandmarie.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class FileChooserUtils {
    private static final FileChooser fileChooser = new FileChooser();

    public static String chooseFile(Window ownerWindow) {
        fileChooser.setTitle("Import Save File");

        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Save Files", "*.ajm")
        );

        File selectedFile = fileChooser.showOpenDialog(ownerWindow);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }

        return null;
    }

    public static String saveFile(Window ownerWindow) {
        fileChooser.setTitle("Export Save File");

        fileChooser.setInitialFileName("savegame.ajm");

        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Save Files", "*.ajm")
        );

        File selectedFile = fileChooser.showSaveDialog(ownerWindow);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }

        return null;
    }
}
