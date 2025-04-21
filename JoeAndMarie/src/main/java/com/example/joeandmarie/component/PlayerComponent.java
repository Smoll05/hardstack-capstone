package com.example.joeandmarie.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class PlayerComponent extends Component {
    private final AnimatedTexture texture;
    private final AnimationChannel idle, move, crouch;

    public PlayerComponent(AnimatedTexture texture, AnimationChannel idle, AnimationChannel move, AnimationChannel crouch) {
        this.texture = texture;
        this.idle = idle;
        this.move = move;
        this.crouch = crouch;
    }

    @Override
    public void onAdded() {
        // Only add the texture if it's not already added
        if (entity.getViewComponent().getChildren().isEmpty()) {
            entity.getViewComponent().addChild(texture);
        }
        texture.loopAnimationChannel(idle); // Start with idle animation
    }

    public void moveLeft() {
        if (texture.getAnimationChannel() != move) {
            texture.loopAnimationChannel(move); // Switch to move animation
        }

        texture.setScaleX(1);
    }

    public void moveRight() {
        if (texture.getAnimationChannel() != move) {
            texture.loopAnimationChannel(move); // Switch to move animation
        }

        texture.setScaleX(-1);
    }

    public void crouch() {
        if (texture.getAnimationChannel() != crouch) {
            texture.loopAnimationChannel(crouch); // Switch to crouch animation
        }
    }

    public void idle() {
        if (texture.getAnimationChannel() != idle) {
            texture.loopAnimationChannel(idle); // Switch to idle animation
        }
    }
}


//package com.example.joeandmarie;
//
//import com.almasb.fxgl.entity.component.Component;
//import com.almasb.fxgl.texture.AnimatedTexture;
//import com.almasb.fxgl.texture.AnimationChannel;
//import javafx.util.Duration;
//
//public class PlayerComponent extends Component {
//    private final AnimatedTexture texture;
//    private final AnimationChannel idle, move, crouch;
//
//    public PlayerComponent(AnimatedTexture texture) {
//        this.texture = texture;
//
//        var image = texture.getAnimationChannel().getImage();
//
//        // Row 1: Idle animation (Frames 0 to 7)
//        idle = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 0, 7);
//
//        // Row 2: Move animation (Frames 8 to 15)
//        move = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 8, 15);
//
//        // Row 3: Crouch animation (Frames 16 to 23)
//        crouch = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 16, 23);
//    }
//
//    @Override
//    public void onAdded() {
//        // Only add the texture if it's not already added
//        if (entity.getViewComponent().getChildren().isEmpty()) {
//            entity.getViewComponent().addChild(texture);
//        }
//        texture.loopAnimationChannel(idle);  // This can remain to set the animation
//    }
//
//
//    public void moveLeft() {
//        if (texture.getAnimationChannel() != move)
//            texture.loopAnimationChannel(move);
//    }
//
//    public void moveRight() {
//        if (texture.getAnimationChannel() != move)
//            texture.loopAnimationChannel(move);
//    }
//
//    public void crouch() {
//        if (texture.getAnimationChannel() != crouch)
//            texture.loopAnimationChannel(crouch);
//    }
//
//    public void idle() {
//        if (texture.getAnimationChannel() != idle)
//            texture.loopAnimationChannel(idle);
//    }
//}
