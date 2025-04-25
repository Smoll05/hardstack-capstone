package com.example.joeandmarie;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.EnumSet;


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


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Joe and Marie");
        settings.setVersion("1.0");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setFullScreenAllowed(true);
        settings.setMainMenuEnabled(true);
        settings.setIntroEnabled(false);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));

        // Attach your custom menu
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new JoeMainMenu();
            }
        });
    }

    @Override

    public void initGame() {
        // Remove home UI
        FXGL.getGameScene().clearUINodes();

        // Initialize the actual game content
        FXGL.getGameWorld().addEntityFactory(new PlatformerFactory());

        FXGL.spawn("player1", 300, 300);
        FXGL.spawn("player2", 400, 0);

        FXGL.spawn("platform", 0, 500);
        FXGL.spawn("platform", 200, 400);
        FXGL.spawn("platform", 400, 300);
        FXGL.spawn("platform", 600, 200);

        FXGL.spawn("platform", 0, 700);
        FXGL.spawn("platform", 200, 700);
        FXGL.spawn("platform", 400, 700);
        FXGL.spawn("platform", 600, 700);
        FXGL.spawn("platform", 800, 700);
        FXGL.spawn("platform", 1000, 700);

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

    @Override
    protected void initPhysics() {
    }

    private boolean inputInitializedJoe = false;
    private boolean inputInitializedMarie = false;

    @Override
    protected void onUpdate(double tpf) {
        var player = getPlayer1();
        var pc = player.getComponent(PlayerComponent.class);
        var physics = player.getComponent(PhysicsComponent.class);

        var player2 = getPlayer2();
        var pc2 = player2.getComponent(PlayerComponent.class);
        var physics2 = player2.getComponent(PhysicsComponent.class);

        long currentTime = System.currentTimeMillis();

        if (!inputInitializedJoe && FXGL.getGameWorld().getEntitiesByType(EntityType.PLAYER1).size() > 0 && !inputInitializedMarie && FXGL.getGameWorld().getEntitiesByType(EntityType.PLAYER2).size() > 0) {
            setupInput();
            inputInitializedJoe = true;
            inputInitializedMarie = true;
        }

        if (inputInitializedJoe) {
            if (physics.getVelocityX() == 0 && currentTime - lastMoveTimeJoe > idleDelayJoe) {
                pc.idle();
            }
        }

        if (inputInitializedMarie) {
            if (physics2.getVelocityX() == 0 && currentTime - lastMoveTimeMarie > idleDelayMarie) {
                pc2.idle();
            }
        }
    }

    private void setupInput() {
        var input = FXGL.getInput();
        var player = getPlayer1();
        var pc = player.getComponent(PlayerComponent.class);
        var physics = player.getComponent(PhysicsComponent.class);
        var player2 = getPlayer2();
        var pc2 = player2.getComponent(PlayerComponent.class);
        var physics2 = player2.getComponent(PhysicsComponent.class);

        FXGL.onKeyDown(KeyCode.W, () -> {
//            Checking if on the ground
            if (Math.abs(physics.getVelocityY()) < 0.1) {
                physics.setVelocityY(-400);
                lastMoveTimeJoe = System.currentTimeMillis();
            }
        });

        FXGL.onKey(KeyCode.A, () -> {
            physics.setVelocityX(-150);
            pc.moveLeft();
            lastMoveTimeJoe = System.currentTimeMillis();
        });

        FXGL.onKey(KeyCode.D, () -> {
            physics.setVelocityX(150);
            pc.moveRight();
            lastMoveTimeJoe = System.currentTimeMillis();
        });

        FXGL.onKey(KeyCode.S, () -> {
            if (Math.abs(physics.getVelocityY()) < 0.1) {
                pc.crouch();
                physics.setVelocityX(0);
                lastMoveTimeJoe = System.currentTimeMillis();
            }
        });

        FXGL.onKeyDown(KeyCode.UP, () -> {
//            Checking if on the ground
            if (Math.abs(physics2.getVelocityY()) < 0.1) {
                physics2.setVelocityY(-400);
                lastMoveTimeMarie = System.currentTimeMillis();
            }
        });

        FXGL.onKey(KeyCode.LEFT, () -> {
            physics2.setVelocityX(-150);
            pc2.moveLeft();
            lastMoveTimeMarie = System.currentTimeMillis();
        });

        FXGL.onKey(KeyCode.RIGHT, () -> {
            physics2.setVelocityX(150);
            pc2.moveRight();
            lastMoveTimeMarie = System.currentTimeMillis();
        });

        FXGL.onKey(KeyCode.DOWN, () -> {
            if (Math.abs(physics2.getVelocityY()) < 0.1) {
                pc2.crouch();
                physics2.setVelocityX(0);
                lastMoveTimeMarie = System.currentTimeMillis();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Entity getPlayer1() {
        return FXGL.getGameWorld().getSingleton(EntityType.PLAYER1);
    }

    private Entity getPlayer2() {
        return FXGL.getGameWorld().getSingleton(EntityType.PLAYER2);
    }

}

//    public void start(Stage stage) throws Exception{
//        stage.setScene(new Scene(createContent()));
//        stage.show();
//    }
//    private Parent createContent(){
//        Pane root = new Pane();
//        root.setPrefSize(1280, 720);
//
//        Image bgImage = new Image(getClass().getResource("/assets/textures/bgimages.png").toExternalForm(), 1280, 720, false, true);
//
//        VBox box = new VBox(10,
//                new MenuItems("CO-OP Campaign", () -> {}),
//                new MenuItems("Multiplayer", () -> {}),
//                new MenuItems("Collection", () -> {}),
//                new MenuItems("Settings", () -> {}),
//                new MenuItems("Quit", () -> Platform.exit())
//        );
//
//        box.setBackground(new Background(
//                new BackgroundFill(Color.web("black", 0.6) , null, null)
//        ));
//
//        box.setTranslateX(1280 - 300);
//        box.setTranslateY(720 - 300);
//
//        root.getChildren().addAll(new ImageView(bgImage), box);
//        return  root;
//    }
//
//    private static class MenuItems extends StackPane {
//        MenuItems(String names, Runnable action) {
//            LinearGradient gradient = new LinearGradient(0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE,
//                    new Stop(0.1, Color.web("white", 0.5)),
//                    new Stop(0.1, Color.web("black", 0.5)));
//
//            Rectangle bg = new Rectangle(250, 30, gradient);
//            Rectangle bg1 = new Rectangle(250, 30, Color.web("black", 0.2)); // Corrected color
//            FillTransition ft = new FillTransition(Duration.seconds(1), bg1, Color.web("black", 0.2), Color.web("white", 0.2)); // Corrected color
//
//            ft.setAutoReverse(true);
//            ft.setCycleCount(Integer.MAX_VALUE);
//
//            hoverProperty().addListener((o, oldValue, isHovering) -> {
//                if (isHovering) {
//                    ft.playFromStart();
//                } else {
//                    ft.stop();
//                    bg1.setFill(Color.web("black", 0.2)); // Reset to original color
//                }
//            });
//
//            Rectangle line = new Rectangle(2, 30);
//            line.widthProperty().bind(
//                    Bindings.when(hoverProperty()).then(8).otherwise(5)
//            );
//            line.fillProperty().bind(
//                    Bindings.when(hoverProperty()).then(Color.RED).otherwise(Color.GRAY)
//            );
//
//            Text text = new Text(names);
//            text.fillProperty().bind(
//                    Bindings.when(hoverProperty()).then(Color.WHITE).otherwise(Color.GRAY)
//            );
//
//            setOnMousePressed(e -> bg.setFill(Color.LIGHTBLUE));
//            setOnMouseReleased(e -> bg.setFill(gradient));
//            setOnMouseClicked(e -> action.run());
//
//            setAlignment(Pos.CENTER_LEFT);
//
//            HBox box = new HBox(15, line, text);
//            box.setAlignment(Pos.CENTER_LEFT);
//            getChildren().addAll(bg, bg1, box);
//        }
//    }
//
//    public static void main(String[] args){
//        launch(args);
//    }

