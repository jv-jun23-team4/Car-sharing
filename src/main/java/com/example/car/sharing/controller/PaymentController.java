package com.example.car.sharing.controller;

import com.example.car.sharing.dto.payment.PaymentRequest;
import com.example.car.sharing.model.Payment;
import com.example.car.sharing.service.PaymentService;
import com.stripe.exception.StripeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/")
    public String createPaymentSession(@RequestBody PaymentRequest paymentRequest)
            throws StripeException {
        return paymentService.createPaymentSession(paymentRequest);
    }

    @GetMapping("/")
    public List<Payment> getPaymentsByUserId(@RequestParam("user_id") Long userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    @GetMapping("/success/")
    public ResponseEntity<String> handleSuccessfulPayment(
            @RequestParam("session_id") String sessionId)
            throws StripeException {
        paymentService.handleSuccessfulPayment(sessionId);
        return ResponseEntity.ok("Payment successful! Thank you for your payment.");
    }

    @GetMapping("/cancel/")
    public ResponseEntity<String> handleCanceledPayment(
            @RequestParam("session_id") String sessionId) {
        paymentService.handleCanceledPayment(sessionId);
        return ResponseEntity.ok("Payment canceled. Your payment has been paused.");
    }
}
