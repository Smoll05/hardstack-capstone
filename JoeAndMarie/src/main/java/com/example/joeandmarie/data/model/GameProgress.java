package com.example.joeandmarie.data.model;

import java.io.Serializable;

public class GameProgress implements Serializable {
    private transient int gameProgressId = -1;
    private transient int saveProgressId = -1;
    private int heightProgress = 0;
    private float xCoordinate = 0.0f;
    private float yCoordinate = 0.0f;
    private int deepFallCount = 0;

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
}
