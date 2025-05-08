package com.example.joeandmarie.data.model;

public class SettingPreference {
    private int settingPreferenceId = -1;
    private int saveProgressId = -1;
    private float musicVolume = 0;
    private float fxVolume = 0;
    private boolean isInfiniteJump = false;
    private boolean isClimbWalls = false;
    private boolean isInfiniteGrip = false;

    public int getSettingPreferenceId() {
        return settingPreferenceId;
    }

    public void setSettingPreferenceId(int settingPreferenceId) {
        this.settingPreferenceId = settingPreferenceId;
    }

    public int getSaveProgressId() {
        return saveProgressId;
    }

    public void setSaveProgressId(int saveProgressId) {
        this.saveProgressId = saveProgressId;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public float getFxVolume() {
        return fxVolume;
    }

    public void setFxVolume(float fxVolume) {
        this.fxVolume = fxVolume;
    }

    public boolean isInfiniteJump() {
        return isInfiniteJump;
    }

    public void setInfiniteJump(boolean infiniteJump) {
        isInfiniteJump = infiniteJump;
    }

    public boolean isClimbWalls() {
        return isClimbWalls;
    }

    public void setClimbWalls(boolean climbWalls) {
        isClimbWalls = climbWalls;
    }

    public boolean isInfiniteGrip() {
        return isInfiniteGrip;
    }

    public void setInfiniteGrip(boolean infiniteGrip) {
        isInfiniteGrip = infiniteGrip;
    }
}
