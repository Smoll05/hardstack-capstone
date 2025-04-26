package com.example.joeandmarie.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.config.Constants;
import com.example.joeandmarie.entity.EntityType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Player2Component extends Component {

    private StateComponent state;
    private PhysicsComponent physics;
    private ViewComponent view;

    // Components of the other player
    private StateComponent otherState;
    private PhysicsComponent otherPhysics;
    private ViewComponent otherView;

    private final Entity player1;

    private final AnimatedTexture texture;
    private final AnimationChannel animIdle, animMove, animCrouch, animJump, animCry, animSplat, animHold, animPull, animPulled;

    private final EntityState STAND = new EntityState("STAND");
    private final EntityState WALK = new EntityState("WALK");
    private final EntityState CROUCH = new EntityState("CROUCH");
    private final EntityState HANG = new EntityState("HANG");
    private final EntityState SWING = new EntityState("SWING");
    private final EntityState PULL = new EntityState("PULL");
    private final EntityState PULLED = new EntityState("PULLED");
    private final EntityState CHECKPOINT = new EntityState("CHECKPOINT");
    private final EntityState SAVE = new EntityState("SAVE");
    private final EntityState HOLD = new EntityState("HOLD");
    private final EntityState SPLAT = new EntityState("SPLAT");

    public static boolean isTouchingWall = false;

    private final EntityState JUMP = new EntityState("JUMP") {
        @Override
        protected void onUpdate(double tpf) {
            if(physics.getVelocityY() > 0) {
                state.changeState(FALL);
            }
        }
    };

    private final EntityState FALL = new EntityState("FALL") {
        @Override
        protected void onUpdate(double tpf) {
            super.onUpdate(tpf);
            if (physics.isOnGround()) {
                physics.setVelocityX(0);
                state.changeState(STAND);
            }
        }
    };

    private static class StateData {
        private final AnimationChannel channel;
        private final int moveSpeed;

        public StateData(AnimationChannel channel, int moveSpeed) {
            this.channel = channel;
            this.moveSpeed = moveSpeed;
        }

        public int getMoveSpeed() {
            return moveSpeed;
        }

    }

    private Map<EntityState, StateData> stateData = new HashMap<>();

    public Player2Component() {
        // Create animations for idle, move, and crouch

        player1 = getPlayer1();

        animIdle = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animMove = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animJump = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animCrouch = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 16, 23);
        animCry = new AnimationChannel(FXGL.image("marie_cry_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animSplat = new AnimationChannel(FXGL.image("marie_hapla_spritesheet.png"), 16, 64, 64, Duration.seconds(1), 3, 15);
        animHold = new AnimationChannel(FXGL.image("marie_holding_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
        animPull = new AnimationChannel(FXGL.image("marie_pulling_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
        animPulled = new AnimationChannel(FXGL.image("marie_pulled_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);

        stateData.put(STAND, new StateData(animIdle, 0));
        stateData.put(WALK, new StateData(animMove,  -Constants.RUNNING_SPEED));
        stateData.put(CROUCH, new StateData(animCrouch, 0));
        stateData.put(JUMP, new StateData(animJump,  Constants.JUMP_FORCE));
        stateData.put(FALL, new StateData(animIdle, 0));
        stateData.put(HANG, new StateData(animIdle, 0));
        stateData.put(SWING, new StateData(animIdle, 0));
        stateData.put(PULL, new StateData(animPull, 0));
        stateData.put(PULLED, new StateData(animPulled, 0));
        stateData.put(CHECKPOINT, new StateData(animCry, 0));
        stateData.put(SAVE, new StateData(animIdle, 0));
        stateData.put(SPLAT, new StateData(animSplat, 0));
        stateData.put(HOLD, new StateData(animHold, -Constants.RUNNING_SPEED));

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

        state = entity.getComponent(StateComponent.class);
        physics = entity.getComponent(PhysicsComponent.class);
        view = entity.getComponent(ViewComponent.class);

        otherState = player1.getComponent(StateComponent.class);
        otherPhysics = player1.getComponent(PhysicsComponent.class);
        otherView = player1.getComponent(ViewComponent.class);

        view.addChild(texture);

        state.changeState(STAND);

        state.currentStateProperty().addListener((o, oldState, newState) -> {
            System.out.println("new state: " + newState);

            var data = stateData.get(newState);

            texture.loopAnimationChannel(data.channel);
        });
    }

    public void pull() {
        if (!physics.isOnGround()) {
            return;
        }
        state.changeState(PULL);
    }

    public void pulled() {
        state.changeState(PULLED);
    }

    public void hold() {
        state.changeState(HOLD);

        if(entity.getScaleX() == 1) {
            physics.setVelocityX(stateData.get(HOLD).moveSpeed);
        } else {
            physics.setVelocityX(-1 * stateData.get(HOLD).moveSpeed);
        }
    }

    public void moveLeft() {
        tryMovingState(WALK, 1);
    }

    public void moveRight() {
        tryMovingState(WALK, -1);
    }

    public void cry() {
        if (!physics.isOnGround()) {
            return;
        }

        state.changeState(CHECKPOINT);

        FXGL.runOnce(() -> {
            if (CheckpointComponent.getFlagEntity() != null) {
                Point2D pos = new Point2D(CheckpointComponent.getFlagEntity().getPosition().getX()  + 20, CheckpointComponent.getFlagEntity().getPosition().getY() + 23);
                physics.overwritePosition(pos);
            } else {
                System.out.println("No flag entity to teleport to!");
            }
        }, Duration.seconds(1));
    }

    public void stop() {
        if (state.isIn(WALK)) {
            physics.setVelocityX(0);
            state.changeState(STAND);
        }
    }

    public void stand() {
        state.changeState(STAND);
    }

    public void jump() {
        if (!physics.isOnGround()) {
            return;
        }

        physics.setVelocityY(-Constants.JUMP_FORCE);
        state.changeState(JUMP);
    }

    public void crouch() {
        if (physics.isOnGround() && state.isIn(STAND, WALK)) {
            physics.setVelocityX(0);
            state.changeState(CROUCH);
        }
    }

    private void tryMovingState(EntityState newState, int scale) {
        if (state.isIn(STAND, WALK, JUMP, FALL, SWING)) {
            getEntity().setScaleX(scale * FXGLMath.abs(getEntity().getScaleX()));

            physics.setVelocityX(scale * stateData.get(newState).moveSpeed);

            if (state.getCurrentState() != newState) {
                state.changeState(newState);
            }
        }
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
        if(physics.getVelocityY() > 0) {
            state.changeState(FALL);
        }
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }

    private Entity getPlayer1() {
        return FXGL.getGameWorld().getSingleton(EntityType.PLAYER1);
    }
}


//            var speed = scale * stateData.get(newState).moveSpeed;
//            float angle = physics.getBody().getAngle();
//
//            float forceX = speed * (float) Math.cos(angle); // X component of the force
//            float forceY = speed * (float) Math.sin(angle); // Y component of the force
//
//            // Apply the force to the entity's center
//            physics.applyForceToCenter(new Point2D(speed, 0));

// physics.getBody().setLinearDamping(0.5f);