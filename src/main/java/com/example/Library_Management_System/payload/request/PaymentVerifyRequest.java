package com.example.Library_Management_System.payload.request;

import com.example.Library_Management_System.domain.PaymentGateway;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for verifying payment after gateway callback
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyRequest {

    // Razorpay specific fields
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;

    // Stripe specific fields
    private String stripePaymentIntentId;
    private String stripePaymentIntentStatus;

}
