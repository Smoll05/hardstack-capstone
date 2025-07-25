package com.example.joeandmarie.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.config.Constants;
import com.example.joeandmarie.data.model.SettingPreference;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.Map;

public class Player2Component extends PlayerComponent {

    Entity player1;

    public Player2Component() {
        super();

        animIdle = new AnimationChannel(FXGL.image("marie_idle_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animMove = new AnimationChannel(FXGL.image("marie_jumping_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(0.5), 0, 7);
        animJump = new AnimationChannel(FXGL.image("marie_jumping_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(0.5), 0, 7);
        animCrouch = new AnimationChannel(FXGL.image("marie_crouch_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animHang = new AnimationChannel(FXGL.image("marie_pulled_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animCry = new AnimationChannel(FXGL.image("marie_cry_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animFall = new AnimationChannel(FXGL.image("marie_falling_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(1.5), 0, 7);
        animSwing = new AnimationChannel(FXGL.image("marie_pulling_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animPull = new AnimationChannel(FXGL.image("marie_pulling_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animSplat = new AnimationChannel(FXGL.image("marie_hapla_harness_spritesheet.png"), 16, 64, 64, Duration.seconds(1), 3, 15);
        animHold = new AnimationChannel(FXGL.image("marie_holding_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
        animPull = new AnimationChannel(FXGL.image("marie_pulling_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
        animPulled = new AnimationChannel(FXGL.image("marie_pulled_harness_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);

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
    }

    @Override
    public void onAdded() {
        super.onAdded();

        state.currentStateProperty().addListener((o, oldState, newState) -> {
        });
    }


    @Override
    public void swingMovement(EntityState newState, int scale) {
        getEntity().setScaleX(scale * FXGLMath.abs(getEntity().getScaleX()));

        Point2D pivot = player1.getCenter();
        Point2D center = getEntity().getCenter();

        Point2D toEntity = center.subtract(pivot);

        Point2D tangent = new Point2D(-toEntity.getY(), toEntity.getX()).normalize().multiply(scale);

        double swingSpeed = stateData.get(newState).moveSpeed * 5;

        Point2D oppositeTangent = tangent.multiply(-1);

        Point2D oppositeForce = oppositeTangent.multiply(swingSpeed);
        physics.applyForceToCenter(oppositeForce);

        Point2D prevVelocity = physics.getLinearVelocity();
        FXGL.getGameTimer().runOnceAfter(() -> {
            Point2D currentVelocity = physics.getLinearVelocity();
            if (currentVelocity.magnitude() < prevVelocity.magnitude()) {
                physics.applyForceToCenter(currentVelocity.normalize().multiply(swingSpeed * 0.75));
            }
        }, Duration.millis(100));

        // Update entity state
        if (state.getCurrentState() != newState) {
            state.changeState(newState);
        }
    }

    public void loadPlayer1(Entity player1) {
        this.player1 = player1;

        otherState = player1.getComponent(StateComponent.class);
        otherPhysics = player1.getComponent(PhysicsComponent.class);
        otherView = player1.getComponent(ViewComponent.class);
    }

    boolean isHanging() {
        // Calculate the distance between players
        double player2Y = player1.getPosition().getY();
        double player1Y = getEntity().getPosition().getY();
        double distanceBetweenPlayers = Math.abs(player2Y - player1Y);

        // Get the rope length from the RopeJoint
        float ropeLength = Constants.PLAYER_ROPE_DISTANCE;

        // Check if the distance is near the rope length and both players are stationary
        boolean ropeIsFullyExtended = Math.abs(distanceBetweenPlayers - ropeLength) <= 20;  // Tolerance

        // Return true if both conditions are met
        return ropeIsFullyExtended
                && !physics.isOnGround()
                && (otherPhysics.isOnGround() ||
                otherState.getCurrentState().toString().equals("HOLD")
        );
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        if(state.isIn(SWING)) {
            if(physics.isOnGround()) {
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
        } else {
            // Handle other states like FALL, JUMP, etc.
            if (physics.getVelocityY() > 750 && !physics.isOnGround()) {
                state.changeState(FALL);
            }
        }
    }
}