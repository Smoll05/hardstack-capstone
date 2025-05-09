package com.example.joeandmarie.data.viewmodel;

import com.example.joeandmarie.data.event.GameProgressEvent;
import com.example.joeandmarie.data.model.GameProgress;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewModel<T> implements Subject<T> {

    private final List<Observer<T>> observers = new ArrayList<>();
    protected T state;

    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer<T> observer : observers) {
            observer.update(state);
        }
    }

    public void setState(T state) {
        this.state = state;
        notifyObservers();
    }

    public void clearState() {
        this.state = null;
    }

}
