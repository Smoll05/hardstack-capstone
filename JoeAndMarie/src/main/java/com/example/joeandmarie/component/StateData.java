package com.example.joeandmarie.component;

import com.almasb.fxgl.texture.AnimationChannel;

public class StateData {
    protected AnimationChannel channel;
    protected int moveSpeed;

    public StateData(AnimationChannel channel, int moveSpeed) {
        this.channel = channel;
        this.moveSpeed = moveSpeed;
    }
}
