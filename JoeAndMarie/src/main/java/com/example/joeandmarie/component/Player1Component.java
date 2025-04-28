package com.example.joeandmarie.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.config.Constants;
import javafx.util.Duration;

import java.util.Map;

public class Player1Component extends PlayerComponent {

    Entity player2;

    public Player1Component() {
        super();
        // Create animations for idle, move, and crouch
        animIdle = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animMove = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animJump = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animCrouch = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 16, 23);
        animHang = new AnimationChannel(FXGL.image("joe_pulled_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animCry = new AnimationChannel(FXGL.image("joe_cry_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animFall = new AnimationChannel(FXGL.image("joe_falling_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animSwing = new AnimationChannel(FXGL.image("joe_pulling_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animPull = new AnimationChannel(FXGL.image("joe_pulling_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);

        stateData = Map.of(
                STAND, new Player1Component.StateData(animIdle, 0),
                WALK, new Player1Component.StateData(animMove, -Constants.RUNNING_SPEED),
                CROUCH, new Player1Component.StateData(animCrouch, 0),
                JUMP, new Player1Component.StateData(animJump, Constants.JUMP_FORCE),
                FALL, new Player1Component.StateData(animFall, 0),
                HANG, new Player1Component.StateData(animHang,0),
                SWING, new Player1Component.StateData(animSwing, -Constants.SWING_FORCE),
                PULL, new Player1Component.StateData(animPull, 0),
                CHECKPOINT, new Player1Component.StateData(animIdle, 0),
                SAVE, new Player1Component.StateData(animIdle, 0)
        );

        texture = new AnimatedTexture(animIdle);
        texture.loop();

        texture.setOnCycleFinished(() -> {
            if (texture.getAnimationChannel() == animJump) {
                state.changeState(FALL);
            }
        });
    }

    @Override
    public void onAdded() {
        super.onAdded();

        state.currentStateProperty().addListener((o, oldState, newState) -> {
            System.out.println("Player 1 new state: " + newState);
        });
    }

    public void loadPlayer2(Entity player2) {
        this.player2 = player2;

        otherState = player2.getComponent(StateComponent.class);
        otherPhysics = player2.getComponent(PhysicsComponent.class);
        otherView = player2.getComponent(ViewComponent.class);
    }

    boolean isHanging() {
        // Calculate the distance between players
        double player2Y = player2.getPosition().getY();
        double player1Y = getEntity().getPosition().getY();
        double distanceBetweenPlayers = Math.abs(player2Y - player1Y);

        // Get the rope length from the RopeJoint
        float ropeLength = Constants.PLAYER_ROPE_DISTANCE;

        // Check if the distance is near the rope length and both players are stationary
        boolean ropeIsFullyExtended = Math.abs(distanceBetweenPlayers - ropeLength) <= 20;  // Tolerance

        // System.out.println("Physics not on ground: " + !physics.isOnGround());
        // System.out.println("Other physics on ground: " + otherPhysics.isOnGround());

        // Return true if both conditions are met
        return ropeIsFullyExtended
                && !physics.isOnGround()
                && otherPhysics.isOnGround();
    }

//    @Override
//    public void onUpdate(double tpf) {
//        super.onUpdate(tpf);
//
//        if(state.isIn(SWING)) {
//            if(physics.isOnGround()) {
//                state.changeState(STAND);
//            } else {
//                return;
//            }
//        }
//
//        if (isHanging()) {
//            state.changeState(HANG);
//            applyDamping(physics, 0.988f);
//        } else {
//            // Handle other states like FALL, JUMP, etc.
//            if (physics.getVelocityY() > 0 && !physics.isOnGround()) {
//                state.changeState(FALL);
//            }
//        }
//    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        if (state.isIn(SWING)) {
            if (physics.isOnGround()) {
                state.changeState(STAND);
            } else {
                return;
            }
        }

        if (isHanging()) {
            if (!state.isIn(HANG)) {
                state.changeState(HANG);
            }
            applyDamping(physics, 0.988f);
            return;
        }

        if (physics.getVelocityY() > 0 && !physics.isOnGround()) {
            state.changeState(FALL);
        }
    }

}