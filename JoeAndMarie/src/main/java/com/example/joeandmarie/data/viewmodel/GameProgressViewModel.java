package com.example.joeandmarie.data.viewmodel;

import com.example.joeandmarie.data.dao.GameProgressDao;
import com.example.joeandmarie.data.event.GameProgressEvent;
import com.example.joeandmarie.data.model.GameProgress;

import java.util.ArrayList;
import java.util.List;

public class GameProgressViewModel implements Subject<GameProgress> {
    private final GameProgressDao dao = new GameProgressDao();
    private final List<Observer<GameProgress>> observers = new ArrayList<>();
    private GameProgress state;

    @Override
    public void addObserver(Observer<GameProgress> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<GameProgress> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer<GameProgress> observer : observers) {
            observer.update(state);
        }
    }

    public void setState(GameProgress state) {
        this.state = state;
        notifyObservers();
    }

    public void saveGameProgress() {
        dao.updateGameProgress(state);
    }

    public void onEvent(GameProgressEvent event, Object value) {
        try {
            switch (event) {
                case UPDATE_HEIGHT:
                    state.setHeightProgress((int) value);
                    break;
                case UPDATE_X_COORDINATE:
                    state.setXCoordinate((Float) value);
                    break;
                case UPDATE_Y_COORDINATE:
                    state.setYCoordinate((Float) value);
                    break;
                case UPDATE_DEEP_FALL_COUNT:
                    state.setDeepFallCount((int) value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event type: " + event);
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Error on game progress view model event", e);
        }

        notifyObservers();
    }
}