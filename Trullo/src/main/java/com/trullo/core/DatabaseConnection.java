package com.trullo.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    private volatile HikariDataSource dataSource;
    private static final Dotenv dotenv = Dotenv.load();

    private static final int POOL_MIN = 2;
    private static final int POOL_MAX = 10;
    private static final long CONNECTION_TIMEOUT = 30000;
    private static final long IDLE_TIMEOUT = 600000;
    private static final long MAX_LIFETIME = 1800000;

    private DatabaseConnection() {
        this.dataSource = createDataSource();
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "db-shutdown"));
    }

    private static HikariDataSource createDataSource() {
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
        return new HikariDataSource(config);
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        HikariDataSource ds = dataSource;
        if (ds == null || ds.isClosed()) {
            synchronized (this) {
                ds = dataSource;
                if (ds == null || ds.isClosed()) {
                    ds = createDataSource();
                    dataSource = ds;
                }
            }
        }
        return ds.getConnection();
    }

    public void close() {
        synchronized (this) {
            HikariDataSource ds = dataSource;
            if (ds != null && !ds.isClosed()) {
                ds.close();
            }
            dataSource = null;
        }
    }
}
