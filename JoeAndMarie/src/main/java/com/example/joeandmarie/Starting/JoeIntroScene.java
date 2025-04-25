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
import javafx.animation.FadeTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
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
public class JoeIntroScene extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setIntroEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public IntroScene newIntro() {
                return new MyIntroScene();
            }
        });
    }

    public static class MyIntroScene extends IntroScene {

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

            root.getChildren().addAll(mediaView, titleImage);

            // Play fade-in animation for video
            var fadeInVideo = new FadeTransition(Duration.seconds(0.5), mediaView);
            fadeInVideo.setFromValue(0);
            fadeInVideo.setToValue(1);

            // Show image with fade-in after 1 sec
            var fadeInImage = new FadeTransition(Duration.seconds(2), titleImage);
            fadeInImage.setFromValue(0);
            fadeInImage.setToValue(1);
            fadeInImage.setDelay(Duration.seconds(1));

            // Fade-out both video and image before end
            var fadeOutVideo = new FadeTransition(Duration.seconds(1.5), mediaView);
            fadeOutVideo.setFromValue(1);
            fadeOutVideo.setToValue(0);

            var fadeOutImage = new FadeTransition(Duration.seconds(1.5), titleImage);
            fadeOutImage.setFromValue(1);
            fadeOutImage.setToValue(0);

            mediaPlayer.setOnReady(() -> {
                Duration totalDuration = media.getDuration();
                Duration fadeOutStart = totalDuration.subtract(Duration.seconds(5));

                FXGL.getGameTimer().runOnceAfter(() -> {
                    fadeOutVideo.play();
                    fadeOutImage.play();
                }, fadeOutStart);
            });

            // After video ends, remove and show intro content
            mediaPlayer.setOnEndOfMedia(() -> {
                root.getChildren().removeAll(mediaView, titleImage);
                finishIntro(); // End the intro scene immediately
            });



            fadeInVideo.play();
            fadeInImage.play();
            mediaPlayer.play();
        }


//        private void showIntroContent(Pane root) {
//
//            var welcomeText = new Text("Welcome to the Adventure of Joe and Marie");
//            welcomeText.setFont(Font.font("Arial", 40));
//            welcomeText.setFill(Color.WHITE);
//            welcomeText.setTranslateX(250);
//            welcomeText.setTranslateY(400);
//
//            var textAnim = FXGL.animationBuilder()
//                    .duration(Duration.seconds(2))
//                    .delay(Duration.seconds(1.66 * index++))
//                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
//                    .fade(welcomeText)
//                    .from(0)
//                    .to(1)
//                    .build();
//
//            var scaleTextAnim = FXGL.animationBuilder()
//                    .duration(Duration.seconds(2))
//                    .delay(Duration.seconds(1.66 * index++))
//                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
//                    .scale(welcomeText)
//                    .from(new Point2D(0, 0))
//                    .to(new Point2D(1, 1))
//                    .build();
//
//            animations.add(textAnim);
//            animations.add(scaleTextAnim);
//
//            animations.get(animations.size() - 1).setOnFinished(this::finishIntro);
//
//            root.getChildren().addAll(
//                    new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight()),
//                    welcomeText
//            );
//
//            startIntro();
//        }

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