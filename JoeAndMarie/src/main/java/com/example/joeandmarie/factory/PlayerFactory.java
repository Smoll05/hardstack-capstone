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
import com.almasb.fxgl.physics.SensorCollisionHandler;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.joeandmarie.component.Player1Component;
import com.example.joeandmarie.component.Player2Component;
import com.example.joeandmarie.entity.EntityType;
import javafx.geometry.Point2D;
import javafx.scene.Group;
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

        HitBox groundSensor = new HitBox("GROUND_SENSOR", new Point2D(10, 64 - 5), BoundingShape.box(25, 10));
        HitBox wallSensor = new HitBox("WALL_SENSOR", new Point2D(0,  20), BoundingShape.box(10, 40));

        SensorCollisionHandler wallHandler = new SensorCollisionHandler() {
            @Override
            protected void onCollisionBegin(Entity other) {
                super.onCollisionBegin(other);
                System.out.println("touching wall");
                Player1Component.isTouchingWall = true;

            }

            @Override
            protected void onCollision(Entity other) {
                super.onCollision(other);
            }

            @Override
            protected void onCollisionEnd(Entity other) {
                super.onCollisionEnd(other);
                Player1Component.isTouchingWall = false;
            }
        };

        physics.addGroundSensor(groundSensor);
        physics.addSensor(wallSensor, wallHandler);

        Rectangle debugBox = new Rectangle(25, 10, Color.RED);
        debugBox.setTranslateX(groundSensor.getBounds().getMinX());
        debugBox.setTranslateY(groundSensor.getBounds().getMinY());

        Rectangle debugWallBox= new Rectangle(10, 40, Color.BLUE);
        debugWallBox.setTranslateX(wallSensor.getBounds().getMinX());
        debugWallBox.setTranslateY(wallSensor.getBounds().getMinY());

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER1)
                .bbox(new HitBox("BODY", new Point2D(2, 23), BoundingShape.circle(20)))
                .with(new CollidableComponent(true))
                .view(new Group(debugBox, debugWallBox))
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

        HitBox groundSensor = new HitBox("GROUND_SENSOR", new Point2D(10, 64 - 5), BoundingShape.box(25, 10));
        HitBox wallSensor = new HitBox("WALL_SENSOR", new Point2D(0,  20), BoundingShape.box(10, 40));

        SensorCollisionHandler wallHandler = new SensorCollisionHandler() {
            @Override
            protected void onCollisionBegin(Entity other) {
                super.onCollisionBegin(other);
                System.out.println("touching wall");
                Player2Component.isTouchingWall = true;

            }

            @Override
            protected void onCollision(Entity other) {
                super.onCollision(other);
            }

            @Override
            protected void onCollisionEnd(Entity other) {
                super.onCollisionEnd(other);
                Player2Component.isTouchingWall = false;
            }
        };

        physics.addGroundSensor(groundSensor);
        physics.addSensor(wallSensor, wallHandler);

        Rectangle debugBox = new Rectangle(25, 10, Color.RED);
        debugBox.setTranslateX(groundSensor.getBounds().getMinX());
        debugBox.setTranslateY(groundSensor.getBounds().getMinY());

        Rectangle debugWallBox= new Rectangle(10, 40, Color.BLUE);
        debugWallBox.setTranslateX(wallSensor.getBounds().getMinX());
        debugWallBox.setTranslateY(wallSensor.getBounds().getMinY());

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER2)
                .bbox(new HitBox("BODY", new Point2D(2, 23), BoundingShape.circle(20)))
                .with(new CollidableComponent(true))
                .view(new Group(debugBox, debugWallBox))
                .with(new StateComponent())
                .with(physics)
                .with(new Player2Component())
                .build();

    }
}
