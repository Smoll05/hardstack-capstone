package com.example.joeandmarie.data.model;

public class SettingPreference {
    private final int settingPreferenceId = 1;
    private float musicVolume = 50f;
    private float fxVolume = 50f;
    private boolean isInfiniteJump = false;
    private boolean isClimbWalls = false;
    private boolean isInfiniteGrip = false;

    public int getSettingPreferenceId() {
        return settingPreferenceId;
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

    public SettingPreference readOnlyCopy() {
        SettingPreference copy = new SettingPreference();
        copy.setMusicVolume(this.musicVolume);
        copy.setFxVolume(this.fxVolume);
        copy.setInfiniteJump(this.isInfiniteJump);
        copy.setClimbWalls(this.isClimbWalls);
        copy.setInfiniteGrip(this.isInfiniteGrip);
        return copy;
    }
}
