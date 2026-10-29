package com.trullo.exception;

import java.sql.SQLException;

public class DaoException extends RuntimeException {
    private final String sqlState;
    private final int errorCode;

    public DaoException(String message, SQLException cause) {
        super(message, cause);
        this.sqlState = cause.getSQLState();
        this.errorCode = cause.getErrorCode();
    }

    public String getSqlState() {
        return sqlState;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public static DaoException from(String message, SQLException cause) {
        return new DaoException(message, cause);
    }
}
