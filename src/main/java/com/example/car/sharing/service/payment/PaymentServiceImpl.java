package com.example.car.sharing.service.payment;

import com.example.car.sharing.dto.payment.PaymentRequest;
import com.example.car.sharing.dto.payment.PaymentResponseDto;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.model.Payment;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.model.User;
import com.example.car.sharing.repository.CarRepository;
import com.example.car.sharing.repository.PaymentRepository;
import com.example.car.sharing.repository.RentalRepository;
import com.example.car.sharing.repository.UserRepository;
import com.example.car.sharing.service.NotificationService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String PAYMENT_SUCCESS = """
            Payment Confirmation: You have successfully rent a car.
            Rent Details:
              - Car: %s
              - Rental Period: from %s to %s
              - Total Price: $%s
            Thank you for choosing our service!
                        """;
    private static final Long EXPIRATION_TIME = Instant.now().plus(24, ChronoUnit.HOURS)
            .getEpochSecond();
    private static final String CURRENCY = "usd";
    private static final String LINE_ITEM_NAME = "Payment";
    private static final String CANCEL_URL
            = "http://localhost:8080/api/payments/cancel/?session_id={CHECKOUT_SESSION_ID}";
    private static final String SUCCESS_URL
            = "http://localhost:8080/api/payments/success/?session_id={CHECKOUT_SESSION_ID}";
    private static final Long MAX_QUANTITY = 1L;
    private static final String MULTIPLY_UNIT_AMOUNT = "100";
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final CalculateTotalPrice calculator;
    private final NotificationService notificationService;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaymentResponseDto createPaymentSession(PaymentRequest paymentRequest)
            throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        Rental rental = rentalRepository.findById(paymentRequest.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id"
                        + paymentRequest.getRentalId()));
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(paymentRequest.getType());
        payment.setAmountToPay(calculator.calculate(payment));

        SessionCreateParams params = createStripeSession(payment);

        Session session = Session.create(params);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setExpiredTime(Instant.ofEpochSecond(session.getExpiresAt()));

        notificationAboutSuccessfulPayment(payment, rental);
        rental.setActive(false);
        rentalRepository.save(rental);
        paymentRepository.save(payment);
        return new PaymentResponseDto(payment.getSessionUrl());
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
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .setExpiresAt(EXPIRATION_TIME)
                .addLineItem(
                        new SessionCreateParams.LineItem.Builder()
                                .setQuantity(MAX_QUANTITY)
                                .setPriceData(
                                        new SessionCreateParams.LineItem.PriceData.Builder()
                                                .setCurrency(CURRENCY)
                                                .setUnitAmount(payment.getAmountToPay()
                                                        .multiply(new BigDecimal(
                                                                MULTIPLY_UNIT_AMOUNT))
                                                        .longValue())
                                                .setProductData(
                                                        new SessionCreateParams.LineItem
                                                                .PriceData.ProductData.Builder()
                                                                .setName(LINE_ITEM_NAME)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private void notificationAboutSuccessfulPayment(Payment payment, Rental rental) {
        Car car = carRepository.findById(rental.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find a car with id" + rental.getCarId())
        );
        User user = userRepository.findById(rental.getUserId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find a user with id" + rental.getUserId())
        );
        String carName = car.getBrand() + " " + car.getModel();
        LocalDate returnDate = rental.getActualReturnDate() == null
                ? rental.getReturnDate() : rental.getActualReturnDate();
        String message = String.format(PAYMENT_SUCCESS, carName, rental.getRentalDate(), returnDate,
                payment.getAmountToPay());
        notificationService.sendMessage(user.getChatId(), message);
    }
}
