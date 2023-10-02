package com.example.car.sharing.service.impl;

import com.example.car.sharing.dto.payment.PaymentRequest;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.model.Payment;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.repository.PaymentRepository;
import com.example.car.sharing.repository.RentalRepository;
import com.example.car.sharing.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public String createPaymentSession(PaymentRequest paymentRequest) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        Rental rental = rentalRepository.findById(paymentRequest.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Rental not found"));

        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(rental);
        payment.setSessionUrl(paymentRequest.getSessionUrl());
        payment.setAmountToPay(paymentRequest.getAmount());

        paymentRepository.save(payment);

        Session session = createStripeSession(payment);
        return session.getId();
    }

    public List<Payment> getPaymentsByUserId(Long userId) {
        List<Rental> rentals = rentalRepository.findByUserId(userId);
        List<Payment> payments = new ArrayList<>();

        for (Rental rental : rentals) {
            payments.addAll(paymentRepository.findByRental(rental));
        }

        return payments;
    }

    public void handleSuccessfulPayment(String sessionId) throws StripeException {
        Payment payment = getPaymentBySessionId(sessionId);
        payment.setStatus(Payment.Status.PAID);
        updatePayment(payment);
    }

    public void handleCanceledPayment(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        payment.setStatus(Payment.Status.CANCELED);
        updatePayment(payment);
    }

    private Payment getPaymentBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for session ID: " + sessionId));
    }

    private void updatePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    private Session createStripeSession(Payment payment) throws StripeException {
        SessionCreateParams.Builder builder = new SessionCreateParams.Builder();
        builder.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD);
        builder.setMode(SessionCreateParams.Mode.PAYMENT);
        builder.setSuccessUrl(payment.getSessionUrl() + "/success");
        builder.setCancelUrl(payment.getSessionUrl() + "/cancel");

        SessionCreateParams.LineItem.PriceData.Builder priceBuilder =
                SessionCreateParams.LineItem.PriceData.builder();
        priceBuilder.setCurrency("usd");
        priceBuilder.setUnitAmount(
                payment.getAmountToPay().multiply(BigDecimal.valueOf(100)).longValue());

        SessionCreateParams.LineItem.Builder lineItemBuilder =
                SessionCreateParams.LineItem.builder();
        lineItemBuilder.setPriceData(priceBuilder.build());
        lineItemBuilder.setQuantity(1L);

        builder.addLineItem(lineItemBuilder.build());

        SessionCreateParams params = builder.build();
        return Session.create(params);
    }
}
