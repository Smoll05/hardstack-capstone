package com.example.joeandmarie;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.IntroScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
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

import java.util.EnumSet;

public class MainApplication extends GameApplication {

    Text nameTag1, nameTag2;

    private double lastUpdateTime = 0;
    private boolean isPulling = false;

    private RopeJoint ropeJoint;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Joe and Marie");
        settings.setVersion("1.0");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setFullScreenAllowed(true);
        settings.setMainMenuEnabled(true);
        settings.setIntroEnabled(true);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));

        // Attach your custom menu
        settings.setSceneFactory(new SceneFactory() {
//            @Override
//            public IntroScene newIntro() {
//                return new JoeIntroScene.MyIntroScene();
//            }

            @Override
            public FXGLMenu newMainMenu() {
                return new JoeMainMenu();
            }
        });
    }


    @Override
    protected void initGame() {
        FXGL.getGameScene().setBackgroundColor(Color.SADDLEBROWN);

        FXGL.getGameWorld().addEntityFactory(new PlatformerFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());

        try {
            FXGL.setLevelFromMap("test.tmx");
        } catch (Exception e) {
            e.printStackTrace();
        }

        var viewport = FXGL.getGameScene().getViewport();
//
//        int mapWidth = 40 * 32;
//        int mapHeight = 23 * 32;

        int mapWidth = 1600;
        int mapHeight = 950;

//        viewport.setZoom(0.8);

        Entity player2 = FXGL.spawn("player2", 500, 300);
        Entity player1 = FXGL.spawn("player1", 500, 200);

        getControlP1().loadPlayer2(player2);
        getControlP2().loadPlayer1(player1);

        FXGL.getGameTimer().runAtInterval(() -> {
            double midX = (player1.getX() + player2.getX()) / 2;
            double midY = (player1.getY() + player2.getY()) / 2;

            // Offset to center the camera
            double cameraX = midX - FXGL.getAppWidth() / 2.0;
            double cameraY = midY - FXGL.getAppHeight() / 2.0;

            // Clamp cameraX and cameraY to not go out of bounds
            cameraX = Math.max(0, Math.min(cameraX, mapWidth - FXGL.getAppWidth()));
            cameraY = Math.max(0, Math.min(cameraY, mapHeight - FXGL.getAppHeight()));

            FXGL.getGameScene().getViewport().setX(cameraX);
            FXGL.getGameScene().getViewport().setY(cameraY);
        }, Duration.seconds(1.0 / 60));

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
            nameTag2.setTranslateX(-nameTag2.getLayoutBounds().getWidth() / 2);
            nameTag2.setTranslateY(-20);

            getPlayer1().getViewComponent().addChild(nameTag1);
            getPlayer2().getViewComponent().addChild(nameTag2);
        }, Duration.seconds(0.1));
    }

    protected void initInput() {
        super.initInput();

        FXGL.getInput().addAction(new UserAction("Jump1") {
            @Override
            protected void onAction() {
                getControlP1().jump();
            }
        }, KeyCode.W);

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

                isPulling = true;
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
                getControlP2().stand();

                isPulling = false;
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

                        ropeJoint = FXGL.getPhysicsWorld()
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


    @Override
    protected void initPhysics() {
        super.initPhysics();

        var player1 = getPlayer1();
        var physics1 = player1.getComponent(PhysicsComponent.class);

        var player2 = getPlayer2();
        var physics2 = player2.getComponent(PhysicsComponent.class);

        Body bodyA = physics1.getBody();
        Body bodyB = physics2.getBody();

        FXGL.runOnce(() -> {

            RopeJointDef ropeDef = new RopeJointDef();
            ropeDef.setBodyA(bodyA);
            ropeDef.setBodyB(bodyB);
            ropeDef.localAnchorA.set(0, 0);
            ropeDef.localAnchorB.set(0, 0);
            ropeDef.maxLength = 3.0f;
            ropeDef.setBodyCollisionAllowed(false);

            ropeJoint = FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(ropeDef);
        }, Duration.seconds(0.1));
    }

    @Override
    protected void onUpdate(double tpf) {
        super.onUpdate(tpf);

        if (isPulling) {
            float current = ropeJoint.getMaxLength();
            if (current > 0) {
                float newLength = current - (float) (1f * tpf);
                ropeJoint.setMaxLength(Math.max(newLength, 0));
            }
        }
    }

    //        RevoluteJointDef revoluteDef = new RevoluteJointDef();
//        revoluteDef.initialize(bodyA, bodyB, bodyA.getWorldCenter());
//        revoluteDef.maxMotorTorque = 100f;
//        revoluteDef.motorSpeed = 10f;
//        revoluteDef.setBodyCollisionAllowed(false);

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

//        RevoluteJoint revoluteJoint = new RevoluteJointDef(bodyA, bodyB, new Vec2(0, 0));  // fixedAnchorBody is the body where the rope or bar is attached
//        revoluteJoint.setMaxMotorTorque(100f); // Limit the motor torque to control swinging force
//        revoluteJoint.setMotorSpeed(10f); // Set motor speed for rotating around the fixed point

//        DistanceJointDef def = new DistanceJointDef();
//        def.initialize(bodyA, bodyB, bodyA.getWorldCenter(), bodyB.getWorldCenter());
//        def.length = 3.0f;
//        def.setBodyCollisionAllowed(false);

    /// /        def.collideConnected = false;

//        RevoluteJointDef def = new RevoluteJointDef();
//        def.initialize(bodyA, bodyB, bodyA.getWorldCenter());
//
//        def.setBodyCollisionAllowed(false);


    // THIS IS THE BODY
//


    // 1. Create a RopeJoint to limit the maximum rope length

//        var world = FXGL.getPhysicsWorld().getJBox2DWorld();
    private Player1Component getControlP1() {
        return FXGL.getGameWorld().getSingleton(e -> e.hasComponent(Player1Component.class))
                .getComponent(Player1Component.class);
    }

    private Player2Component getControlP2() {
        return FXGL.getGameWorld().getSingleton(e -> e.hasComponent(Player2Component.class))
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
