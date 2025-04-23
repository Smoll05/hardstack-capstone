package com.example.joeandmarie.state;

import com.almasb.fxgl.entity.component.Component;

public class PlayerStateComponent extends Component {
    private PlayerState currentState = PlayerState.IDLE;

    public PlayerState getState() {
        return currentState;
    }

    public void setState(PlayerState newState) {
        this.currentState = newState;
    }

    public boolean is(PlayerState state) {
        return currentState == state;
    }
}
