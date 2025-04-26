package com.example.joeandmarie.factory;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.joeandmarie.entity.EntityType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class PlatformerFactory implements EntityFactory {
//    @Spawns("platform")
//    public Entity newPlatform(SpawnData data) {
//        return FXGL.entityBuilder(data)
//                .type(EntityType.PLATFORM)
//                .bbox(new HitBox("BODY", BoundingShape.box(200, 20)))
//                .viewWithBBox(new Rectangle(100, 20, Color.DARKGRAY))
//                .with(new PhysicsComponent())
//                .build();
//    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return entityBuilder(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }
}

