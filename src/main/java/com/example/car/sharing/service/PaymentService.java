package com.example.car.sharing.service;

import com.example.car.sharing.dto.payment.PaymentRequest;
import com.example.car.sharing.model.Payment;
import com.stripe.exception.StripeException;
import java.util.List;

public interface PaymentService {
    String createPaymentSession(PaymentRequest paymentRequest) throws StripeException;

    List<Payment> getPaymentsByUserId(Long userId);

    void handleSuccessfulPayment(String sessionId) throws StripeException;

    void handleCanceledPayment(String sessionId);
}
