package com.example.joeandmarie.factory;

import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.joeandmarie.entity.EntityType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class PlatformerFactory implements EntityFactory {
    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return entityBuilder(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("one_way_platform")
    public Entity newOneWayPlatform(SpawnData data) {
        // Create the one-way platform entity
        return entityBuilder(data)
                .type(EntityType.ONE_WAY_PLATFORM)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("spawn")
    public Entity spawn(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.SPAWN_POINT)
                .bbox(new HitBox(BoundingShape.box(
                        data.<Integer>get("width"),
                        data.<Integer>get("height")
                )))
                .build();
    }


}

