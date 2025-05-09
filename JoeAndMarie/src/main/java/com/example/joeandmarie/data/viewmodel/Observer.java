package com.example.joeandmarie.data.viewmodel;

public interface Observer<T> {
    void update(T newState);
}
