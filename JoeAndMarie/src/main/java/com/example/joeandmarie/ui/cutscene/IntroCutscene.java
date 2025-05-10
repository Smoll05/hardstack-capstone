package com.example.joeandmarie.ui.cutscene;

import com.almasb.fxgl.app.scene.FXGLScene;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import com.almasb.fxgl.dsl.FXGL;

import java.util.List;
public class IntroCutscene extends FXGLScene {

    private int currentIndex = 0;
    private List<Image> slides;
    private ImageView imageView;
    private com.almasb.fxgl.time.TimerAction slideshowTask;

    public IntroCutscene() {
        super();

        // Load your images for the cutscene
        slides = List.of(
                FXGL.image("slide1.png"),
                FXGL.image("slide2.png"),
                FXGL.image("slide3.png")
        );

        // Set up the ImageView to display the slides
        imageView = new ImageView(slides.get(0));
        imageView.setFitWidth(FXGL.getAppWidth());
        imageView.setFitHeight(FXGL.getAppHeight());

        getContentRoot().getChildren().add(imageView);

        // Start the slideshow
        playSlideshow();

        // Set up key press to skip the cutscene
        FXGL.getInput().addAction(new UserAction("Skip Cutscene") {
            @Override
            protected void onActionBegin() {
                endCutscene();
            }
        }, javafx.scene.input.KeyCode.SPACE);
    }

    private void playSlideshow() {
        slideshowTask = FXGL.getGameTimer().runAtInterval(() -> {
            currentIndex++;
            if (currentIndex < slides.size()) {
                imageView.setImage(slides.get(currentIndex));
            } else {
                endCutscene();
            }
        }, Duration.seconds(2));  // Change slide every 2 seconds
    }

    private void endCutscene() {
        if (slideshowTask != null) {
            slideshowTask.expire();  // Stop the slideshow timer
        }

        // Close the cutscene by removing the current scene
        FXGL.getSceneService().popSubScene();  // Close the cutscene and return to previous scene

        // Optional: Proceed to the next action after the cutscene ends (e.g., starting the game)
        // FXGL.getGameController().startNewGame();
    }
}
