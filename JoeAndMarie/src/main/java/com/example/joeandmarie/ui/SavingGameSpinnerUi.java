package com.example.joeandmarie.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class SavingGameSpinnerUi {

    private final HBox spinnerView;

    public SavingGameSpinnerUi() {
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(64, 64);

        Label checkpointLabel = new Label("Saving Game");
        checkpointLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-family: \"Georgia\";");

        spinnerView = new HBox(10, checkpointLabel, spinner);
        spinnerView.setAlignment(Pos.CENTER);
        spinnerView.setPadding(new Insets(10));
        spinnerView.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-background-radius: 10;");

    }

    public HBox getView() {
        return spinnerView;
    }
}
