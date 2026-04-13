package com.example.Library_Management_System.domain;

/**
 * Enum representing the payment status of a fine
 */
public enum FineStatus {
    /**
     * Fine has been assessed but not paid
     */
    PENDING,

    /**
     * Fine has been partially paid
     */
    PARTIALLY_PAID,

    /**
     * Fine has been fully paid
     */
    PAID,

    /**
     * Fine has been waived by an administrator
     */
    WAIVED
}
