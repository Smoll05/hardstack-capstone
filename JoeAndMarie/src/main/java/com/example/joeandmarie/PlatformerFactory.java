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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PlatformerFactory implements EntityFactory {

    @Spawns("player1")
    public Entity newPlayer1(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().friction(0.3f));

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER1)
                .bbox(new HitBox("BODY", BoundingShape.box(40, 40)))
                .viewWithBBox("meow_2.png")
                .with(physics)
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("player2")
    public Entity newPlayer2(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().friction(0.3f));

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER2)
                .bbox(new HitBox("BODY", BoundingShape.box(40, 40)))
                .viewWithBBox("meow_1.png")
                .with(physics)
                .with(new CollidableComponent(true))
                .build();
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

