package com.example.joeandmarie.data.viewmodel;

import com.example.joeandmarie.data.model.SettingPreference;

import java.util.ArrayList;
import java.util.List;

public class SettingPreferenceViewModel implements Subject<SettingPreference> {
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
}
