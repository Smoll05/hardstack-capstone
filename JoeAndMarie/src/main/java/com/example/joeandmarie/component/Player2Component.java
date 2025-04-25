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
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.config.Constants;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.Map;

public class Player2Component extends Component {

    private StateComponent state;
    private PhysicsComponent physics;
    private ViewComponent view;

    // Components of the other player
    private StateComponent otherState;
    private PhysicsComponent otherPhysics;
    private ViewComponent otherView;

    private Entity player1;

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

    private record StateData(AnimationChannel channel, int moveSpeed) { }

    private final Map<EntityState, StateData> stateData;

    public Player2Component() {
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

    @Override
    public void onAdded() {
        state = entity.getComponent(StateComponent.class);
        physics = entity.getComponent(PhysicsComponent.class);
        view = entity.getComponent(ViewComponent.class);

        view.addChild(texture);
        state.changeState(STAND);

        state.currentStateProperty().addListener((o, oldState, newState) -> {
            System.out.println("Player 2 new state: " + newState);

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

    public void swingLeft() {
        tryMovingState(SWING, 1);
    }

    public void swingRight() {
        tryMovingState(SWING, -1);
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
        if (state.isIn(STAND, WALK, JUMP, FALL)) {
            linearMovement(newState, scale);
        } else if(state.isIn(HANG, SWING)) {
            swingMovement(newState, scale);
        }
    }

    private void linearMovement(EntityState newState, int scale) {
        getEntity().setScaleX(scale * FXGLMath.abs(getEntity().getScaleX()));

        physics.setVelocityX(scale * stateData.get(newState).moveSpeed);

        if (state.getCurrentState() != newState) {
            state.changeState(newState);
        }
    }

    private void swingMovement(EntityState newState, int scale) {
        getEntity().setScaleX(scale * FXGLMath.abs(getEntity().getScaleX()));

        int speed = scale * stateData.get(newState).moveSpeed;
        physics.applyForceToCenter(new Point2D(speed, 0));

        if (state.getCurrentState() != newState) {
            state.changeState(newState);
        }
    }

    private boolean isHanging() {
        // Calculate the distance between players
        double player1Y = player1.getPosition().getY();
        double player2Y = entity.getPosition().getY();
        double distanceBetweenPlayers = Math.abs(player1Y - player2Y);

        // Get the rope length from the RopeJoint
        float ropeLength = Constants.PLAYER_ROPE_DISTANCE;

        // Check if the distance is near the rope length and both players are stationary
        boolean ropeIsFullyExtended = Math.abs(distanceBetweenPlayers - ropeLength) <= 15;  // Tolerance

        // Return true if both conditions are met
        return ropeIsFullyExtended
                && !physics.isOnGround()
                && otherPhysics.isOnGround();
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        if (isHanging()) {
            state.changeState(HANG);
            applyDamping(physics, 0.988f);
        } else {
            // Handle other states like FALL, JUMP, etc.
            if (physics.getVelocityY() > 0 && !physics.isOnGround()) {
                state.changeState(FALL);
            }
        }
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }

    public void loadPlayer1(Entity player1) {
        this.player1 = player1;

        otherState = player1.getComponent(StateComponent.class);
        otherPhysics = player1.getComponent(PhysicsComponent.class);
        otherView = player1.getComponent(ViewComponent.class);
    }

    private void applyDamping(PhysicsComponent physics, float factor) {
        Vec2 vel = physics.getBody().getLinearVelocity();
        Vec2 damped = vel.mul(factor);
        physics.getBody().setLinearVelocity(damped);

//        float angVel = physics.getBody().getAngularVelocity();
//        physics.getBody().setAngularVelocity(angVel * factor); // reduce spin
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