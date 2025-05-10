package com.example.joeandmarie.data.viewmodel;

import com.example.joeandmarie.data.dao.GameProgressDao;
import com.example.joeandmarie.data.event.GameProgressEvent;
import com.example.joeandmarie.data.model.GameProgress;

public class GameProgressViewModel extends ViewModel<GameProgress> {
    private static volatile GameProgressViewModel instance;
    private final GameProgressDao dao = new GameProgressDao();

    private GameProgressViewModel() { }

    public static GameProgressViewModel getInstance() {
        if(instance == null) {
            synchronized (GameProgressViewModel.class) {
                if (instance == null) {
                    instance = new GameProgressViewModel();
                }
            }
        }

        return instance;
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
                    state.setDeepFallCount(state.getDeepFallCount() + (int) value);
                    dao.updateDeepFallCount(state);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event type: " + event);
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Error on game progress view model event", e);
        }

        notifyObservers();
    }

    public GameProgress getSnapshot() {
        return state.readOnlyCopy(state.getGameProgressId(), state.getSaveProgressId());
    }
}