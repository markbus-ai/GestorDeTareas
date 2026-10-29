package com.trullo.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DatabaseConnection
 * Gestiona las conexiones con la base de datos mediante HikariCP.
 * <p>
 * Implementa un pool de conexiones que permite reutilizar conexiones
 * existentes y limita a un máximo de 10 conexiones simultáneas.
 * <p>
 * Al finalizar su uso, las conexiones se devuelven al pool para
 * que puedan ser reutilizadas por otras operaciones, evitando
 * la creación y destrucción constante de conexiones físicas.
 * 
 * @author Nahuel Romero
 */
public class DatabaseConnection {
    /**
     * Pool de conexiones a la base de datos administrado por HikariCP.
     */
    private volatile HikariDataSource dataSource;

    /**
     * Carga las variables de entrono desde el .env manejado por Dotenv.
     */
    private static final Dotenv dotenv = Dotenv.load();
    
    /**
     * Instancia única del gestor de conexiones.
     */
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private static final int POOL_MIN = 2;
    private static final int POOL_MAX = 10;
    private static final long CONNECTION_TIMEOUT = 30000;
    private static final long IDLE_TIMEOUT = 600000;
    private static final long MAX_LIFETIME = 1800000;

    /**
     * Inicializa la conexión con la base de datos mediante HikariCP.
     * <p>
     * Crea el pool de conexiones y registra un hook de apagado
     * para cerrar el recurso cuando finaliza la JVM.
     */
    private DatabaseConnection() {
        this.dataSource = createDataSource();
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "db-shutdown"));
    }

    /**
     * Configura los parámetros de conexión a la base de datos.
     *
     * @return objeto HikariDataConfig con la configuración del pool de conexiones
     */
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

    /**
     * Proporciona acceso a la instancia única encargada de gestionar
     * el pool de conexiones a la base de datos.
     *
     * @return instancia de DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    /**
     * Obtiene una conexión disponible del pool administrado por HikariCP.
     * <p>
     * Si el pool no está inicializado o se encuentra cerrado, lo crea
     * nuevamente antes de solicitar la conexión.
     *
     * @return conexión disponible para ejecutar operaciones con la base de datos
     * @throws SQLException si ocurre un error al obtener la conexión
     */
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

    /**
     * Cierra el pool de conexiones y libera sus recursos.
     * <p>
     * Si el pool existe y todavía está abierto, lo cierra y establece
     * la referencia a null para permitir su posterior reinicialización.
     */
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
