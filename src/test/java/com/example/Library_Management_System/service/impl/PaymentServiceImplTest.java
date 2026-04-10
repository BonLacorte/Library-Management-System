package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.domain.PaymentGateway;
import com.example.Library_Management_System.domain.PaymentStatus;
import com.example.Library_Management_System.domain.PaymentType;
import com.example.Library_Management_System.exception.PaymentException;
import com.example.Library_Management_System.modal.Payment;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.request.PaymentInitiateRequest;
import com.example.Library_Management_System.payload.request.PaymentVerifyRequest;
import com.example.Library_Management_System.payload.response.PaymentInitiateResponse;
import com.example.Library_Management_System.payload.response.PaymentLinkResponse;
import com.example.Library_Management_System.repository.PaymentRepository;
import com.example.Library_Management_System.repository.UserRepository;
import com.example.Library_Management_System.service.gateway.RazorpayService;
// import com.example.Library_Management_System.service.gateway.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RazorpayService razorpayService;

    // // We still mock StripeService to ensure it isn't accidentally called
    // @Mock
    // private StripeService stripeService; 

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User dummyUser;
    private Payment dummyPayment;
    private PaymentInitiateRequest initiateRequest;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setEmail("johndoe@example.com");
        dummyUser.setFullName("John Doe");
        dummyUser.setPhone("1234567890");

        dummyPayment = new Payment();
        dummyPayment.setId(1L);
        dummyPayment.setUser(dummyUser);
        dummyPayment.setAmount(500L);
        dummyPayment.setPaymentType(PaymentType.MEMBERSHIP);
        dummyPayment.setGateway(PaymentGateway.RAZORPAY);
        dummyPayment.setStatus(PaymentStatus.PENDING);
        dummyPayment.setGatewayPaymentId("pay_razorpay12345");

        initiateRequest = PaymentInitiateRequest.builder()
                .userId(1L)
                .amount(500L)
                .paymentType(PaymentType.MEMBERSHIP)
                .gateway(PaymentGateway.RAZORPAY) // Enforcing Razorpay here
                .build();
    }

    // ==================== INITIATE PAYMENT (RAZORPAY) ====================

    @Test
    void testInitiatePayment_WithRazorpay_Success() throws PaymentException {
        // ARRANGE
        PaymentLinkResponse razorpayLinkResponse = PaymentLinkResponse.builder()
                .payment_link_id("plink_123")
                .payment_link_url("https://rzp.io/i/plink123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(razorpayService.createPaymentLink(dummyUser, dummyPayment))
                .thenReturn(razorpayLinkResponse);
        when(paymentRepository.save(any(Payment.class))).thenReturn(dummyPayment);

        // ACT
        PaymentInitiateResponse response = paymentService.initiatePayment(initiateRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals("https://rzp.io/i/plink123", response.getCheckoutUrl());
        
        // Verify Razorpay was called, and Stripe was NEVER called
        verify(razorpayService, times(1)).createPaymentLink(dummyUser, dummyPayment);
        // verify(stripeService, never()).createPaymentLink(any(), any());

        // Capture and verify the payment entity that was saved
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        
        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(PaymentGateway.RAZORPAY, savedPayment.getGateway());
        assertEquals(PaymentStatus.PENDING, savedPayment.getStatus());
    }

    // ==================== VERIFY PAYMENT (RAZORPAY) ====================

    @Test
    void testVerifyPayment_Razorpay_Success() throws PaymentException {
        // ARRANGE
        PaymentVerifyRequest verifyRequest = new PaymentVerifyRequest();
        // verifyRequest.setRazorPaymentId("1"); 
        verifyRequest.setRazorpayPaymentId("pay_razorpay12345");
        verifyRequest.setRazorpayOrderId("order_123");
        verifyRequest.setRazorpaySignature("signature_hash");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(dummyPayment));
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(dummyPayment);

        // ACT
        paymentService.verifyPayment(verifyRequest);

        // ASSERT
        assertEquals(PaymentStatus.SUCCESS, dummyPayment.getStatus());
        verify(paymentRepository, times(1)).save(dummyPayment);
        // Verify success event was published to trigger Subscription or Fine logic
        verify(eventPublisher, times(1)).publishEvent(any()); 
    }

    @Test
    void testVerifyPayment_Razorpay_InvalidSignature_ShouldFail() throws PaymentException {
        // ARRANGE
        PaymentVerifyRequest verifyRequest = new PaymentVerifyRequest();
        verifyRequest.setRazorpayPaymentId("pay_razorpay12345");
        verifyRequest.setRazorpayOrderId("order_123");
        verifyRequest.setRazorpaySignature("invalid_hash");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(dummyPayment));

        // ACT & ASSERT
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.verifyPayment(verifyRequest);
        });

        assertTrue(exception.getMessage().contains("Verification Failed") || exception.getMessage().contains("Invalid Signature"));
        assertEquals(PaymentStatus.FAILED, dummyPayment.getStatus());
        verify(paymentRepository, times(1)).save(dummyPayment);
    }

    @Test
    void testVerifyPayment_WhenPaymentRecordNotFound_ShouldThrowException() {
        // ARRANGE
        PaymentVerifyRequest verifyRequest = new PaymentVerifyRequest();
        verifyRequest.setRazorpayPaymentId("99"); 

        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(PaymentException.class, () -> {
            paymentService.verifyPayment(verifyRequest);
        });
    }
}