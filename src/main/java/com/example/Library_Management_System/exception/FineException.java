package com.example.Library_Management_System.exception;

/**
 * Custom exception for fine-related operations
 */
public class FineException extends RuntimeException {

    public FineException(String message) {
        super(message);
    }

    public FineException(String message, Throwable cause) {
        super(message, cause);
    }
}
