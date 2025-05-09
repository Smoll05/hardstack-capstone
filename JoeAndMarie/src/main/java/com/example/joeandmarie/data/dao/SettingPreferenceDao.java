package com.example.joeandmarie.data.dao;

import com.example.joeandmarie.data.database.ConnectionPool;
import com.example.joeandmarie.data.model.SettingPreference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingPreferenceDao {

    public void insertSettingPreference(SettingPreference state) {
        String sql = """
            INSERT INTO tbSettingPreference (
                setting_preference_id,
                music_volume,
                fx_volume,
                infinite_jump,
                climb_walls,
                infinite_grip,            
            ) VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, state.getSettingPreferenceId());
            stmt.setFloat(2, state.getMusicVolume());
            stmt.setFloat(3, state.getFxVolume());
            stmt.setInt(4, state.isInfiniteJump() ? 1 : 0);
            stmt.setInt(5, state.isClimbWalls() ? 1 : 0);
            stmt.setInt(6, state.isInfiniteGrip() ? 1 : 0);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("SettingPreference successfully inserted ");
            } else {
                System.out.println("No rows affected during insert of SettingPreference ");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting SettingPreference", e);
        }
    }

    public SettingPreference selectSettingPreference(int settingPreferenceId) {
        String sql = "SELECT * FROM tbSettingPreference WHERE setting_preference_id = ?";

        SettingPreference currentState = null;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, settingPreferenceId);

            try (ResultSet resultSet = stmt.executeQuery()) {

                if (resultSet.next()) {
                    currentState = new SettingPreference();
                    currentState.setMusicVolume(resultSet.getFloat("music_volume"));
                    currentState.setFxVolume(resultSet.getFloat("fx_volume"));
                    currentState.setInfiniteJump(resultSet.getFloat("infinite_jump") == 1);
                    currentState.setClimbWalls(resultSet.getInt("climb_walls") == 1);
                    currentState.setInfiniteGrip(resultSet.getInt("infinite_grip") == 1);

                    System.out.println("SettingPreference with setting_progress_id: " + settingPreferenceId + " found");
                } else {
                    System.out.println("No SettingPreference found with setting_progress_id: " + settingPreferenceId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error selecting SettingPreference", e);
        }

        return currentState;
    }

    public void updateSettingPreference(SettingPreference state) {
        String sql = """
            UPDATE tbSettingPreference
            SET music_volume = ?, fx_volume = ?,
                infinite_jump = ?, climb_walls = ?,
                infinite_grip = ?
            WHERE setting_preference_id = ?
        """;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, state.getMusicVolume());
            stmt.setFloat(2, state.getFxVolume());
            stmt.setInt(3, state.isInfiniteJump() ? 1 : 0);
            stmt.setInt(4, state.isClimbWalls() ? 1 : 0);
            stmt.setInt(5, state.isInfiniteGrip() ? 1 : 0);
            stmt.setInt(5, state.getSettingPreferenceId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("SettingPreference successfully updated");
            } else {
                System.out.println("No rows affected during update of SettingPreference ");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating SettingPreference", e);
        }
    }

    public void deleteSettingPreference(int settingPreferenceId) {
        String sql = "DELETE FROM tbSettingPreference WHERE setting_preference_id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, settingPreferenceId);

            if(stmt.executeUpdate() > 0) {
                System.out.println("Successfully deleted setting preference with id " + settingPreferenceId);
            } else {
                System.out.println("Failed to delete setting preference with id " + settingPreferenceId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting setting preference", e);
        }
    }
}
