package com.example.Library_Management_System.payload.request;

import com.example.Library_Management_System.domain.PaymentGateway;
import com.example.Library_Management_System.domain.PaymentType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for initiating a payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateRequest {

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    // todo: book loan id
    private Long bookLoanId; // Required only for FINE payments

    @NotNull(message = "Payment type is mandatory")
    private PaymentType paymentType;

    @NotNull(message = "Payment gateway is mandatory")
    private PaymentGateway gateway; // RAZORPAY or STRIPE

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private Long amount;

    @Size(min = 3, max = 3, message = "Currency must be 3-letter code (e.g., USD)")
    private String currency = "USD";

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Long fineId;
    private Long subscriptionId;

    // Return URLs for payment gateway redirects
    @Size(max = 500, message = "Success URL must not exceed 500 characters")
    private String successUrl;

    @Size(max = 500, message = "Cancel URL must not exceed 500 characters")
    private String cancelUrl;
}
