package com.trullo.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    private final HikariDataSource dataSource;
    private static final Dotenv dotenv = Dotenv.load();

    private static final int POOL_MIN = 2;
    private static final int POOL_MAX = 10;
    private static final long CONNECTION_TIMEOUT = 30000;
    private static final long IDLE_TIMEOUT = 600000;
    private static final long MAX_LIFETIME = 1800000;

    private DatabaseConnection() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" +
                dotenv.get("DB_HOST") + ":" +
                dotenv.get("DB_PORT") + "/" +
                dotenv.get("DB_NAME"));
        config.setUsername(dotenv.get("DB_USER"));
        config.setPassword(dotenv.get("DB_PASSWORD"));
        config.setMinimumIdle(POOL_MIN);
        config.setMaximumPoolSize(POOL_MAX);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setIdleTimeout(IDLE_TIMEOUT);
        config.setMaxLifetime(MAX_LIFETIME);

        this.dataSource = new HikariDataSource(config);
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
