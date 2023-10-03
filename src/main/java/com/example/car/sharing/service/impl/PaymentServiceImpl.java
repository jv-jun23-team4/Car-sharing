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
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id"
                        + paymentRequest.getRentalId()));
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setAmountToPay(paymentRequest.getAmount());
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);

        SessionCreateParams params = createStripeSession(payment);

        Session session = Session.create(params);
        payment.setSessionUrl(session.getUrl()
                .substring(0, Math.min(session.getUrl().length(), 255)));
        payment.setSessionId(session.getId());

        paymentRepository.save(payment);

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

    public void handleSuccessfulPayment(String sessionId) {
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

    private SessionCreateParams createStripeSession(Payment payment) {
        return new SessionCreateParams.Builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://example.com/success")
                .setCancelUrl("https://example.com/cancel")
                .addLineItem(
                        new SessionCreateParams.LineItem.Builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        new SessionCreateParams.LineItem.PriceData.Builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(payment.getAmountToPay()
                                                        .multiply(new BigDecimal(100)).longValue())
                                                .setProductData(
                                                        new SessionCreateParams.LineItem
                                                                .PriceData.ProductData.Builder()
                                                                .setName("Payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
