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
import javafx.animation.FadeTransition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.EnumSet;

public class MainApplication extends GameApplication {
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
    private static Sound sfx_meow;
    private static Sound sfx_click;

    private static Music music_underground;

    private final GameProgressViewModel gameProgressViewModel = GameProgressViewModel.getInstance();

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
        settings.setFullScreenFromStart(true);

        settings.setSceneFactory(new SceneFactory() {

            @Override
            public IntroScene newIntro() {
                return new JoeIntroScene();
            }

            @Override
            public FXGLMenu newMainMenu() {
                return new JoeMainMenu();
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new JoeGameMenu();
            }
        });
    }


    @Override
    protected void initGame() {
        FXGL.getGameScene().setBackgroundColor(Color.BLACK);

        FXGL.getGameWorld().addEntityFactory(new PlatformerFactory());
        FXGL.getGameWorld().addEntityFactory(new PlayerFactory());
        FXGL.getGameWorld().addEntityFactory(new BlockFactory());

        sfx_meow = FXGL.getAssetLoader().loadSound("sound_meow.wav");
        sfx_cry = FXGL.getAssetLoader().loadSound("sound_back_checkpoint.wav");
        sfx_hover = FXGL.getAssetLoader().loadSound("sound_button_hover.wav");
        sfx_splat = FXGL.getAssetLoader().loadSound("sound_cry.wav");

        music_underground = FXGL.getAssetLoader().loadMusic("music_underground_city.mp3");

        FXGL.getAudioPlayer().stopMusic(JoeMainMenu.getMainMenuMusic());
        FXGL.getAudioPlayer().loopMusic(music_underground);

        try {
            FXGL.setLevelFromMap("ForestBottom.tmx");
        } catch (Exception e) {
            e.printStackTrace();
        }

        int mapWidth = 165 * 32;  // 4,800 pixels
        int mapHeight = 261 * 32; // 3,584 pixels

        FXGL.set("spawnPoint", FXGL.getGameWorld().getSingleton(EntityType.SPAWN_POINT));

        double x, y;
        Entity spawnPoint = FXGL.geto("spawnPoint");
        originY = spawnPoint.getY();

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER1, EntityType.Finish) {
            @Override
            protected void onCollisionBegin(Entity player, Entity finish) {
                showFinishImage(); // just shows image, no saving
            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER2, EntityType.Finish) {
            @Override
            protected void onCollisionBegin(Entity player, Entity finish) {
                showFinishImage(); // just shows image, no saving
            }
        });

        GameProgress snapshot = gameProgressViewModel.getSnapshot();

        if (snapshot.getHeightProgress() == 0) {
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

        DeepFallCountUi deepFallCountUi = new DeepFallCountUi();
        Node deepFallProgress = deepFallCountUi.getView();

        FXGL.addUINode(deepFallProgress);
        FXGL.addUINode(heightProgress);
        gameProgressViewModel.addObserver(heightProgressUi);
        gameProgressViewModel.addObserver(deepFallCountUi);
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

    private void showFinishImage() {
        Image image = FXGL.image("finish.png");
        ImageView imageView = new ImageView(image);

        // Set to full screen size
        double screenWidth = FXGL.getSettings().getWidth();
        double screenHeight = FXGL.getSettings().getHeight();
        imageView.setFitWidth(screenWidth);
        imageView.setFitHeight(screenHeight);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);

        // Set initial opacity to 0 (fully transparent)
        imageView.setOpacity(0);

        // Add to scene first
        FXGL.getGameScene().addUINode(imageView);

        // Create fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), imageView);
        fadeIn.setFromValue(0);   // Start transparent
        fadeIn.setToValue(1);     // End fully visible
        fadeIn.play();            // Start animation ivann gwapo h

        try {
            Music mainMenuMusic = FXGL.getAssetLoader().loadMusic("IntroMusic.mp3");
            FXGL.getAudioPlayer().loopMusic(mainMenuMusic);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static Sound getSfx_meow() {
        return sfx_meow;
    }

    public static Sound getSfx_click() {
        return sfx_click;
    }

    public static void setSfx_hover(Sound sfx_hover) {
        MainApplication.sfx_hover = sfx_hover;
    }

    public static void setSfx_meow(Sound sfx_meow) {
        MainApplication.sfx_meow = sfx_meow;
    }

    public static void setSfx_click(Sound sfx_click) {
        MainApplication.sfx_click = sfx_click;
    }
}

