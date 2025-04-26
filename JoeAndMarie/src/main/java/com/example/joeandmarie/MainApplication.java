package com.example.joeandmarie;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.joints.*;
import com.example.joeandmarie.component.Player1Component;
import com.example.joeandmarie.component.Player2Component;
import com.example.joeandmarie.entity.EntityType;
import com.example.joeandmarie.factory.PlatformerFactory;
import com.example.joeandmarie.factory.PlayerFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class MainApplication extends GameApplication {
    private Entity player1, player2;
    Text nameTag1, nameTag2;
    boolean isMoving = false;
    boolean leftDown = false;
    boolean rightDown = false;
    private long lastMoveTimeJoe = 0;
    private final long idleDelayJoe = 50;
    private long lastMoveTimeMarie = 0;
    private final long idleDelayMarie = 5; // milliseconds
    public static RopeJoint ropeJoint;

    private static final double TARGET_FPS = 60;
    private static final double TARGET_TPF = 1.0 / TARGET_FPS;

    private double lastUpdateTime = 0;



    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Joe and Marie");
//        settings.setFullScreenAllowed(true);
//        settings.setFullScreenFromStart(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setDeveloperMenuEnabled(true);
        settings.setProfilingEnabled(true);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new PlatformerFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());

        FXGL.spawn("player1", 500, 200);
        FXGL.spawn("player2", 500, 300);

//        FXGL.spawn("platform", 450, 315);
//        FXGL.spawn("platform", 550, 260);
//        FXGL.spawn("platform", 350, 260);
//        FXGL.spawn("platform", 450, 220);


        FXGL.spawn("platform", 0, 700);
        FXGL.spawn("platform", 100, 700);
        FXGL.spawn("platform", 200, 700);
        FXGL.spawn("platform", 300, 700);
        FXGL.spawn("platform", 400, 700);
        FXGL.spawn("platform", 500, 700);
        FXGL.spawn("platform", 600, 700);
        FXGL.spawn("platform", 700, 700);
        FXGL.spawn("platform", 800, 700);
        FXGL.spawn("platform", 900, 700);
        FXGL.spawn("platform", 1000, 700);

        FXGL.spawn("platform", 500, 500);

        FXGL.getPhysicsWorld().setGravity(0, 1250);

        FXGL.runOnce(() -> {
            nameTag1 = new Text("Joe");
            nameTag1.setFont(Font.font(14));
            nameTag1.setFill(Color.BLACK);

            nameTag1.setTranslateX(-nameTag1.getLayoutBounds().getWidth() / 2);
            nameTag1.setTranslateY(-20);

            nameTag2 = new Text("Marie");
            nameTag2.setFont(Font.font(14));
            nameTag2.setFill(Color.BLACK);

            nameTag2.setTranslateX(-nameTag1.getLayoutBounds().getWidth() / 2);
            nameTag2.setTranslateY(-20);

            getPlayer1().getViewComponent().addChild(nameTag1);
            getPlayer2().getViewComponent().addChild(nameTag2);
        }, Duration.seconds(0.1));
    }

    private boolean inputInitializedJoe = false;
    private boolean inputInitializedMarie = false;

