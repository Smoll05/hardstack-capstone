package com.example.joeandmarie.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.config.Constants;
import javafx.geometry.Point2D;

import java.util.Map;
import java.util.TimerTask;

public abstract class PlayerComponent extends Component {

    StateComponent state;
    PhysicsComponent physics;
    ViewComponent view;

    StateComponent otherState;
    PhysicsComponent otherPhysics;
    ViewComponent otherView;

    AnimatedTexture texture;
    AnimationChannel animIdle,
            animMove, animCrouch, animJump,
            animHang, animCry, animFall,
            animSwing, animPull;

    private TimerTask hangTransitionTask;


    final EntityState STAND = new EntityState("STAND");
    final EntityState WALK = new EntityState("WALK");
    final EntityState HANG = new EntityState("HANG");
    final EntityState PULL = new EntityState("PULL");
    final EntityState CHECKPOINT = new EntityState("CHECKPOINT");
    final EntityState SAVE = new EntityState("SAVE");
    final EntityState HOLD = new EntityState("HOLD");
    final EntityState CROUCH = new EntityState("CROUCH");
    final EntityState SWING = new EntityState("SWING");

    final EntityState JUMP = new EntityState("JUMP") {
        @Override
        protected void onUpdate(double tpf) {
            if(physics.getVelocityY() > 0) {
                state.changeState(FALL);
            }
        }
    };

    final EntityState FALL = new EntityState("FALL") {
        @Override
        protected void onUpdate(double tpf) {
            super.onUpdate(tpf);
            if (physics.isOnGround()) {
                physics.setVelocityX(0);
                state.changeState(STAND);
            }
        }
    };

    record StateData(AnimationChannel channel, int moveSpeed) { }

    Map<EntityState, Player1Component.StateData> stateData;

    @Override
    public void onAdded() {

        state = getEntity().getComponent(StateComponent.class);
        physics = getEntity().getComponent(PhysicsComponent.class);
        view = getEntity().getComponent(ViewComponent.class);

        view.addChild(texture);

        state.changeState(STAND);

        state.currentStateProperty().addListener((_, _, newState) -> {
            var data = stateData.get(newState);
            texture.loopAnimationChannel(data.channel);
        });

    }

    public void moveLeft() {
        tryMovingState(1);
    }

    public void moveRight() {
        tryMovingState(-1);
    }

    public void cry() {
        state.changeState(CHECKPOINT);
    }

    public void stop() {
        if (state.isIn(WALK)) {
            physics.setVelocityX(0);
            state.changeState(STAND);
        } else if(state.isIn(SWING)) {
            state.changeState(HANG);
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

//            if(physics.getBody().getType() != BodyType.STATIC) {
//                physics.getBody().setType(BodyType.STATIC);
//            }
        }
    }

//    if (physics.isOnGround() && state.isIn(STAND, WALK)) {
//        physics.setVelocityX(0);
//        state.changeState(CROUCH);
//        System.out.println("Crouching");
//
//        FXGL.runOnce(() -> {
//            physics.getBody().setType(BodyType.KINEMATIC);
//        }, Duration.seconds(0.1));
//    }
//
//        if(physics.isOnGround() && state.isIn(STAND, WALK)) {
//        physics.setVelocityX(0);
//        state.changeState(CROUCH);
//        isImmobile = true;
//        lockedPos = entity.getPosition();
//    }

    void tryMovingState(int scale) {
        if (state.isIn(STAND, WALK, JUMP, FALL)) {
            linearMovement(WALK, scale);
        } else if(state.isIn(HANG, SWING)) {
            swingMovement(SWING, scale);
        }
    }

    void linearMovement(EntityState newState, int scale) {
        getEntity().setScaleX(scale * FXGLMath.abs(getEntity().getScaleX()));

        physics.setVelocityX(scale * stateData.get(newState).moveSpeed);

        if (state.getCurrentState() != newState) {
            state.changeState(newState);
        }
    }

    void swingMovement(EntityState newState, int scale) {
        getEntity().setScaleX(scale * FXGLMath.abs(getEntity().getScaleX()));

        int speed = scale * stateData.get(newState).moveSpeed;
        physics.applyForceToCenter(new Point2D(speed, 0));

        if (state.getCurrentState() != newState) {
            state.changeState(newState);
        }
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }

    void applyDamping(PhysicsComponent physics, float factor) {
        Vec2 vel = physics.getBody().getLinearVelocity();
        Vec2 damped = vel.mul(factor);
        physics.getBody().setLinearVelocity(damped);
    }
}

//        float angVel = physics.getBody().getAngularVelocity();
//        physics.getBody().setAngularVelocity(angVel * factor); // reduce spin

//            var speed = scale * stateData.get(newState).moveSpeed;
//            float angle = physics.getBody().getAngle();
//
//            float forceX = speed * (float) Math.cos(angle); // X component of the force
//            float forceY = speed * (float) Math.sin(angle); // Y component of the force
//
//            // Apply the force to the entity's center
//            physics.applyForceToCenter(new Point2D(speed, 0));

// physics.getBody().setLinearDamping(0.5f);
