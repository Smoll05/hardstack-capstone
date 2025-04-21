package com.example.joeandmarie;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
//        settings.setFullScreenAllowed(true);
//        settings.setFullScreenFromStart(true);
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
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

            nameTag2.setTranslateX(-nameTag1.getLayoutBounds().getWidth() / 2);
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
//        FXGL.onKeyDown(KeyCode.S, () -> {
//            pc.crouch();
//        });
//
//        if (!moving[0]) {
//            physics.setVelocityX(0);
//            pc.idle();
//        }
//    }



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
