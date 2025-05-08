package com.example.joeandmarie.data.dao;

import com.example.joeandmarie.data.database.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaveProgressDao {

    // SAVE SLOT IDs (1, 2, 3)
    public void insertSaveProgress(int saveProgressId) {
        String sql = """
            INSERT INTO tbSaveProgress (
                save_progress_id,          
            ) VALUES (?)
        """;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saveProgressId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("SaveProgress successfully inserted ");
            } else {
                System.out.println("No rows affected during insert of SaveProgress ");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting SaveProgress", e);
        }
    }

    public void deleteSaveProgress(int save_progress_id) {
        String sql = "DELETE FROM tbSaveProgress WHERE save_progress_id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, save_progress_id);

            if(stmt.executeUpdate() > 0) {
                System.out.println("Successfully deleted save progress with id " + save_progress_id);
            } else {
                System.out.println("Failed to delete save progress with id " + save_progress_id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting save progress", e);
        }
    }
}
