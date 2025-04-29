package com.example.joeandmarie.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;


public class CheckpointComponent {

    private static Entity flagEntity; // Singleton tracking

    private final Point2D pos;
    private final AnimatedTexture texture;
    private final AnimationChannel animStart, animMove;

    public CheckpointComponent(Point2D pos) {
        this.pos = pos;

        animStart = new AnimationChannel(FXGL.image("flag_spritesheet.png"), 20, 64, 64, Duration.seconds(0.01), 0, 3);
        animMove = new AnimationChannel(FXGL.image("flag_spritesheet.png"), 20, 64, 64, Duration.seconds(1.5), 4, 15);

        texture = new AnimatedTexture(animStart);
    }

    public void plantFlag() {
        if (flagEntity == null) {
            // First time placing the flag
            flagEntity = FXGL.entityBuilder()
                    .at(pos.getX(), pos.getY())
                    .view(texture)
                    .zIndex(100)
                    .buildAndAttach();

            texture.playAnimationChannel(animStart);
            texture.setOnCycleFinished(() -> {
                texture.loopAnimationChannel(animMove);
            });

        } else {
            // Flag already exists, just move it
            flagEntity.setPosition(pos.getX(), pos.getY());
        }
    }

    public static void teleportToCheckpoint(Entity entity) {
        if (flagEntity != null) {
            entity.setPosition(flagEntity.getPosition());
        } else {
            System.out.println("No checkpoint set yet.");
        }
    }

    public static Entity getFlagEntity() {
        return flagEntity;
    }

    public Point2D getPosition() {
        return pos;
    }
}
