package com.example.joeandmarie.ui;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.IntroScene;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimatedTexture;
import javafx.animation.*;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import com.almasb.fxgl.audio.Music;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class JoeIntroScene extends  IntroScene{
    private final List<Animation<?>> animations = new ArrayList<>();

    public JoeIntroScene() {
        var root = getRoot();

        // --- Background image ---
        var background = FXGL.texture("background_plain.png");
        background.setFitWidth(FXGL.getAppWidth());
        background.setFitHeight(FXGL.getAppHeight());
        root.getChildren().add(background);

        // --- Title image ---
        var titleImage = FXGL.texture("menu_title_noshadow.png");
        titleImage.setTranslateX(FXGL.getAppWidth() / 2.0 - titleImage.getWidth() / 2.0);
        titleImage.setTranslateY(-titleImage.getHeight());
        titleImage.setOpacity(0);

        var fallTransition = new TranslateTransition(Duration.seconds(4), titleImage);
        fallTransition.setToY((FXGL.getAppHeight() / 2.0 - titleImage.getHeight() / 2.0) - 50);
        fallTransition.setInterpolator(Interpolators.BOUNCE.EASE_OUT());

        var fadeInImage = new FadeTransition(Duration.seconds(2), titleImage);
        fadeInImage.setFromValue(0);
        fadeInImage.setToValue(1);
        fadeInImage.setDelay(Duration.seconds(1));

        root.getChildren().add(titleImage);
        fallTransition.play();
        fadeInImage.play();

        // --- Progress bar ---
        ProgressBar progressBar = new ProgressBar(0);
        double barWidth = FXGL.getAppWidth() * 0.6;
        double barX = (FXGL.getAppWidth() - barWidth) / 2;
        double barY = FXGL.getAppHeight() - 50;

        progressBar.setPrefWidth(barWidth);
        progressBar.setPrefHeight(10);
        progressBar.setTranslateX(barX);
        progressBar.setTranslateY(barY);

        progressBar.setStyle(
                "-fx-accent: lightgreen;" +
                        "-fx-control-inner-background: #222;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-color: white;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-width: 1;"
        );

        root.getChildren().add(progressBar);

        // --- "by HardStack" label ---
        var labelImage = FXGL.texture("text_by_hardstack.png");
        labelImage.setOpacity(0);
        root.getChildren().add(labelImage);

        fadeInImage.setOnFinished(event -> {
            labelImage.setTranslateX(FXGL.getAppWidth() / 2.0 - labelImage.getWidth() / 2.0);
            labelImage.setTranslateY(titleImage.getTranslateY() + titleImage.getHeight() - 40);

            var labelFade = new FadeTransition(Duration.seconds(1), labelImage);
            labelFade.setFromValue(0);
            labelFade.setToValue(1);
            labelFade.play();
        });

        // --- Progress bar animation ---
        Timeline progressTimeline = new Timeline(
            new KeyFrame(Duration.seconds(5),
            new KeyValue(progressBar.progressProperty(), 1))
        );
        progressTimeline.play();

            // --- Animated Joe character ---
            var move = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8,
                    64, 64, Duration.seconds(1), 0, 5);
            var animatedJoe = new AnimatedTexture(move);
            animatedJoe.loopAnimationChannel(move);

        animatedJoe.setTranslateY(barY - 50);
        animatedJoe.setTranslateX(barX);
        root.getChildren().add(animatedJoe);

        TranslateTransition walk = new TranslateTransition(Duration.seconds(5), animatedJoe);
        walk.setFromX(barX);
        walk.setToX(barX + barWidth - 64);
        walk.setInterpolator(Interpolator.LINEAR);
        walk.play();

        // --- Scene transition ---
        progressTimeline.setOnFinished(e -> {
            var fadeOut = new FadeTransition(Duration.seconds(1), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> {
                root.getChildren().clear();
                finishIntro();
            });
            fadeOut.play();
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        animations.forEach(a -> a.onUpdate(tpf));
    }

    @Override
    public void startIntro() {
        animations.forEach(Animation::start);
    }
}
