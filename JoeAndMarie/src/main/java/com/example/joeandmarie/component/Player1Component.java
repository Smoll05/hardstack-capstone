package com.example.joeandmarie.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.joints.DistanceJoint;
import com.almasb.fxgl.physics.box2d.dynamics.joints.DistanceJointDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.MainApplication;
import com.example.joeandmarie.config.Constants;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class Player1Component extends PlayerComponent {

    Entity player2;
    public static CheckpointComponent flag;

    public Player1Component() {
        super();
        animIdle = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animMove = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animJump = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animCrouch = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 16, 23);
        animHang = new AnimationChannel(FXGL.image("joe_pulled_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animCry = new AnimationChannel(FXGL.image("joe_cry_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animFall = new AnimationChannel(FXGL.image("joe_falling_spritesheet.png"), 8, 64, 64, Duration.seconds(1.5), 0, 7);
        animSwing = new AnimationChannel(FXGL.image("joe_pulling_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animPull = new AnimationChannel(FXGL.image("joe_pulling_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animPulled = new AnimationChannel(FXGL.image("joe_pulled_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
        animPlant = new AnimationChannel(FXGL.image("joe_plant_spritesheet.png"), 20, 64, 64, Duration.seconds(2), 0, 18);
        animSplat = new AnimationChannel(FXGL.image("joe_hapla_spritesheet.png"), 16, 64, 64, Duration.seconds(1), 3, 15);
        animHold = new AnimationChannel(FXGL.image("joe_holding_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);

        stateData.put(STAND, new StateData(animIdle, 0));
        stateData.put(WALK, new StateData(animMove,  -Constants.RUNNING_SPEED));
        stateData.put(CROUCH, new StateData(animCrouch, 0));
        stateData.put(JUMP, new StateData(animJump,  Constants.JUMP_FORCE));
        stateData.put(FALL, new StateData(animFall, 0));
        stateData.put(HANG, new StateData(animHang, 0));
        stateData.put(SWING, new StateData(animMove, -Constants.SWING_FORCE));
        stateData.put(PULL, new StateData(animPull, 0));
        stateData.put(PULLED, new StateData(animPulled, 0));
        stateData.put(CHECKPOINT, new StateData(animCry, 0));
        stateData.put(SAVE, new StateData(animPlant, 0));
        stateData.put(SPLAT, new StateData(animSplat, 0));
        stateData.put(HOLD, new StateData(animHold, -Constants.HOLD_FORCE));

        texture = new AnimatedTexture(animIdle);
        texture.loop();

//        texture.setOnCycleFinished(() -> {
//            if (texture.getAnimationChannel() == animJump) {
//                state.changeState(FALL);
//            }
//        }); // Commented out to avoid constant falling state
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

    public void plant() {
        if (!physics.isOnGround()) {
            return;
        }
        state.changeState(SAVE);

        flag = new CheckpointComponent(entity.getPosition());

        FXGL.runOnce(() -> {
            flag.plantFlag();
        }, Duration.seconds(.5));
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

        if (physics.getVelocityY() > 750 && !physics.isOnGround()) { // changed min velocity for falling detection
            state.changeState(FALL);
        }
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}