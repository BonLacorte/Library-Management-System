package com.example.Library_Management_System.payload.request;

import com.example.Library_Management_System.domain.PaymentGateway;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new subscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeRequest {

    private Long userId;

    @NotNull(message = "Plan ID is mandatory")
    private Long planId;

    private PaymentGateway paymentGateway;

    private Boolean autoRenew = false;

    private String successUrl;

    private String cancelUrl;
}

