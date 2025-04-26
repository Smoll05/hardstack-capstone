package com.example.joeandmarie.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.MainApplication;
import com.example.joeandmarie.config.Constants;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class Player1Component extends Component {

    private StateComponent state;
    private PhysicsComponent physics;
    private ViewComponent view;

    private final AnimatedTexture texture;
    private final AnimationChannel animIdle, animMove, animCrouch, animJump, animCry, animFall, animPlant, animSplat, animHold, animPull, animPulled;

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

    public static CheckpointComponent flag;
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
                state.changeState(SPLAT);
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

    public Player1Component() {
        // Create animations for idle, move, and crouch
        animIdle = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animMove = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animJump = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animCrouch = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 16, 23);
        animCry = new AnimationChannel(FXGL.image("joe_cry_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animFall = new AnimationChannel(FXGL.image("joe_falling_spritesheet.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animPlant = new AnimationChannel(FXGL.image("joe_plant_spritesheet.png"), 20, 64, 64, Duration.seconds(2), 0, 18);
        animSplat = new AnimationChannel(FXGL.image("joe_hapla_spritesheet.png"), 16, 64, 64, Duration.seconds(1), 3, 15);
        animHold = new AnimationChannel(FXGL.image("joe_holding_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
        animPull = new AnimationChannel(FXGL.image("joe_pulling_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);
        animPulled = new AnimationChannel(FXGL.image("joe_pulled_spritesheet.png"), 8, 64, 64, Duration.seconds(1), 0, 7);

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
        stateData.put(SAVE, new StateData(animPlant, 0));
        stateData.put(SPLAT, new StateData(animSplat, 0));
        stateData.put(HOLD, new StateData(animHold, -Constants.RUNNING_SPEED));

//        stateData = Map.of(
//                STAND, new StateData(animIdle, 0),
//                WALK, new StateData(animMove, -Constants.RUNNING_SPEED),
//                CROUCH, new StateData(animCrouch, 0),
//                JUMP, new StateData(animJump, Constants.JUMP_FORCE),
//                FALL, new StateData(animFall, 0),
//                HANG, new StateData(animIdle,0),
//                SWING, new StateData(animIdle, 0),
//                PULL, new StateData(animIdle, 0),
//                CHECKPOINT, new StateData(animCry, 0),
//                SAVE, new StateData(animPlant, 0),
//                SPLAT, new StateData(animSplat, 0)
//        );

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

        view.addChild(texture);

        state.changeState(STAND);
        state.currentStateProperty().addListener((o, oldState, newState) -> {
//            System.out.println("new state: " + newState);

            var data = stateData.get(newState);

            if(data.channel == animPlant) {
                texture.playAnimationChannel(data.channel);
            } else {
                texture.loopAnimationChannel(data.channel);
            }
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

    public void cry() {
        if (!physics.isOnGround()) {
            return;
        }

        state.changeState(CHECKPOINT);


        FXGL.runOnce(() -> {
            if (CheckpointComponent.getFlagEntity() != null) {
                Point2D pos = new Point2D(CheckpointComponent.getFlagEntity().getPosition().getX()  - 20, CheckpointComponent.getFlagEntity().getPosition().getY() + 23);
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

    public void stopMidair() {
        if (state.isIn(SWING)) {
            physics.setVelocityX(0);
            state.changeState(HANG);
        }
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
        if (state.isIn(STAND, WALK, JUMP, FALL, SWING, SPLAT)) {
            getEntity().setScaleX(scale * FXGLMath.abs(getEntity().getScaleX()));

            physics.setVelocityX(scale * stateData.get(newState).moveSpeed);
//            physics.setFixtureDef(new FixtureDef().friction(5.0f));

            if (state.getCurrentState() != newState) {
                state.changeState(newState);
            }
        }
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}