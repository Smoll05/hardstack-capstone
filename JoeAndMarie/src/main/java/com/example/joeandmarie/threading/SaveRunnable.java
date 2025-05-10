package com.example.joeandmarie.threading;

import com.example.joeandmarie.data.viewmodel.GameProgressViewModel;

public class SaveRunnable implements Runnable{
    private final GameProgressViewModel viewModel = GameProgressViewModel.getInstance();

    @Override
    public void run() {
        viewModel.saveGameProgress();
    }
}
