package com.example.joeandmarie.data.viewmodel;

public interface Observer<T> {
    public void update(T newState);
}
