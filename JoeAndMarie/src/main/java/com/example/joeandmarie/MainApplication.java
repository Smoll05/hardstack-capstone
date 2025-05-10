package com.example.joeandmarie;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.IntroScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.*;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.joints.*;
import com.example.joeandmarie.component.Player1Component;
import com.example.joeandmarie.component.Player2Component;
import com.example.joeandmarie.config.Constants;
import com.example.joeandmarie.controller.JoeMainMenuController;
import com.example.joeandmarie.data.event.GameProgressEvent;
import com.example.joeandmarie.data.model.GameProgress;
import com.example.joeandmarie.data.viewmodel.GameProgressViewModel;
import com.example.joeandmarie.data.viewmodel.SettingPreferenceViewModel;
import com.example.joeandmarie.entity.EntityType;
import com.example.joeandmarie.factory.BlockFactory;
import com.example.joeandmarie.factory.PlatformerFactory;
import com.example.joeandmarie.factory.PlayerFactory;
import com.example.joeandmarie.threading.SaveRunnable;
import com.example.joeandmarie.ui.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.EnumSet;

public class MainApplication extends GameApplication {

    private Text nameTag1, nameTag2;

    private boolean isPulling = false;

    private RopeJoint ropeJoint;
    private DistanceJoint distanceJoint = null;
    private RevoluteJoint revoluteJoint = null;

    private boolean hasPlayerOnePassedOneWayPlatform = false;
    private boolean hasPlayerTwoPassedOneWayPlatform = false;

    private boolean isSwinging;
    private boolean isCrouching = false;

    private static Sound sfx_cry;
    private static Sound sfx_hover;
    private static Sound sfx_splat;
    private static Sound sfx_click;

    private static Music music_underground;

    private final GameProgressViewModel gameProgressViewModel = GameProgressViewModel.getInstance();
    private final SettingPreferenceViewModel settingPreferenceViewModel = SettingPreferenceViewModel.getInstance();

    private double originY;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Joe and Marie");
        settings.setVersion("1.0");
        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setIntroEnabled(true);
        settings.setFullScreenAllowed(true);
        settings.setMainMenuEnabled(true);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
//        settings.setDeveloperMenuEnabled(true);
//        settings.setProfilingEnabled(true);
        settings.setFullScreenFromStart(true);

