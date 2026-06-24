package com.exception;

public class StockDataException extends Exception {
    public StockDataException(String message) {
        super(message);
    }
    public StockDataException(String message, Throwable cause) {
        super(message, cause);
    }
}