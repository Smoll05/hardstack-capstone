package com.example.joeandmarie.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.config.Constants;
import com.example.joeandmarie.state.PlayerState;
import javafx.util.Duration;

import java.util.Map;

public class PlayerComponent extends Component {

    private StateComponent state;
    private PhysicsComponent physics;
    private ViewComponent view;

    private final AnimatedTexture texture;
    private final AnimationChannel animIdle, animMove, animCrouch, animJump;

    private final EntityState STAND = new EntityState("STAND");
    private final EntityState WALK = new EntityState("WALK");
    private final EntityState CROUCH = new EntityState("CROUCH");
    private final EntityState HANG = new EntityState("HANG");
    private final EntityState SWING = new EntityState("SWING");
    private final EntityState PULL = new EntityState("PULL");
    private final EntityState CHECKPOINT = new EntityState("CHECKPOINT");
    private final EntityState SAVE = new EntityState("SAVE");

    private final EntityState JUMP = new EntityState("JUMP") {
        @Override
        protected void onUpdate(double tpf) {
            super.onUpdate(tpf);
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
    }

    private final Map<EntityState, StateData> stateData;

    public PlayerComponent() {
        // Create animations for idle, move, and crouch
        animIdle = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        animMove = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animJump = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        animCrouch = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 16, 23);


        stateData = Map.of(
                STAND, new StateData(animIdle, 0),
                WALK, new StateData(animMove, -Constants.RUNNING_SPEED),
                CROUCH, new StateData(animCrouch, 0),
                JUMP, new StateData(animJump, Constants.JUMP_FORCE),
                FALL, new StateData(animJump, 0),
                HANG, new StateData(animIdle,0),
                SWING, new StateData(animIdle, 0),
                PULL, new StateData(animIdle, 0),
                CHECKPOINT, new StateData(animIdle, 0),
                SAVE, new StateData(animIdle, 0)
        );

        texture = new AnimatedTexture(animIdle);
        texture.loop();

        texture.setOnCycleFinished(() -> {
            if (texture.getAnimationChannel() == animJump) {
                state.changeState(FALL);
            }
        });
    }

//    @Override
//    public void onUpdate(double tpf) {
//        System.out.println("VY: " + physics.getVelocityY());
//
//        if (!physics.isOnGround()
//                && !state.isIn(JUMP, FALL, SWING, HANG)) {
//            state.changeState(FALL);
//        } else {
//            System.out.println("On Ground");
//        }
//    }

    @Override
    public void onAdded() {

        state = entity.getComponent(StateComponent.class);
        physics = entity.getComponent(PhysicsComponent.class);
        view = entity.getComponent(ViewComponent.class);

        view.addChild(texture);

        state.changeState(STAND);

        // Only add the texture if it's not already added
//        if (entity.getViewComponent().getChildren().isEmpty()) {
//            entity.getViewComponent().addChild(texture);
//        }

        state.currentStateProperty().addListener((o, oldState, newState) -> {
            System.out.println("new state: " + newState);

            var data = stateData.get(newState);

            texture.loopAnimationChannel(data.channel);
        });
    }

    public void moveLeft() {
        tryMovingState(WALK, 1);
    }

    public void moveRight() {
        tryMovingState(WALK, -1);
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
        System.out.println("VelocityX: " + physics.getVelocityX());
        System.out.println("tpf: " + tpf);
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}