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
import com.almasb.fxgl.physics.box2d.dynamics.joints.DistanceJoint;
import com.almasb.fxgl.physics.box2d.dynamics.joints.DistanceJointDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.MainApplication;
import com.example.joeandmarie.config.Constants;
import com.sun.tools.javac.Main;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

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
            animSwing, animPull, animPulled, animPlant,
            animSplat, animHold;

    public static boolean isTouchingWall = false;

    final EntityState STAND = new EntityState("STAND");
    final EntityState WALK = new EntityState("WALK");
    final EntityState PULL = new EntityState("PULL");
    final EntityState PULLED = new EntityState("PULLED");
    final EntityState CHECKPOINT = new EntityState("CHECKPOINT");
    final EntityState SAVE = new EntityState("SAVE");
    final EntityState HOLD = new EntityState("HOLD");
    final EntityState CROUCH = new EntityState("CROUCH") {
        @Override
        protected void onUpdate(double tpf) {
            physics.setVelocityX(0);
            physics.setVelocityY(1000); // to be improved, when other character is hanging, crouching would cause it to spring up
        }
    };
    final EntityState SWING = new EntityState("SWING");
    final EntityState SPLAT = new EntityState("SPLAT");
    final EntityState HANG = new EntityState("HANG") {
        @Override
        protected void onUpdate(double tpf) {
            if(physics.isOnGround()) {
                state.changeState(STAND);
            }
        }
    };

    final EntityState JUMP = new EntityState("JUMP") {
        @Override
        protected void onUpdate(double tpf) {
            if(physics.getVelocityY() > 750) {
                state.changeState(FALL);
            }
            if (physics.getVelocityY() == 0) {
                state.changeState(STAND);
            }
        }
    };

    final EntityState FALL = new EntityState("FALL") {
        @Override
        protected void onUpdate(double tpf) {
            super.onUpdate(tpf);
            if (physics.isOnGround()) {
                physics.setVelocityX(0);
                state.changeState(SPLAT);
            }
        }
    };

    record StateData(AnimationChannel channel, int moveSpeed) { }

    Map<EntityState, Player1Component.StateData> stateData = new HashMap<>();

    public StateComponent getState() {
        return state;
    }

    public EntityState getSWING() {
        return SWING;
    }

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

    public void stop() {
        if (state.isIn(WALK)) {
            physics.setVelocityX(0);
            state.changeState(STAND);
        } else if(state.isIn(SWING)) {
//            state.changeState(HANG); // Commented out as to not modify VelocityX on release from swing
        }
    }

    public void stand() {
        state.changeState(STAND);
        setFriction(5);
    }

    public void jump() {
        if (!physics.isOnGround()) {
            return;
        }

        setFriction(0);
        physics.setVelocityY(-Constants.JUMP_FORCE);
        state.changeState(JUMP);
    }

    public boolean pull() {
        if (!physics.isOnGround()) {
            return false;
        }

        state.changeState(PULL);
        return true;
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

    public void cry() {
        if (!physics.isOnGround()) {
            return;
        }

        state.changeState(CHECKPOINT);

        FXGL.runOnce(() -> {
            if (CheckpointComponent.getFlagEntity() != null) {
                double x = CheckpointComponent.getFlagEntity().getPosition().getX();
                double y = CheckpointComponent.getFlagEntity().getPosition().getY() + 23;

                x += this instanceof Player1Component ? -15 : 15;

                Point2D pos = new Point2D(x, y);
                physics.overwritePosition(pos);
            } else {
                System.out.println("No flag entity to teleport to!");
            }
        }, Duration.seconds(1));

    }

    void tryMovingState(int scale) {
        if (state.isIn(STAND, WALK, JUMP, FALL, SPLAT)) {
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
        Point2D previousVelocity = physics.getLinearVelocity();

        Point2D tangentialForce;
        if (scale == -1) { // Counter-clockwise
            tangentialForce = new Point2D(entity.getY(), entity.getX()).normalize();
        } else { // Clockwise
            tangentialForce = new Point2D(entity.getY(), -entity.getX()).normalize();
        }

        int swingSpeed = scale * stateData.get(newState).moveSpeed * 5;
        tangentialForce = tangentialForce.multiply(swingSpeed);
        physics.applyForceToCenter(tangentialForce);

        Point2D currentVelocity = physics.getLinearVelocity();

        if (currentVelocity.magnitude() < previousVelocity.magnitude()) {
            // Apply a small force in the direction of the current velocity to boost it.
            physics.applyForceToCenter(currentVelocity.normalize().multiply(swingSpeed * 0.75f)); // Adjust 0.5f as needed
        }

//        int speed = scale * stateData.get(newState).moveSpeed;
//        physics.applyForceToCenter(new Point2D(speed*2, 0));

        if (state.getCurrentState() != newState) {
            state.changeState(newState);
        }
    }

    public boolean playerOnGround() {
        return physics.isOnGround();
    }

    public boolean isHoldingWall() {
        return state.isIn(HOLD);
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

    private void setFriction(float friction) {
        physics.setOnPhysicsInitialized(() -> {
            physics.getBody().getFixtures().getFirst().setFriction(friction);
        });
    }

}
