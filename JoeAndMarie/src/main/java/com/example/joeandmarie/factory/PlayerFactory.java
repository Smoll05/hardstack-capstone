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
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.component.Player1Component;
import com.example.joeandmarie.component.Player2Component;
import com.example.joeandmarie.entity.EntityType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PlayerFactory implements EntityFactory {

    @Spawns("player1")
    public Entity newPlayer1(SpawnData data) {

        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true); // Does not rotate the entity
        bd.setType(BodyType.DYNAMIC);

        PhysicsComponent physics = new PhysicsComponent();

        physics.setFixtureDef(new FixtureDef().friction(5f).density(0.25f));
        physics.setBodyDef(bd);

        HitBox groundSensor = new HitBox("GROUND_SENSOR", new Point2D(0, 64 - 5), BoundingShape.box(40, 10));

        physics.addGroundSensor(groundSensor);

        Rectangle debugBox = new Rectangle(40, 10, Color.RED);
        debugBox.setTranslateX(groundSensor.getBounds().getMinX());
        debugBox.setTranslateY(groundSensor.getBounds().getMinY());

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER1)
                .bbox(new HitBox("BODY", BoundingShape.box(40,64)))
                .with(new CollidableComponent(true))
                .view(debugBox)
                .with(new StateComponent())
                .with(physics)
                .with(new Player1Component())
                .build();
    }

    @Spawns("player2")
    public Entity newPlayer2(SpawnData data) {

        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true); // Does not rotate the entity
        bd.setType(BodyType.DYNAMIC);

        PhysicsComponent physics = new PhysicsComponent();

        physics.setFixtureDef(new FixtureDef().friction(5f).density(0.25f));
        physics.setBodyDef(bd);

        HitBox groundSensor = new HitBox("GROUND_SENSOR", new Point2D(0, 64 - 5), BoundingShape.box(40, 10));

        physics.addGroundSensor(groundSensor);

        Rectangle debugBox = new Rectangle(40, 10, Color.RED);
        debugBox.setTranslateX(groundSensor.getBounds().getMinX());
        debugBox.setTranslateY(groundSensor.getBounds().getMinY());

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER2)
                .bbox(new HitBox("BODY", BoundingShape.box(40,64)))
                .with(new CollidableComponent(true))
                .view(debugBox)
                .with(new StateComponent())
                .with(physics)
                .with(new Player2Component())
                .build();

    }
}