//    @Override
//    protected void onUpdate(double tpf) {
//        var player = getPlayer1();
//        var pc = player.getComponent(PlayerComponent.class);
//        var physics = player.getComponent(PhysicsComponent.class);
//
//        var player2 = getPlayer2();
//        var pc2 = player2.getComponent(PlayerComponent.class);
//        var physics2 = player2.getComponent(PhysicsComponent.class);
//
//        long currentTime = System.currentTimeMillis();
//
//        if (!inputInitializedJoe && FXGL.getGameWorld().getEntitiesByType(EntityType.PLAYER1).size() > 0
//                && !inputInitializedMarie && FXGL.getGameWorld().getEntitiesByType(EntityType.PLAYER2).size() > 0) {
//            setupInput();
//            inputInitializedJoe = true;
//            inputInitializedMarie = true;
//        }
//
//        if (inputInitializedJoe) {
//            if (physics.getVelocityX() == 0 && currentTime - lastMoveTimeJoe > idleDelayJoe) {
//                pc.idle();
//            }
//        }
//
//        if (inputInitializedMarie) {
//            if (physics2.getVelocityX() == 0 && currentTime - lastMoveTimeMarie > idleDelayMarie) {
//                pc2.idle();
//            }
//        }
//    }
//
//
//    private void setupInput() {
//        var input = FXGL.getInput();
//        var player = getPlayer1();
//        var pc = player.getComponent(PlayerComponent.class);
//        var physics = player.getComponent(PhysicsComponent.class);
//
//        var player2 = getPlayer2();
//        var pc2 = player2.getComponent(PlayerComponent.class);
//        var physics2 = player2.getComponent(PhysicsComponent.class);
//
//        DistanceJointDef def = new DistanceJointDef();
//        def.setBodyA(physics.getBody());
//        def.setBodyB(physics2.getBody());
//        def.localAnchorA.set(0, 0);
//        def.localAnchorB.set(0, 0);
//        def.length = 6f;
//        def.frequencyHz = 1.5f;
//        def.dampingRatio = 0.6f;
//        def.setBodyCollisionAllowed(false);
//
//        DistanceJoint rope = FXGL.getPhysicsWorld()
//                        .getJBox2DWorld().createJoint(def);
//
//        rope.setLength(4f);
//        rope.setDampingRatio(0.8f);
//
//        RopeJointDef def = new RopeJointDef();
//        def.localAnchorA.set(0, 0);
//        def.localAnchorB.set(0, 0);
//        def.maxLength = 5.0f;
//        def.setBodyCollisionAllowed(false);
//
//        def.setBodyA(physics.getBody());
//        def.setBodyB(physics2.getBody());
//
//        RopeJoint rope = FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(def);
//
//        FXGL.onKeyDown(KeyCode.W, () -> {
////            Checking if on the ground
//            if (Math.abs(physics.getVelocityY()) < 0.1) {
//                physics.setVelocityY(-Constants.JUMP_FORCE);
//                lastMoveTimeJoe = System.currentTimeMillis();
//            }
//        });
//
//        FXGL.onKey(KeyCode.A, () -> {
//            physics.setVelocityX(-Constants.RUNNING_SPEED);
//            pc.moveLeft();
//            lastMoveTimeJoe = System.currentTimeMillis();
//        });
//
//        FXGL.onKey(KeyCode.D, () -> {
//            physics.setVelocityX(Constants.RUNNING_SPEED);
//            pc.moveRight();
//            lastMoveTimeJoe = System.currentTimeMillis();
//        });
//
//        FXGL.onKey(KeyCode.S, () -> {
//            if (Math.abs(physics.getVelocityY()) < 0.1) {
//                pc.crouch();
//                physics.setVelocityX(0);
//                lastMoveTimeJoe = System.currentTimeMillis();
//            }
//        });
//
//        FXGL.onKeyDown(KeyCode.UP, () -> {
////            Checking if on the ground
//            if (Math.abs(physics2.getVelocityY()) < 0.1) {
//                physics2.setVelocityY(-400);
//                lastMoveTimeMarie = System.currentTimeMillis();
//            }
//        });
//
//        FXGL.onKey(KeyCode.LEFT, () -> {
//            physics2.setVelocityX(-Constants.RUNNING_SPEED);
//            pc2.moveLeft();
//            lastMoveTimeMarie = System.currentTimeMillis();
//        });
//
//        FXGL.onKey(KeyCode.RIGHT, () -> {
//            physics2.setVelocityX(Constants.RUNNING_SPEED);
//            pc2.moveRight();
//            lastMoveTimeMarie = System.currentTimeMillis();
//        });
//
//        FXGL.onKey(KeyCode.DOWN, () -> {
//            if (Math.abs(physics2.getVelocityY()) < 0.1) {
//                pc2.crouch();
//                physics2.setVelocityX(0);
//                lastMoveTimeMarie = System.currentTimeMillis();
//            }
//        });
//
//    }

    @Override
    protected void initInput() {

        // Player 1 Controls
        FXGL.onKey(KeyCode.W, () -> getControlP1().jump());

        FXGL.getInput().addAction(new UserAction("Pull1") {
            @Override
            protected void onAction() {
                getControlP1().pull();
                getControlP2().pulled();
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
                getControlP2().stand();
            }
        }, KeyCode.E);

        FXGL.getInput().addAction(new UserAction("Pull2") {
            @Override
            protected void onAction() {
                getControlP2().pull();
                getControlP1().pulled();
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
                getControlP2().stand();
            }
        }, KeyCode.O);

        FXGL.getInput().addAction(new UserAction("Plant") {
            @Override
            protected void onAction() {
                getControlP1().plant();
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
            }
        }, KeyCode.F);

        FXGL.getInput().addAction(new UserAction("Cry") {
            @Override
            protected void onAction() {
                if (ropeJoint != null) {
                    FXGL.getPhysicsWorld().getJBox2DWorld().destroyJoint(ropeJoint);
                    ropeJoint = null;
                }

                getControlP1().cry();
                getControlP2().cry();
            }

            @Override
            protected void onActionEnd() {

                getControlP1().stand();
                getControlP2().stand();

                FXGL.runOnce(() -> {
                    var physics1 = getPlayer1().getComponent(PhysicsComponent.class);
                    var physics2 = getPlayer2().getComponent(PhysicsComponent.class);

                    var bodyA = physics1.getBody();
                    var bodyB = physics2.getBody();

                    // Recreate joint
                    if (bodyA.isActive() && bodyB.isActive()) {
                        RopeJointDef newDef = new RopeJointDef();
                        newDef.setBodyA(bodyA);
                        newDef.setBodyB(bodyB);
                        newDef.localAnchorA.set(0, 0);
                        newDef.localAnchorB.set(0, 0);
                        newDef.maxLength = 3.0f;
                        newDef.setBodyCollisionAllowed(false);

                        ropeJoint = (RopeJoint) FXGL.getPhysicsWorld()
                                .getJBox2DWorld()
                                .createJoint(newDef);
                    }
                }, Duration.seconds(1.05)); // 0.2 is usually enough


            }
        }, KeyCode.C);

        FXGL.getInput().addAction(new UserAction("Hold1") {
            @Override
            protected void onAction() {
                if(Player1Component.isTouchingWall) {
                    getControlP1().hold();
                } else {
                    getControlP1().stand();
                }
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
            }
        }, KeyCode.Q);

        FXGL.getInput().addAction(new UserAction("Hold2") {
            @Override
            protected void onAction() {
                if(Player2Component.isTouchingWall) {
                    getControlP2().hold();
                } else {
                    getControlP2().stand();
                }
            }

            @Override
            protected void onActionEnd() {
                getControlP2().stand();
            }
        }, KeyCode.P);

        FXGL.getInput().addAction(new UserAction("Left1") {
            @Override
            protected void onAction() {
                getControlP1().moveLeft();
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stop();
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Right1") {
            @Override
            protected void onAction() {
                getControlP1().moveRight();
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stop();
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Crouch1") {
            @Override
            protected void onAction() {
                getControlP1().crouch();
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
            }
        }, KeyCode.S);

        // Player 2 Controls
        FXGL.onKey(KeyCode.UP, () -> getControlP2().jump());

        FXGL.getInput().addAction(new UserAction("Left2") {
            @Override
            protected void onAction() {
                getControlP2().moveLeft();
            }

            @Override
            protected void onActionEnd() {
                getControlP2().stop();
            }
        }, KeyCode.LEFT);

        FXGL.getInput().addAction(new UserAction("Right2") {
            @Override
            protected void onAction() {
                getControlP2().moveRight();
            }

            @Override
            protected void onActionEnd() {
                getControlP2().stop();
            }
        }, KeyCode.RIGHT);

        FXGL.getInput().addAction(new UserAction("Crouch3") {
            @Override
            protected void onAction() {
                getControlP2().crouch();
            }

            @Override
            protected void onActionEnd() {
                getControlP2().stand();
            }
        }, KeyCode.DOWN);
    }

    //    @Override
//    protected void initInput() {
//        var player = getPlayer1();
//        var pc = player.getComponent(PlayerComponent.class);
//        var physics = player.getComponent(PhysicsComponent.class);
//
//        final boolean[] moving = {false};
//
//        FXGL.onKeyDown(KeyCode.W, () -> {
//            physics.setVelocityY(-400); // Jump
//        });
//
//        FXGL.onKey(KeyCode.A, () -> {
//            physics.setVelocityX(-150);
//            pc.moveLeft();
//            moving[0] = true;
//        });
//
//        FXGL.onKey(KeyCode.D, () -> {
//            physics.setVelocityX(150);
//            pc.moveRight();
//            moving[0] = true;
//        });
//
//     //   FXGL.onKeyDown(KeyCode.S, () -> {
//            pc.crouch();
//        });
//        if (!moving[0]) {
//            physics.setVelocityX(0);
//            pc.idle();
//        }
//    }


    @Override
    protected void initPhysics() {
        super.initPhysics();

        var player1 = getPlayer1();
        var physics1 = player1.getComponent(PhysicsComponent.class);

        var player2 = getPlayer2();
        var physics2 = player2.getComponent(PhysicsComponent.class);

//        RopeJointDef def = new RopeJointDef();
//        def.localAnchorA.set(0, 0);
//        def.localAnchorB.set(0, 0);
//        def.maxLength = 3.0f;
//        def.setBodyCollisionAllowed(false);
//
//        def.setBodyA(physics1.getBody());
//        def.setBodyB(physics2.getBody());
//
//        RopeJoint rope = FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(def);

        var bodyA = physics1.getBody();
        var bodyB = physics2.getBody();

//        RevoluteJoint revoluteJoint = new RevoluteJointDef(bodyA, bodyB, new Vec2(0, 0));  // fixedAnchorBody is the body where the rope or bar is attached
//        revoluteJoint.setMaxMotorTorque(100f); // Limit the motor torque to control swinging force
//        revoluteJoint.setMotorSpeed(10f); // Set motor speed for rotating around the fixed point

//        DistanceJointDef def = new DistanceJointDef();
//        def.initialize(bodyA, bodyB, bodyA.getWorldCenter(), bodyB.getWorldCenter());
//        def.length = 3.0f;
//        def.setBodyCollisionAllowed(false);
////        def.collideConnected = false;

//        RevoluteJointDef def = new RevoluteJointDef();
//        def.initialize(bodyA, bodyB, bodyA.getWorldCenter());
//
//        def.setBodyCollisionAllowed(false);


        // THIS IS THE BODY

//        RevoluteJointDef jointDef = new RevoluteJointDef();
//        jointDef.initialize(bodyA, bodyB, bodyA.getWorldCenter());
//        jointDef.maxMotorTorque = 100f;
//        jointDef.motorSpeed = 10f;
//        jointDef.setBodyCollisionAllowed(false);
//
//        FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(jointDef);
//


        // 1. Create a RopeJoint to limit the maximum rope length

//        var world = FXGL.getPhysicsWorld().getJBox2DWorld();

        RopeJointDef ropeDef = new RopeJointDef();
        ropeDef.setBodyA(bodyA);
        ropeDef.setBodyB(bodyB);
        ropeDef.localAnchorA.set(0, 0);
        ropeDef.localAnchorB.set(0, 0);
        ropeDef.maxLength = 3.0f;
        ropeDef.setBodyCollisionAllowed(false);

        ropeJoint = (RopeJoint) FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(ropeDef);
    }

    private Player1Component getControlP1() {
        return getGameWorld().getSingleton(e -> e.hasComponent(Player1Component.class))
                .getComponent(Player1Component.class);
    }

    private Player2Component getControlP2() {
        return getGameWorld().getSingleton(e -> e.hasComponent(Player2Component.class))
                .getComponent(Player2Component.class);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Entity getPlayer1() {
        return FXGL.getGameWorld().getSingleton(EntityType.PLAYER1);
    }

    public Entity getPlayer2() {
        return FXGL.getGameWorld().getSingleton(EntityType.PLAYER2);
    }
}
