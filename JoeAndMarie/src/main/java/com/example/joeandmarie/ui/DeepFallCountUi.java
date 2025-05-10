package com.example.joeandmarie.ui;

import com.example.joeandmarie.data.model.GameProgress;
import com.example.joeandmarie.data.viewmodel.Observer;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DeepFallCountUi implements Observer<GameProgress> {

    private final Text deepfallText;

    public DeepFallCountUi() {
        deepfallText = new Text("Height: 0m");
        deepfallText.setFont(Font.font("Georgia", 24));
        deepfallText.setFill(Color.WHITE);
        deepfallText.setTranslateX(20);
        deepfallText.setTranslateY(80);
    }

    @Override
    public void update(GameProgress newState) {
        deepfallText.setText("Deep Fall Count: " + newState.getDeepFallCount());
    }

    public Node getView() {
        return deepfallText;
    }

}

