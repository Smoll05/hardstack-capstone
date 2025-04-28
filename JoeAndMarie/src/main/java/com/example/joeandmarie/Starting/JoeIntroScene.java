/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.example.joeandmarie.Starting;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.IntroScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.animation.FadeTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Media; // ✅ Correct import

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows how to customize and provide own intro.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JoeIntroScene {

    public static class MyIntroScene extends IntroScene {
        private AnimationChannel move;
        private AnimationChannel texture;
        private static final int SIZE = 150;
        private List<Animation<?>> animations = new ArrayList<>();
        private int index = 0;


        public MyIntroScene() {
            var root = getRoot();

            // Load and play video first
            String videoPath = "/assets/Video/undeground.mp4";
            var mediaURL = getClass().getResource(videoPath);

            if (mediaURL == null) {
                System.err.println("Video file not found: " + videoPath);
                return;
            }

            var media = new Media(mediaURL.toExternalForm());
            var mediaPlayer = new MediaPlayer(media);
            var mediaView = new MediaView(mediaPlayer);

            mediaView.setFitWidth(FXGL.getAppWidth());
            mediaView.setFitHeight(FXGL.getAppHeight());
            mediaView.setOpacity(0); // Start transparent

            // Load image from FXGL assets
            var titleImage = FXGL.texture("Titles.png");
            titleImage.setTranslateX(FXGL.getAppWidth() / 2.0 - titleImage.getWidth() / 2.0);
            titleImage.setTranslateY(FXGL.getAppHeight() / 2.0 - titleImage.getHeight() / 2.0);
            titleImage.setOpacity(0); // Start transparent

            // Create and style the progress bar
            ProgressBar progressBar = new ProgressBar(0);
            double barWidth = FXGL.getAppWidth() * 0.6;
            progressBar.setPrefWidth(barWidth);
            progressBar.setPrefHeight(10);
            progressBar.setTranslateX((FXGL.getAppWidth() - barWidth) / 2);
            progressBar.setTranslateY(FXGL.getAppHeight() - 30);
            progressBar.setStyle(
                    "-fx-accent: #00ff88;" +
                            "-fx-control-inner-background: #222;" +
                            "-fx-background-insets: 0;" +
                            "-fx-background-radius: 5;" +
                            "-fx-border-color: white;" +
                            "-fx-border-radius: 5;" +
                            "-fx-border-width: 1;"
            );

            // --- Setup character animation ---
            move = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
            texture = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
            var animatedJoe = new com.almasb.fxgl.texture.AnimatedTexture(move);
            animatedJoe.loopAnimationChannel(move);

            // Position above progress bar
            animatedJoe.setTranslateX((FXGL.getAppWidth() - barWidth) / 2);
            animatedJoe.setTranslateY(progressBar.getTranslateY() - 64 - 10); // 10px above

            // Add all visual elements to root
            root.getChildren().addAll(mediaView, titleImage, progressBar, animatedJoe);

            // Fade-in animation for video
            var fadeInVideo = new FadeTransition(Duration.seconds(0.5), mediaView);
            fadeInVideo.setFromValue(0);
            fadeInVideo.setToValue(1);

            // Fade-in for title image
            var fadeInImage = new FadeTransition(Duration.seconds(2), titleImage);
            fadeInImage.setFromValue(0);
            fadeInImage.setToValue(1);
            fadeInImage.setDelay(Duration.seconds(1));

            // Fade-out animations
            var fadeOutVideo = new FadeTransition(Duration.seconds(1.5), mediaView);
            fadeOutVideo.setFromValue(1);
            fadeOutVideo.setToValue(0);

            var fadeOutImage = new FadeTransition(Duration.seconds(1.5), titleImage);
            fadeOutImage.setFromValue(1);
            fadeOutImage.setToValue(0);

            mediaPlayer.setOnReady(() -> {
                Duration totalDuration = media.getDuration();

                // Progress bar updates
                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    double progress = newTime.toMillis() / totalDuration.toMillis();
                    progressBar.setProgress(progress);

                    // Move Joe left to right based on progress
                    animatedJoe.setTranslateX((FXGL.getAppWidth() - barWidth) / 2 + progress * (barWidth - 64));
                });

                // Fade out near the end
                Duration fadeOutStart = totalDuration.subtract(Duration.seconds(5));
                FXGL.getGameTimer().runOnceAfter(() -> {
                    fadeOutVideo.play();
                    fadeOutImage.play();
                }, fadeOutStart);
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                root.getChildren().removeAll(mediaView, titleImage, progressBar, animatedJoe);
                finishIntro();
            });

            fadeInVideo.play();
            fadeInImage.play();
            mediaPlayer.play();
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