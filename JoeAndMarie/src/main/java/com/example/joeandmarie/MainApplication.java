package com.example.joeandmarie;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.*;
import com.example.joeandmarie.component.Player1Component;
import com.example.joeandmarie.component.Player2Component;
import com.example.joeandmarie.config.Constants;
import com.example.joeandmarie.entity.EntityType;
import com.example.joeandmarie.factory.PlatformerFactory;
import com.example.joeandmarie.factory.PlayerFactory;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.EnumSet;

public class MainApplication extends GameApplication {

    Text nameTag1, nameTag2;

    private boolean isPulling = false;

    private RopeJoint ropeJoint;
    private DistanceJoint distanceJoint = null;
    private RevoluteJoint revoluteJoint = null;

    private boolean isSwinging;
    private boolean isCrouching = false;

    private boolean highFrictionSet = false;
    private boolean lowFrictionSet = false;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Joe and Marie");
        settings.setVersion("1.0");
        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setFullScreenAllowed(true);
        settings.setMainMenuEnabled(true);
//        settings.setIntroEnabled(true);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
        settings.setDeveloperMenuEnabled(true);
        settings.setProfilingEnabled(true);
        settings.setFullScreenFromStart(true);

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
        FXGL.getGameScene().setBackgroundColor(Color.ALICEBLUE);

        FXGL.getGameWorld().addEntityFactory(new PlatformerFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());

        try {
            FXGL.setLevelFromMap("test2.tmx");
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
                setFriction(0f, getPlayer1());
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Pull1") {
            @Override
            protected void onAction() {
                if(getControlP1().pull()) {
                    getControlP2().pulled();
                    isPulling = true;
                }
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
                getControlP2().stand();

                isPulling = false;
            }
        }, KeyCode.E);

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
                getControlP1().cry();
                getControlP2().cry();
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
                getControlP2().stand();
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
                isCrouching = true;
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
                isCrouching = false;
            }
        }, KeyCode.S);

        // Player 2 Controls

        FXGL.getInput().addAction(new UserAction("Jump2") {
            @Override
            protected void onAction() {
                getControlP2().jump();
                setFriction(0f, getPlayer2());
            }
        }, KeyCode.UP);

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

        FXGL.getInput().addAction(new UserAction("Crouch2") {
            @Override
            protected void onAction() {
                getControlP2().crouch();
                isCrouching = true;
            }

            @Override
            protected void onActionEnd() {
                getControlP2().stand();
                isCrouching = false;
            }
        }, KeyCode.DOWN);

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

        FXGL.getInput().addAction(new UserAction("Pull2") {
            @Override
            protected void onAction() {
                if(getControlP2().pull()) {
                    getControlP1().pulled();
                    isPulling = true;
                }
            }

            @Override
            protected void onActionEnd() {
                getControlP1().stand();
                getControlP2().stand();

                isPulling = false;
            }
        }, KeyCode.O);
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
            ropeDef.maxLength = Constants.ROPE_LENGTH;
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
        } else { // return to original rope length
            if(ropeJoint != null) {
                float current = ropeJoint.getMaxLength();
                if (current < Constants.ROPE_LENGTH) {
                    float newLength = current + (float) (2f * tpf);
                    ropeJoint.setMaxLength(Math.min(newLength, Constants.ROPE_LENGTH));
                }
            }
        }

        // Check if a player starts swinging to create and delete distance joint once
        boolean isSwingActive = getControlP1().getState().isIn(getControlP1().getSWING()) ||
                getControlP2().getState().isIn(getControlP2().getSWING());

        if (isSwingActive && !isSwinging && isCrouching) {
            isSwinging = true;
            if(distanceJoint == null) {
                createDistanceJoint();
            }
        }
        // Check for swing end
        else if (!isSwingActive && isSwinging && !isCrouching) {
            isSwinging = false;
            if(distanceJoint != null) {
                deleteDistanceJoint();
            }
        }

        if(distanceJoint != null && !isSwingActive) {
            deleteDistanceJoint();
        }


//        if (getPlayer1().getComponent(PhysicsComponent.class).isOnGround() &&
//                getPlayer2().getComponent(PhysicsComponent.class).isOnGround()) {
//
//            if(!highFrictionSet) {
//                setFriction(5f, getPlayer1());
//                setFriction(5f, getPlayer2());
//
//                lowFrictionSet = false;
//                highFrictionSet = true;
//
//                System.out.println("High Friction Has Been Set");
//            }
//
//        } else {
//
//            if (!lowFrictionSet) {
//                setFriction(0.8f, getPlayer1());
//                setFriction(0.8f, getPlayer2());
//
//                lowFrictionSet = true;
//                highFrictionSet = false;
//
//                System.out.println("Low Friction Has Been Set");
//            }
//
//        }


