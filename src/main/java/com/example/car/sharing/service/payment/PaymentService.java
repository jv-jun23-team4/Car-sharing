package com.example.car.sharing.service.payment;

import com.example.car.sharing.dto.payment.PaymentRequest;
import com.example.car.sharing.dto.payment.PaymentResponseDto;
import com.example.car.sharing.model.Payment;
import com.stripe.exception.StripeException;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(PaymentRequest paymentRequest) throws StripeException;

    PaymentResponseDto renewPaymentSession(String sessionId) throws StripeException;

    List<Payment> getPaymentsByUserId(Long userId);

    void handleSuccessfulPayment(String sessionId) throws StripeException;

    void handleCanceledPayment(String sessionId);
}
