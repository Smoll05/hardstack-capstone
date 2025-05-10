package com.example.joeandmarie.data.dao;

import com.example.joeandmarie.data.database.ConnectionPool;
import com.example.joeandmarie.data.model.GameProgress;

import java.sql.*;

public class GameProgressDao {

    public void insertGameProgress(GameProgress state) {
        String sql = """
            INSERT INTO tbGameProgress (
                game_progress_id,      
                save_progress_id,
                height_progress,
                x_coordinate,
                y_coordinate,
                deepfall_count
            ) VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, state.getGameProgressId());
            stmt.setInt(2, state.getSaveProgressId());
            stmt.setInt(3, state.getHeightProgress());
            stmt.setFloat(4, state.getXCoordinate());
            stmt.setFloat(5, state.getYCoordinate());
            stmt.setInt(6, state.getDeepFallCount());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("GameProgress successfully inserted ");
            } else {
                System.out.println("No rows affected during insert of GameProgress ");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting GameProgress", e);
        }
    }

    public GameProgress selectGameProgress(int gameProgressId) {
        String sql = "SELECT * FROM tbGameProgress WHERE game_progress_id = ?";

        GameProgress currentState = null;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameProgressId);

            try (ResultSet resultSet = stmt.executeQuery()) {

                if (resultSet.next()) {
                    int saveProgressId = resultSet.getInt("save_progress_id");
                    currentState = new GameProgress(gameProgressId, saveProgressId);
                    currentState.setHeightProgress(resultSet.getInt("height_progress"));
                    currentState.setXCoordinate(resultSet.getFloat("x_coordinate"));
                    currentState.setYCoordinate(resultSet.getFloat("y_coordinate"));
                    currentState.setDeepFallCount(resultSet.getInt("deepfall_count"));

                    System.out.println("GameProgress with game_progress_id: " + gameProgressId + " found");
                } else {
                    System.out.println("No GameProgress found with game_progress_id: " + gameProgressId);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error selecting GameProgress", e);
        }

        return currentState;
    }

    public void updateGameProgress(GameProgress state) {
        String sql = """
            UPDATE tbGameProgress
            SET height_progress = ?, x_coordinate = ?,
                y_coordinate = ?, deepfall_count = ?
            WHERE game_progress_id = ?
        """;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, state.getHeightProgress());
            stmt.setFloat(2, state.getXCoordinate());
            stmt.setFloat(3, state.getYCoordinate());
            stmt.setFloat(4, state.getDeepFallCount());
            stmt.setInt(5, state.getGameProgressId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("GameProgress successfully updated");
            } else {
                System.out.println("No rows affected during update of GameProgress ");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating GameProgress", e);
        }
    }

    public void deleteGameProgress(int gameProgressId) {
        String sql = "DELETE FROM tbGameProgress WHERE game_progress_id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameProgressId);

            if(stmt.executeUpdate() > 0) {
                System.out.println("Successfully deleted game progress with id " + gameProgressId);
            } else {
                System.out.println("Failed to delete game progress with id " + gameProgressId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting game progress", e);
        }
    }


    // Specific query for the save file UI
    public int selectHeightGameProgress(int gameProgressId) {
        String sql = "SELECT height_progress FROM tbGameProgress WHERE game_progress_id = ?";

        int heightProgress = 0;

        try(Connection conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameProgressId);

            try(ResultSet res = stmt.executeQuery()) {

                if(res.next()) {
                    heightProgress = res.getInt("height_progress");
                } else {
                    System.out.println("Failed to get height progress on game progress with id: " + gameProgressId);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting height progress on game progress", e);
        }

        return heightProgress;
    }


    // Specific value change for the deepfall_count attribute
    public void updateDeepFallCount(GameProgress state) {
        String sql = """
            UPDATE tbGameProgress
            SET deepfall_count = ?
            WHERE game_progress_id = ?
        """;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, state.getDeepFallCount());
            stmt.setInt(2, state.getGameProgressId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("GameProgress deep fall count successfully updated");
            } else {
                System.out.println("No rows affected during update of GameProgress deep fall count");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating GameProgress deep fall count", e);
        }
    }
}
