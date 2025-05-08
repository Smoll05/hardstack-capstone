package com.example.joeandmarie.data.viewmodel;

import com.example.joeandmarie.data.dao.SettingPreferenceDao;
import com.example.joeandmarie.data.event.SettingPreferenceEvent;
import com.example.joeandmarie.data.model.SettingPreference;

import java.util.ArrayList;
import java.util.List;

public class SettingPreferenceViewModel implements Subject<SettingPreference> {
    private final SettingPreferenceDao dao = new SettingPreferenceDao();
    private final List<Observer<SettingPreference>> observers = new ArrayList<>();
    private SettingPreference state;

    @Override
    public void addObserver(Observer<SettingPreference> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<SettingPreference> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer<SettingPreference> observer : observers) {
            observer.update(state);
        }
    }

    public void setState(SettingPreference state) {
        this.state = state;
        notifyObservers();
    }

    public void saveSettingPreference() {
        dao.updateSettingPreference(state);
    }

    public void onEvent(SettingPreferenceEvent event, Object value) {
        try {
            switch (event) {
                case UPDATE_MUSIC_VOLUME:
                    state.setMusicVolume((Float) value);
                    break;
                case UPDATE_FX_VOLUME:
                    state.setFxVolume((Float) value);
                    break;
                case UPDATE_INFINITE_JUMP:
                    state.setInfiniteJump((Boolean) value);
                    break;
                case UPDATE_CLIMB_WALLS:
                    state.setClimbWalls((Boolean) value);
                    break;
                case UPDATE_INFINITE_GRIP:
                    state.setInfiniteGrip((Boolean) value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event type: " + event);
            }
        } catch(ClassCastException e) {
            throw new RuntimeException("Error on setting preference view model event", e);
        }

        notifyObservers();
    }
}
