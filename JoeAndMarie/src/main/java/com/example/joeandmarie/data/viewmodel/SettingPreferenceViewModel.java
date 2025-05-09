package com.example.joeandmarie.data.viewmodel;

import com.example.joeandmarie.data.dao.SettingPreferenceDao;
import com.example.joeandmarie.data.event.SettingPreferenceEvent;
import com.example.joeandmarie.data.model.SettingPreference;

public class SettingPreferenceViewModel extends ViewModel<SettingPreference> {
    private static volatile SettingPreferenceViewModel instance;
    private final SettingPreferenceDao dao = new SettingPreferenceDao();

    private SettingPreferenceViewModel() { }

    public static SettingPreferenceViewModel getInstance() {
        if(instance == null) {
            synchronized (SettingPreferenceViewModel.class) {
                if (instance == null) {
                    instance = new SettingPreferenceViewModel();
                }
            }
        }

        return instance;
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

    public SettingPreference getSnapshot() {
        return state.readOnlyCopy();
    }
}
