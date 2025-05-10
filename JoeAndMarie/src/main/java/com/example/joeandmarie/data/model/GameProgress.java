package com.example.joeandmarie.data.model;

import java.io.Serializable;

public class GameProgress implements Serializable {
    private transient int gameProgressId;
    private transient int saveProgressId;
    private int heightProgress = 0;
    private float xCoordinate = 0.0f;
    private float yCoordinate = 0.0f;
    private int deepFallCount = 0;

    public GameProgress (int gameProgressId, int saveProgressId) {
        this.gameProgressId = gameProgressId;
        this.saveProgressId = saveProgressId;
    }

    public int getGameProgressId() {
        return gameProgressId;
    }

    public void setGameProgressId(int gameProgressId) {
        this.gameProgressId = gameProgressId;
    }

    public int getSaveProgressId() {
        return saveProgressId;
    }

    public void setSaveProgressId(int saveProgressId) {
        this.saveProgressId = saveProgressId;
    }

    public int getHeightProgress() {
        return heightProgress;
    }

    public void setHeightProgress(int heightProgress) {
        this.heightProgress = heightProgress;
    }

    public float getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(float xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public float getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(float yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public int getDeepFallCount() {
        return deepFallCount;
    }

    public void setDeepFallCount(int deepFallCount) {
        this.deepFallCount = deepFallCount;
    }

    public GameProgress readOnlyCopy(int gameProgressId, int saveProgressId) {
        GameProgress copy = new GameProgress(gameProgressId, saveProgressId);
        copy.setHeightProgress(this.heightProgress);
        copy.setXCoordinate(this.xCoordinate);
        copy.setYCoordinate(this.yCoordinate);
        copy.setDeepFallCount(this.deepFallCount);
        return copy;
    }
}
