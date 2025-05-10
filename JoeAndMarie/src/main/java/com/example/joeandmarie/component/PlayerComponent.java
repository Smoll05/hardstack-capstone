package com.example.joeandmarie.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.TimerAction;
import com.example.joeandmarie.config.Constants;
import com.example.joeandmarie.data.event.GameProgressEvent;
import com.example.joeandmarie.data.viewmodel.GameProgressViewModel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerComponent extends Component {

    private GameProgressViewModel viewModel = GameProgressViewModel.getInstance();

    protected StateComponent state;
    protected PhysicsComponent physics;
    protected ViewComponent view;

    protected StateComponent otherState;
    protected PhysicsComponent otherPhysics;
    protected ViewComponent otherView;

    protected AnimatedTexture texture;
    protected AnimationChannel animIdle,
            animMove, animCrouch, animJump,
            animHang, animCry, animFall,
            animSwing, animPull, animPulled, animPlant,
            animSplat, animHold;

    private boolean isTiredTouchingWall = false;
    public static boolean isTouchingWall = false;
    private TimerAction wallClingTimer = null;

    protected boolean isOnGroundFlag = false;

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
            physics.setVelocityY(500);
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
                setFriction(1f);
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
                viewModel.onEvent(GameProgressEvent.UPDATE_DEEP_FALL_COUNT, 1);
            }

            setFriction(1f);
        }
    };

    protected Map<EntityState, StateData> stateData = new HashMap<>();

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
        }
    }

    public void stand() {
        state.changeState(STAND);

        if(physics.isOnGround()) {
            if (wallClingTimer != null && !wallClingTimer.isExpired()) {
                wallClingTimer.expire();
            }
        }

        isTiredTouchingWall = false;
        if(physics.getBody().getType() == BodyType.STATIC) {
            setBodyStatic(false);
        }
    }

    public void jump() {
        if (!physics.isOnGround()) {
            return;
        }

        setFriction(0f);

        PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
        float currentFriction = physics.getBody().getFixtures().getFirst().getFriction();

        System.out.println("Current Friction: " + currentFriction);

        physics.setVelocityY(-Constants.JUMP_FORCE);
        state.changeState(JUMP);
    }

    public boolean pull() {
        if (!physics.isOnGround()) {
            return false;
        }

        setBodyStatic(true);
        state.changeState(PULL);
        return true;
    }

    public void pulled() {
        state.changeState(PULLED);
        setFriction(0f);
    }

    public void hold() {
        if(!isTiredTouchingWall) {
            state.changeState(HOLD);
            setBodyStatic(true);

            wallClingTimer = FXGL.getGameTimer().runOnceAfter(() -> {
                isTiredTouchingWall = true;
                setBodyStatic(false);
                state.changeState(STAND);
            }, Duration.seconds(6));
        }

//        physics.setVelocityY(0);
//        if(entity.getScaleX() == 1) {
//            physics.setVelocityX(stateData.get(HOLD).moveSpeed);
//        } else {
//            physics.setVelocityX(-1 * stateData.get(HOLD).moveSpeed);
//        }
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

    abstract void swingMovement(EntityState newState, int scale);

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

    protected void setFriction(float friction) {
        physics.getBody().getFixtures().forEach(f -> f.setFriction(friction));
    }

    protected void setBodyStatic(boolean isKinematic) {
        if(isKinematic) {
            physics.getBody().setType(BodyType.STATIC);
        } else {
            physics.getBody().setType(BodyType.DYNAMIC);
        }
    }

}
