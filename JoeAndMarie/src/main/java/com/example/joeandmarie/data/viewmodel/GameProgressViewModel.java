package com.example.joeandmarie.data.viewmodel;

import com.example.joeandmarie.data.model.GameProgress;
import com.example.joeandmarie.data.model.SettingPreference;

import java.util.ArrayList;
import java.util.List;

public class GameProgressViewModel implements Subject<GameProgress> {
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
}
