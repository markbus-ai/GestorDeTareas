package com.trullo.exception;

import java.sql.SQLException;

/**
 * Excepción de la capa de acceso a datos.
 *
 * <p>Envuelve una {@link SQLException} para que las capas superiores no dependan de
 * {@code java.sql} y conserva el estado SQL y el código de error originales para poder
 * diagnosticar el problema.
 */
public class DaoException extends RuntimeException {
    private final String sqlState;
    private final int errorCode;

    /**
     * Crea la excepción a partir del error producido por la base de datos.
     *
     * @param message mensaje que describe la operación que falló
     * @param cause excepción SQL original
     */
    public DaoException(String message, SQLException cause) {
        super(message, cause);
        this.sqlState = cause.getSQLState();
        this.errorCode = cause.getErrorCode();
    }

    /**
     * Devuelve el estado SQL informado por el motor de base de datos.
     *
     * @return el estado SQL de la excepción original
     */
    public String getSqlState() {
        return sqlState;
    }

    /**
     * Devuelve el código de error informado por el motor de base de datos.
     *
     * @return el código de error de la excepción original
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Crea una {@link DaoException} a partir de un mensaje y una excepción SQL.
     *
     * @param message mensaje que describe la operación que falló
     * @param cause excepción SQL original
     * @return la excepción de acceso a datos
     */
    public static DaoException from(String message, SQLException cause) {
        return new DaoException(message, cause);
    }
}
