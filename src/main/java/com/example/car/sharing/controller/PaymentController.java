package com.example.car.sharing.controller;

import com.example.car.sharing.dto.payment.PaymentRequest;
import com.example.car.sharing.dto.payment.PaymentResponseDto;
import com.example.car.sharing.model.Payment;
import com.example.car.sharing.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for payment managing")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/")
    @Operation(summary = "Create a new payment session")
    public PaymentResponseDto createPaymentSession(@RequestBody PaymentRequest paymentRequest)
            throws StripeException {
        return paymentService.createPaymentSession(paymentRequest);
    }

    @GetMapping("/{user_id}")
    @Operation(summary = "Get users payments by users ID",
            description = "Get list of all users payments by users ID")
    public List<Payment> getPaymentsByUserId(@PathVariable("user_id") Long userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    @GetMapping("/success/")
    @Operation(summary = "Handle successful Stripe payments",
            description = "Receive a response about a successful payment from Stripe, set "
                    + "payment status: PAID and return a successful payment message to the user")
    public ResponseEntity<String> handleSuccessfulPayment(
            @RequestParam("session_id") String sessionId)
            throws StripeException {
        paymentService.handleSuccessfulPayment(sessionId);
        return ResponseEntity.ok("Payment successful! Thank you for your payment.");
    }

    @GetMapping("/cancel/")
    @Operation(summary = "Handle unsuccessful Stripe payments",
            description = "Receive a response about canceled payment from Stripe, set payment "
                    + "status: CANCELED and return a canceled payment message to the user")
    public ResponseEntity<String> handleCanceledPayment(
            @RequestParam("session_id") String sessionId) {
        paymentService.handleCanceledPayment(sessionId);
        return ResponseEntity.ok("Payment canceled. "
                + "You can pay later but the session will be available for 24 hours");
    }
}
