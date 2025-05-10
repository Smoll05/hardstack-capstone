package com.example.joeandmarie.ui;

import com.example.joeandmarie.data.model.GameProgress;
import com.example.joeandmarie.data.viewmodel.Observer;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HeightProgressUi implements Observer<GameProgress> {

    private final Text heightText;

    public HeightProgressUi() {
        heightText = new Text("Height: 0m");
        heightText.setFont(Font.font("Verdana", 24));
        heightText.setFill(Color.WHITE);
        heightText.setTranslateX(20);
        heightText.setTranslateY(50);
    }

    @Override
    public void update(GameProgress newState) {
        heightText.setText("Height: " + newState.getHeightProgress() + "m");
    }

    public Node getView() {
        return heightText;
    }

}
