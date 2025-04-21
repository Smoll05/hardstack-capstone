package com.example.joeandmarie;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PlatformerFactory implements EntityFactory {

    @Spawns("player1")
    public Entity newPlayer1(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().friction(0.3f));

        // Create animations for idle, move, and crouch
        AnimationChannel idle = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        AnimationChannel move = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        AnimationChannel crouch = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 16, 23);

        // Initialize the AnimatedTexture with idle animation
        AnimatedTexture anim = new AnimatedTexture(idle);

        // Pass animations into PlayerComponent
        PlayerComponent playerComponent = new PlayerComponent(anim, idle, move, crouch);

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER1)
                .bbox(new HitBox("BODY", BoundingShape.box(64,64)))
                .viewWithBBox(anim)
                .with(physics)
                .with(new CollidableComponent(true))
                .with(playerComponent)
                .build();

//
//        AnimatedTexture anim = createPlayerAnimation();
//        return FXGL.entityBuilder(data)
//                .type(EntityType.PLAYER1)
//                .bbox(new HitBox("BODY", BoundingShape.box(32, 32)))
//                .viewWithBBox(anim)
//                .with(physics)
//                .with(new CollidableComponent(true))
//                .with(new PlayerComponent(anim))
//                .build();
    }

    private AnimatedTexture createPlayerAnimation() {
        Image image = FXGL.image("joe_spritesheet.png");

        // Create animations
        AnimationChannel idle = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 0, 7);     // row 0
        AnimationChannel move = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 8, 15);    // row 1
        AnimationChannel crouch = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 16, 23); // row 2

        // Initialize texture with the idle animation
        AnimatedTexture texture = new AnimatedTexture(idle);
        texture.loopAnimationChannel(idle);

        return texture;
    }

    @Spawns("player2")
    public Entity newPlayer2(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().friction(0.3f));

        // Create animations for idle, move, and crouch
        AnimationChannel idle = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        AnimationChannel move = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        AnimationChannel crouch = new AnimationChannel(FXGL.image("marie_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 16, 23);

        // Initialize the AnimatedTexture with idle animation
        AnimatedTexture anim = new AnimatedTexture(idle);

        // Pass animations into PlayerComponent
        PlayerComponent playerComponent = new PlayerComponent(anim, idle, move, crouch);

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER2)
                .bbox(new HitBox("BODY", BoundingShape.box(64,64)))
                .viewWithBBox(anim)
                .with(physics)
                .with(new CollidableComponent(true))
                .with(playerComponent)
                .build();

//        PhysicsComponent physics = new PhysicsComponent();
//        physics.setBodyType(BodyType.DYNAMIC);
//        physics.setFixtureDef(new FixtureDef().friction(0.3f));
//
//
//        return FXGL.entityBuilder(data)
//                .type(EntityType.PLAYER2)
//                .bbox(new HitBox("BODY", BoundingShape.box(40, 40)))
//                .viewWithBBox("meow_1.png")
//                .with(physics)
//                .with(new CollidableComponent(true))
//                .build();
    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.PLATFORM)
                .bbox(new HitBox("BODY", BoundingShape.box(200, 20)))
                .viewWithBBox(new Rectangle(200, 20, Color.DARKGRAY))
                .with(new PhysicsComponent()) // Default STATIC
                .build();
    }
}

