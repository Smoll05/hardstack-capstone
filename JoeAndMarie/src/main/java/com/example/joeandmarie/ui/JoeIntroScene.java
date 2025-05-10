package com.example.joeandmarie.ui;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.IntroScene;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimatedTexture;
import javafx.animation.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.paint.Color; // Make sure this is imported


import java.util.ArrayList;
import java.util.List;

public class JoeIntroScene {
    public static class MyIntroScene extends IntroScene {
        private final List<Animation<?>> animations = new ArrayList<>();

        public MyIntroScene() {
            var root = getRoot();
// --- Background image setup ---
            var background = FXGL.texture("background_plain.png"); // Replace with your background image filename
            background.setFitWidth(FXGL.getAppWidth());
            background.setFitHeight(FXGL.getAppHeight());

            root.getChildren().add(background);

            // --- Title image setup ---
            var titleImage = FXGL.texture("menu_title_noshadow.png");
            titleImage.setTranslateX(FXGL.getAppWidth() / 2.0 - titleImage.getWidth() / 2.0);
            titleImage.setTranslateY(-titleImage.getHeight());
            titleImage.setOpacity(0);

            var fallTransition = new TranslateTransition(Duration.seconds(4), titleImage);
            fallTransition.setToY(FXGL.getAppHeight() / 2.0 - titleImage.getHeight() / 2.0);
            fallTransition.setInterpolator(Interpolators.BOUNCE.EASE_OUT());

            var fadeInImage = new FadeTransition(Duration.seconds(2), titleImage);
            fadeInImage.setFromValue(0);
            fadeInImage.setToValue(1);
            fadeInImage.setDelay(Duration.seconds(1));

            root.getChildren().add(titleImage);
            fallTransition.play();
            fadeInImage.play();

            // --- Progress bar setup ---
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

            // --- "by HardStack" label setup ---
            var titleImages = FXGL.texture("text_by_hardstack.png");
//          Text byLabel = FXGL.getUIFactoryService().newText("by HardStack", 18);
            titleImages.setOpacity(0);
            root.getChildren().add(titleImages);

            // After fade-in animation, show "by HardStack" centered below image
            fadeInImage.setOnFinished(event -> {
                titleImages.setTranslateX(FXGL.getAppWidth() / 2.0 - titleImages.getWidth() / 2.0);
                titleImages.setTranslateY(titleImage.getTranslateY() + titleImage.getHeight() - 20);

                var labelFade = new FadeTransition(Duration.seconds(1), titleImages);
                labelFade.setFromValue(0);
                labelFade.setToValue(1);
                labelFade.play();
            });

            // Animate progress bar fill over 10 seconds
            Timeline progressTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(5),
                            new KeyValue(progressBar.progressProperty(), 1))
            );
            progressTimeline.play();

            // --- Character setup ---
            var move = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8,
                    64, 64, Duration.seconds(1), 0, 5);
            var animatedJoe = new AnimatedTexture(move);
            animatedJoe.loopAnimationChannel(move);

            animatedJoe.setTranslateY(barY - 50); // Position just above the progress bar

// Set cat's size (optional, in case it's too big/small visually)
// animatedJoe.setFitWidth(64);
// animatedJoe.setFitHeight(64);

// Start at the left edge of the progress bar
            animatedJoe.setTranslateX(barX); // Start at progress bar X

            TranslateTransition walk = new TranslateTransition(Duration.seconds(5), animatedJoe);
            walk.setFromX(barX);
            walk.setToX(barX + barWidth - 64); // End at end of progress bar minus sprite width
            walk.setInterpolator(Interpolator.LINEAR);
            walk.play();


            root.getChildren().add(animatedJoe);

            // Clear intro scene and go to main menu after 10 seconds
            progressTimeline.setOnFinished(e -> {
                var fadeOut = new FadeTransition(Duration.seconds(1), getRoot());
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(ev -> {
                    getRoot().getChildren().clear();
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
}
