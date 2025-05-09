package com.example.joeandmarie.data.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static final HikariDataSource datasource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DatabaseCredentials.URL);
        config.setUsername(DatabaseCredentials.USERNAME);
        config.setPassword(DatabaseCredentials.PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        datasource = new HikariDataSource(config);
    }

    private ConnectionPool() { }

    public static Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }
}
