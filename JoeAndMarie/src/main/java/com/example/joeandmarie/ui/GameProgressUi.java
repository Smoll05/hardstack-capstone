package com.example.joeandmarie.ui;

import com.example.joeandmarie.data.model.GameProgress;
import com.example.joeandmarie.data.viewmodel.Observer;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameProgressUi implements Observer<GameProgress> {

    private final Text heightText;

    public GameProgressUi() {
        heightText = new Text("Height: 0");
        heightText.setFont(Font.font("Verdana", 24)); // Set font and size
        heightText.setTranslateX(20);
        heightText.setTranslateY(50);
    }

    @Override
    public void update(GameProgress newState) {
        heightText.setText("Height: " + newState.getHeightProgress());
    }

    public Node getView() {
        return heightText;
    }

}