//        if (isSwingActive && !isSwinging && isCrouching) {
//            isSwinging = true;
//            if(revoluteJoint == null) {
//                createRevoluteJoint();
//            }
//        }
//        // Check for swing end
//        else if (!isSwingActive && isSwinging && !isCrouching) {
//            isSwinging = false;
//            if(revoluteJoint != null) {
//                deleteRevoluteJoint();
//            }
//        }
//
//        if(revoluteJoint != null && !isSwingActive) {
//            deleteRevoluteJoint();
//        }
    }

    public void deleteDistanceJoint() {
        FXGL.getPhysicsWorld().getJBox2DWorld().destroyJoint(distanceJoint);
        distanceJoint = null;

        System.out.println("DELETED DISTANCE JOINT");
    }

    public void createDistanceJoint() {
        var player1 = getPlayer1();
        var physics1 = player1.getComponent(PhysicsComponent.class);

        var player2 = getPlayer2();
        var physics2 = player2.getComponent(PhysicsComponent.class);

        Body bodyA = physics1.getBody();
        Body bodyB = physics2.getBody();

        FXGL.runOnce(() -> {

            DistanceJointDef distanceDef = new DistanceJointDef();
            distanceDef.setBodyA(bodyA);
            distanceDef.setBodyB(bodyB);
            distanceDef.localAnchorA.set(0, 0);
            distanceDef.localAnchorB.set(0, 0);
            distanceDef.length = ropeJoint.getMaxLength(); // Use the stored rope length
            distanceDef.frequencyHz = 0.0f;  // Make it stiff.  Adjust as needed.
            distanceDef.dampingRatio = 1.0f; //  Damping for stability.
            distanceDef.setBodyCollisionAllowed(false);

            distanceJoint = (DistanceJoint) FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(distanceDef);
        }, Duration.seconds(0.1));

        System.out.println("CREATED DISTANCE JOINT");
    }

    public void deleteRevoluteJoint() {
        FXGL.getPhysicsWorld().getJBox2DWorld().destroyJoint(revoluteJoint);
        revoluteJoint = null;

        System.out.println("DELETED REVOLUTE JOINT");
    }

    public void createRevoluteJoint() {
//        Point2D position1 = entity1.getPosition();
//        Point2D position2 = entity2.getPosition();

// Convert Point2D to Vec2
//        Vec2 vecPosition1 = new Vec2((float) position1.getX(), (float) position1.getY());
//        Vec2 vecPosition2 = new Vec2((float) position2.getX(), (float) position2.getY());

// Set the local anchor points for the revolute joint
//        Vec2 localAnchor1 = new Vec2(anchor.x - bodyA.getPosition().x, anchor.y - bodyA.getPosition().y);
//        Vec2 localAnchor2 = new Vec2(anchor.x - bodyB.getPosition().x, anchor.y - bodyB.getPosition().y);

// Create the revolute joint definition

//        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
//        revoluteJointDef.setBodyA(bodyA);
//        revoluteJointDef.setBodyB(bodyB);
//
//        revoluteJointDef.localAnchorA.set(anchor1);
//        revoluteJointDef.localAnchorB.set(anchor2);
//
//        revoluteJointDef.maxMotorTorque = 100f;
//        revoluteJointDef.motorSpeed = 10f;
//        revoluteJointDef.setBodyCollisionAllowed(false);


//        Entity player1 = getPlayer1();
//        PhysicsComponent physics1 = player1.getComponent(PhysicsComponent.class);
//
//        Entity player2 = getPlayer2();
//        PhysicsComponent physics2 = player2.getComponent(PhysicsComponent.class);
//
//        Body bodyA = physics1.getBody();
//        Body bodyB = physics2.getBody();

//        Point2D worldAnchor = player1.getCenter().midpoint(player2.getCenter());
//        Point2D anchor1 = worldAnchor.subtract(player1.getCenter());
//        Point2D anchor2 = worldAnchor.subtract(player2.getCenter());

//        RevoluteJointDef def = new RevoluteJointDef();
//        PhysicsWorld pWorld = FXGL.getPhysicsWorld();
//
//        def.localAnchorA = pWorld.toPoint(
//                player1.getAnchoredPosition().add(anchor1)).subLocal(bodyA.getWorldCenter()
//        );
//
//        def.localAnchorB = pWorld.toPoint(
//                player2.getAnchoredPosition().add(anchor2)).subLocal(bodyB.getWorldCenter()
//        );
//
//        def.enableMotor = true;
//
//        revoluteJoint = pWorld.addJoint(player1, player2, def);
//

//        RevoluteJointDef revoluteDef = new RevoluteJointDef();
//        revoluteDef.initialize(bodyA, bodyB, bodyA.getWorldCenter());
//        revoluteDef.maxMotorTorque = 100f;
//        revoluteDef.motorSpeed = 10f;
//        revoluteDef.setBodyCollisionAllowed(false);
//
//        revoluteJoint = FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(revoluteDef);
    }


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

    private void setFriction(float friction, Entity e) {
        PhysicsComponent p = e.getComponent(PhysicsComponent.class);
        p.getBody().getFixtures().getFirst().setFriction(friction);
    }

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
