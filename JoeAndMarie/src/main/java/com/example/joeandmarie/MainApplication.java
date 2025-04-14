package com.example.joeandmarie;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MainApplication extends GameApplication {
    private Entity player1, player2;
    Text nameTag1, nameTag2;


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
//        player1 = FXGL.entityBuilder().at(300, 300)
//                .viewWithBBox("meow_jm.png")
//                .type(EntityType.PLAYER1)
//                .with(new CollidableComponent(true))
//                .buildAndAttach();
//
//        player2 = FXGL.entityBuilder().at(300, 300)
//                .viewWithBBox("meow_jm.png")
//                .type(EntityType.PLAYER2)
//                .with(new CollidableComponent(true))
//                .buildAndAttach();

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
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER1, EntityType.PLAYER2) {
            @Override
            protected void onCollisionBegin(Entity player1, Entity player2) {
                FXGL.play("oof.wav");
                System.out.println("OOF");

                getPlayer1().getViewComponent().clearChildren();
                getPlayer1().getViewComponent().addChild(FXGL.texture("hug1.png"));

                getPlayer2().getViewComponent().clearChildren();
                getPlayer2().getViewComponent().addChild(FXGL.texture("hug2.png"));

                getPlayer1().getViewComponent().addChild(nameTag1);
                getPlayer2().getViewComponent().addChild(nameTag2);
            }

            @Override
            protected void onCollisionEnd(Entity player1, Entity player2) {
                getPlayer1().getViewComponent().clearChildren();
                getPlayer1().getViewComponent().addChild(FXGL.texture("meow_1.png"));


                getPlayer2().getViewComponent().clearChildren();
                getPlayer2().getViewComponent().addChild(FXGL.texture("meow_2.png"));

                getPlayer1().getViewComponent().addChild(nameTag1);
                getPlayer2().getViewComponent().addChild(nameTag2);
            }
        } );
    }

    @Override
    protected void initInput() {
        FXGL.onKey(KeyCode.A, () -> getPlayer1().getComponent(PhysicsComponent.class).setVelocityX(-150));
        FXGL.onKey(KeyCode.D, () -> getPlayer1().getComponent(PhysicsComponent.class).setVelocityX(150));
        FXGL.onKeyDown(KeyCode.W, () -> {
            PhysicsComponent physics = getPlayer1().getComponent(PhysicsComponent.class);
//            if (physics.isOnGround()) {
//
//            }
            physics.setVelocityY(-400); // Jump
        });

//        FXGL.onKeyUp(KeyCode.A, () -> getPlayer1().getComponent(PhysicsComponent.class).setVelocityX(0));
//        FXGL.onKeyUp(KeyCode.D, () -> getPlayer1().getComponent(PhysicsComponent.class).setVelocityX(0));

        FXGL.onKey(KeyCode.LEFT, () -> getPlayer2().getComponent(PhysicsComponent.class).setVelocityX(-150));
        FXGL.onKey(KeyCode.RIGHT, () -> getPlayer2().getComponent(PhysicsComponent.class).setVelocityX(150));
        FXGL.onKeyDown(KeyCode.UP, () -> {
            PhysicsComponent physics = getPlayer2().getComponent(PhysicsComponent.class);
//            if (physics.isOnGround()) {
//                physics.setVelocityY(-400); // Jump
//            }
            physics.setVelocityY(-400); // Jump
        });

//        FXGL.onKeyUp(KeyCode.LEFT, () -> getPlayer2().getComponent(PhysicsComponent.class).setVelocityX(0));
//        FXGL.onKeyUp(KeyCode.RIGHT, () -> getPlayer2().getComponent(PhysicsComponent.class).setVelocityX(0));
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