        // Attach your custom menu
        settings.setSceneFactory(new SceneFactory() {

            @Override
            public IntroScene newIntro() {
                return new JoeIntroScene();
            }

            @Override
            public FXGLMenu newMainMenu() {
                return new JoeMainMenu();
            }

//            @Override
//            public FXGLMenu newGameMenu() {
//                return new JoeGameMenu(); // This replaces the ESC menu
//            }
        });
    }


    @Override
    protected void initGame() {
        FXGL.getGameScene().setBackgroundColor(Color.ALICEBLUE);

        FXGL.getGameWorld().addEntityFactory(new PlatformerFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
        FXGL.getGameWorld().addEntityFactory(new BlockFactory());

        sfx_click = FXGL.getAssetLoader().loadSound("sound_meow.wav");
        sfx_cry = FXGL.getAssetLoader().loadSound("sound_back_checkpoint.wav");
        sfx_hover = FXGL.getAssetLoader().loadSound("sound_button_hover.wav");
        sfx_splat = FXGL.getAssetLoader().loadSound("sound_cry.wav");

        music_underground = FXGL.getAssetLoader().loadMusic("music_underground_city.mp3");

        FXGL.getAudioPlayer().stopMusic(JoeMainMenu.getMainMenuMusic());
        FXGL.getAudioPlayer().loopMusic(music_underground);

        try {
            FXGL.setLevelFromMap("FirstLevel.tmx");
        } catch (Exception e) {
            e.printStackTrace();
        }

        var viewport = FXGL.getGameScene().getViewport();
//
//        int mapWidth = 40 * 32;
//        int mapHeight = 23 * 32;

//        int mapWidth = 1600;
//        int mapHeight = 950;

        int mapWidth = 150 * 32;  // 4,800 pixels
        int mapHeight = 112 * 32; // 3,584 pixels

//        viewport.setZoom(0.8);

//        Entity player2 = FXGL.spawn("player2", 500, 300);
//        Entity player1 = FXGL.spawn("player1", 500, 200);

        FXGL.set("spawnPoint", FXGL.getGameWorld().getSingleton(EntityType.SPAWN_POINT));

        double x, y;
        Entity spawnPoint = FXGL.geto("spawnPoint");
        originY = spawnPoint.getY();

        GameProgress snapshot = gameProgressViewModel.getSnapshot();

        if(snapshot.getHeightProgress() == 0) {
            // Retrieve spawn point position
            x = spawnPoint.getX();
            y = spawnPoint.getY();
        } else {
            x = snapshot.getXCoordinate();
            y = snapshot.getYCoordinate();
        }

        // Spawn players at that point
        Entity player1 = FXGL.spawn("player1", x, y);
        Entity player2 = FXGL.spawn("player2", x, y);

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
            protected void onActionBegin() {
                FXGL.getAudioPlayer().playSound(sfx_cry);
            }

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
                if(Player1Component.isTouchingWall) return;
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
                if(Player1Component.isTouchingWall) return;
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
            }

        }, KeyCode.UP);

        FXGL.getInput().addAction(new UserAction("Left2") {
            @Override
            protected void onAction() {
                if(Player2Component.isTouchingWall) return;
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
                if(Player2Component.isTouchingWall) return;
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


        // One Way Platform Logic
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER1, EntityType.ONE_WAY_PLATFORM) {

            @Override
            protected void onCollisionBegin(Entity player, Entity platform) {
                if (player.getBottomY() >= platform.getY() && player.getY() + 32 >= platform.getBottomY()) {
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        player.getComponent(PhysicsComponent.class).getBody().setType(BodyType.KINEMATIC);
                        player.getComponent(PhysicsComponent.class).setVelocityY(-400);
                    }, Duration.seconds(0));
                    hasPlayerOnePassedOneWayPlatform = true;
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity platform) {
                if(hasPlayerOnePassedOneWayPlatform) {
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        player.getComponent(PhysicsComponent.class).getBody().setType(BodyType.DYNAMIC);
                        player.getComponent(PhysicsComponent.class).setVelocityY(-200);
                        player.getComponent(PhysicsComponent.class).getBody().getFixtures().getFirst().setFriction(1f);
                    }, Duration.seconds(0));
                    hasPlayerOnePassedOneWayPlatform = false;
                }
            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER2, EntityType.ONE_WAY_PLATFORM) {

            @Override
            protected void onCollisionBegin(Entity player, Entity platform) {
                if (player.getBottomY() >= platform.getY() && player.getY() + 32 >= platform.getBottomY()) {
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        player.getComponent(PhysicsComponent.class).getBody().setType(BodyType.KINEMATIC);
                        player.getComponent(PhysicsComponent.class).setVelocityY(-400);
                    }, Duration.seconds(0));
                    hasPlayerTwoPassedOneWayPlatform = true;
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity platform) {
                if(hasPlayerTwoPassedOneWayPlatform) {
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        player.getComponent(PhysicsComponent.class).getBody().setType(BodyType.DYNAMIC);
                        player.getComponent(PhysicsComponent.class).setVelocityY(-200);
                        player.getComponent(PhysicsComponent.class).getBody().getFixtures().getFirst().setFriction(1f);
                    }, Duration.seconds(0));
                    hasPlayerTwoPassedOneWayPlatform = false;
                }
            }
        });


        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER1, EntityType.CHECK_POINT) {

            @Override
            protected void onCollisionBegin(Entity player, Entity checkpoint) {

                SavingGameSpinnerUi spinnerUi = new SavingGameSpinnerUi();
                Node spinnerView = spinnerUi.getView();

                double screenWidth = FXGL.getAppWidth();
                double screenHeight = FXGL.getAppHeight();

                spinnerView.setTranslateX(screenWidth - spinnerView.prefWidth(-1) - 250);
                spinnerView.setTranslateY(screenHeight - spinnerView.prefHeight(-1) - 50);

                FXGL.addUINode(spinnerView);

                Thread saveThread = new Thread(new SaveRunnable());
                saveThread.start();

                FXGL.runOnce(() -> {

                    FXGL.getGameScene().removeUINode(spinnerView);

                }, Duration.seconds(2));

            }
        });

    }

    @Override
    protected void initUI() {
        createRopeVisualization();

        HeightProgressUi heightProgressUi = new HeightProgressUi();
        Node heightProgress = heightProgressUi.getView();
        FXGL.addUINode(heightProgress);
        gameProgressViewModel.addObserver(heightProgressUi);
    }

    @Override
    protected void onUpdate(double tpf) {

        int currentHeight = (int) ((-getPlayer1().getY() + originY) / 50);

        gameProgressViewModel.onEvent(GameProgressEvent.UPDATE_HEIGHT, currentHeight);
        gameProgressViewModel.onEvent(GameProgressEvent.UPDATE_X_COORDINATE, (float) getPlayer1().getX());
        gameProgressViewModel.onEvent(GameProgressEvent.UPDATE_Y_COORDINATE, (float) getPlayer1().getY());

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

//    private void createRopeVisualization() {
//        var physics1 = getPlayer1().getComponent(PhysicsComponent.class);
//
//        Body bodyA = physics1.getBody();
//        Vec2 position = bodyA.getPosition();
//        float xPos = position.x;
//        float yPos = position.y;
//
//        PhysicsComponent firstPhysics = new PhysicsComponent();
//        FixtureDef firstDef = new FixtureDef();
//
//        firstDef.setSensor(true);
//
//        firstPhysics.setBodyType(BodyType.DYNAMIC);
//        firstPhysics.setFixtureDef(firstDef);
//
//        Entity firstRopeSegment = entityBuilder()
//                .at(xPos, yPos)
//                .view(texture("rope_segment.png", 8, 8))
//                .with(firstPhysics)
//                .buildAndAttach();
//
//        addRopeJoint(getPlayer1(), firstRopeSegment);
//
//        Entity last = firstRopeSegment;
//
//        int ropeSegmentSize = 10;
//
//        for (int i = 0; i < ropeSegmentSize; i++) {
//            PhysicsComponent physics = new PhysicsComponent();
//            FixtureDef def = new FixtureDef();
//
//            def.setSensor(true);
//
//            physics.setBodyType(BodyType.DYNAMIC);
//            physics.setFixtureDef(def);
//
//            Entity ropeSegment = entityBuilder()
//                    .at(xPos * i + 8, yPos)
//                    .view(texture("rope_segment.png", 12, 12))
//                    .with(physics)
//                    .buildAndAttach();
//
//            addRopeJoint(last, ropeSegment);
//
//            last = ropeSegment;
//        }
//
//        addRopeJoint(last, getPlayer2());
//    }
//
//    private void addRopeJoint(Entity e1, Entity e2) {
//        RopeJointDef ropeDef = new RopeJointDef();
//        ropeDef.setBodyA(e1.getComponent(PhysicsComponent.class).getBody());
//        ropeDef.setBodyB(e2.getComponent(PhysicsComponent.class).getBody());
//        ropeDef.localAnchorA.set(0, 0);
//        ropeDef.localAnchorB.set(0, 0);
//        ropeDef.maxLength = 0.2f;
//        ropeDef.setBodyCollisionAllowed(false);
//
//        FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(ropeDef);
//    }

//    private void createRopeVisualization() {
//        var physics1 = getPlayer1().getComponent(PhysicsComponent.class);
//
//        Vec2 position = physics1.getBody().getPosition();
//        float startX = position.x * 64;
//        float startY = position.y * 64;
//
//        PhysicsComponent firstPhysics = new PhysicsComponent();
//        FixtureDef firstDef = new FixtureDef();
//        firstDef.setSensor(true); // no collision
//        firstPhysics.setBodyType(BodyType.DYNAMIC);
//        firstPhysics.setFixtureDef(firstDef);
//
//        Entity firstRopeSegment = entityBuilder()
//                .at(startX, startY)
//                .view(texture("rope_segment.png", 12, 12))
//                .with(firstPhysics)
//                .buildAndAttach();
//
//        addRopeJoint(getPlayer1(), firstRopeSegment, 12f / 64f); // segment length in meters
//
//        Entity last = firstRopeSegment;
//        int segmentCount = 10;
//
//        for (int i = 1; i <= segmentCount; i++) {
//            float segmentX = startX + i * 12; // evenly spaced
//            PhysicsComponent physics = new PhysicsComponent();
//            FixtureDef def = new FixtureDef();
//            def.setSensor(true); // or false for physical interactions
//            def.setDensity(1.0f);
//            physics.setBodyType(BodyType.DYNAMIC);
//            physics.setFixtureDef(def);
//
//            Entity ropeSegment = entityBuilder()
//                    .at(segmentX, startY)
//                    .view(texture("rope_segment.png", 12, 12))
//                    .with(physics)
//                    .buildAndAttach();
//
//            addRopeJoint(last, ropeSegment, 12f / 64f); // same length
//            last = ropeSegment;
//        }
//
//        addRopeJoint(last, getPlayer2(), 12f / 64f);
//    }

    private void addRopeJoint(Entity e1, Entity e2, float maxLengthMeters) {
        RopeJointDef ropeDef = new RopeJointDef();
        ropeDef.setBodyA(e1.getComponent(PhysicsComponent.class).getBody());
        ropeDef.setBodyB(e2.getComponent(PhysicsComponent.class).getBody());
        ropeDef.localAnchorA.set(0, 0);
        ropeDef.localAnchorB.set(0, 0);
        ropeDef.maxLength = maxLengthMeters;
        ropeDef.setBodyCollisionAllowed(false);

        FXGL.getPhysicsWorld().getJBox2DWorld().createJoint(ropeDef);
    }

    private void createRopeVisualization() {
//        CubicCurve curve = new CubicCurve();
//        curve.setStroke(Color.rgb(49, 52, 73));
//        curve.setStrokeWidth(5);
//        curve.setFill(Color.TRANSPARENT);
//        FXGL.getGameScene().addUINode(curve);
//
//        FXGL.getGameTimer().runAtInterval(() -> {
//            var viewport = FXGL.getGameScene().getViewport();
//            double offsetX = viewport.getX();
//            double offsetY = viewport.getY();
//
//            Point2D p1 = getPlayer1().getCenter().subtract(offsetX, offsetY);
//            Point2D p2 = getPlayer2().getCenter().subtract(offsetX, offsetY);
//
//            curve.setStartX(p1.getX());
//            curve.setStartY(p1.getY());
//            curve.setEndX(p2.getX());
//            curve.setEndY(p2.getY());
//
//            double distance = p1.distance(p2);
//
//            // Sag decreases as players get farther apart
//            double sag = Math.max(0, 100 - distance * 0.5);
//
//            // Optional: tweak sag formula based on testing
//            double dx = p2.getX() - p1.getX();
//            double dy = p2.getY() - p1.getY();
//
//            curve.setControlX1(p1.getX() + dx * 0.25);
//            curve.setControlY1(p1.getY() + dy * 0.25 + sag);
//
//            curve.setControlX2(p1.getX() + dx * 0.75);
//            curve.setControlY2(p1.getY() + dy * 0.75 + sag);
//        }, Duration.seconds(1.0 / 60));

    CubicCurve curve = new CubicCurve();
    curve.setStroke(Color.rgb(49, 52, 73));
    curve.setStrokeWidth(5);
    curve.setFill(Color.TRANSPARENT);
    FXGL.getGameScene().addUINode(curve);

    FXGL.getGameTimer().runAtInterval(() -> {
            var viewport = FXGL.getGameScene().getViewport();
            double offsetX = viewport.getX();
            double offsetY = viewport.getY();

            Point2D p1 = getPlayer1().getCenter().subtract(offsetX, offsetY);
            Point2D p2 = getPlayer2().getCenter().subtract(offsetX, offsetY);

            curve.setStartX(p1.getX());
            curve.setStartY(p1.getY());
            curve.setEndX(p2.getX());
            curve.setEndY(p2.getY());

            double distance = p1.distance(p2);

            // Max rope length (3 units) and max player-to-rope distance (150 units)
            double maxRopeLength = 3;
            double maxDistance = 150;

            // Calculate the sag, which decreases as distance increases.
            double sag = 0;

            if (distance < maxDistance) {
                // Sag is proportional to how close the distance is to the max distance.
                sag = Math.max(0, (maxDistance - distance) * 0.5);
            }

            // Optional: tweak sag formula based on testing
            double dx = p2.getX() - p1.getX();
            double dy = p2.getY() - p1.getY();

            // Set the control points for the cubic curve to simulate the rope's sag
            curve.setControlX1(p1.getX() + dx * 0.25);
            curve.setControlY1(p1.getY() + dy * 0.25 + sag);

            curve.setControlX2(p1.getX() + dx * 0.75);
            curve.setControlY2(p1.getY() + dy * 0.75 + sag);

        }, Duration.seconds(1.0 / 144));

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
    public static Sound getSfx_hover() {
        return sfx_hover;
    }

    public static Sound getSfx_splat() {
        return sfx_splat;
    }

    public static Sound getSfx_click() {
        return sfx_click;
    }

    public static void setSfx_hover(Sound sfx_hover) {
        MainApplication.sfx_hover = sfx_hover;
    }

    public static void setSfx_click(Sound sfx_click) {
        MainApplication.sfx_click = sfx_click;
    }
}
