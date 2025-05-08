package com.example.joeandmarie.data.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInit {
    private static final String createGameProgress = """ 
            CREATE TABLE IF NOT EXISTS tbGameProgress (
                game_progress_id INT PRIMARY KEY,
                save_progress_id INT,
                height_progress INT,
                x_coordinate FLOAT,
                y_coordinate FLOAT,
                deepfall_count INT DEFAULT 0,
                FOREIGN KEY(save_progress_id) REFERENCES tbSaveProgress(save_progress_id)
                ON DELETE CASCADE
            )
            """;

    private static final String createSaveProgress = """
            CREATE TABLE IF NOT EXISTS tbSaveProgress (
                save_progress_id INT PRIMARY KEY
            )
            """;

    private static final String createSettingPreference = """
            CREATE TABLE IF NOT EXISTS tbSettingPreference (
                setting_preference_id INT PRIMARY KEY,
                save_progress_id INT,
                music_volume FLOAT,
                fx_volume FLOAT,
                infinite_jump TINYINT(1),
                climb_walls TINYINT(1),
                infinite_grip TINYINT(1),
                FOREIGN KEY(save_progress_id) REFERENCES tbSaveProgress(save_progress_id)
                ON DELETE CASCADE
            )
            """;

    public static void initialize() {
        try (Connection connection = ConnectionPool.getConnection();
             Statement statement = connection.createStatement()) {

            statement.addBatch(createSaveProgress);
            statement.addBatch(createGameProgress);
            statement.addBatch(createSettingPreference);

            statement.executeBatch();

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            throw new RuntimeException(e + ": Database initialized unsuccessfully");
        }
    }
}
