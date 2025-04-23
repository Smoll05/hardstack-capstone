package com.example.joeandmarie.factory;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.DistanceJoint;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.component.PlayerComponent;
import com.example.joeandmarie.entity.EntityType;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.lang.reflect.Field;

public class PlayerFactory implements EntityFactory {

    @Spawns("player1")
    public Entity newPlayer1(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.friction(5.0f);

        physics.setFixtureDef(fd);

        // Create animations for idle, move, and crouch
        AnimationChannel idle = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 0, 7);
        AnimationChannel move = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.5), 8, 13);
        AnimationChannel crouch = new AnimationChannel(FXGL.image("joe_spritesheet_upscaled.png"), 8, 64, 64, Duration.seconds(0.75), 16, 23);

        // Initialize the AnimatedTexture with idle animation
        AnimatedTexture anim = new AnimatedTexture(idle);

        // Pass animations into PlayerComponent
//        PlayerComponent playerComponent = new PlayerComponent(anim, idle, move, crouch);

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER1)
                .bbox(new HitBox("BODY", BoundingShape.box(64,64)))
                .viewWithBBox(anim)
                .with(physics)
                .with(new StateComponent())
                .with(new CollidableComponent(true))
//                .with(playerComponent)
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

//    private AnimatedTexture createPlayerAnimation() {
//        Image image = FXGL.image("joe_spritesheet.png");
//
//        // Create animations
//        AnimationChannel idle = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 0, 7);     // row 0
//        AnimationChannel move = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 8, 15);    // row 1
//        AnimationChannel crouch = new AnimationChannel(image, 32, 32, 32, Duration.seconds(1), 16, 23); // row 2
//
//        // Initialize texture with the idle animation
//        AnimatedTexture texture = new AnimatedTexture(idle);
//        texture.loopAnimationChannel(idle);
//
//        return texture;
//    }

    @Spawns("player2")
    public Entity newPlayer2(SpawnData data) {
        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true); // Does not rotate the entity
        bd.setType(BodyType.DYNAMIC);

        PhysicsComponent physics = new PhysicsComponent();

        physics.setFixtureDef(new FixtureDef().friction(0).density(0.25f));
        physics.setBodyDef(bd);

        // Add ground sensor
        // Create the sensor HitBox
        HitBox groundSensor = new HitBox("GROUND_SENSOR", new Point2D(0, 64 - 5), BoundingShape.box(40, 10));

        physics.addGroundSensor(groundSensor);

        Rectangle debugBox = new Rectangle(40, 10, Color.RED);
        debugBox.setTranslateX(groundSensor.getBounds().getMinX());
        debugBox.setTranslateY(groundSensor.getBounds().getMinY());

        return FXGL.entityBuilder(data)
                .from(data)
                .type(EntityType.PLAYER2)
                .bbox(new HitBox("BODY", BoundingShape.box(40,64)))
//                .with(new CollidableComponent(true))
                .collidable()
                .view(debugBox)
                .with(new StateComponent())
                .with(physics)
                .with(new PlayerComponent())
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
}
