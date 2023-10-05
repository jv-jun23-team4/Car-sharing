package com.example.car.sharing.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.example.car.sharing.dto.payment.PaymentRequest;
import com.example.car.sharing.dto.payment.PaymentResponseDto;
import com.example.car.sharing.model.Payment;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.repository.PaymentRepository;
import com.example.car.sharing.repository.RentalRepository;
import com.example.car.sharing.service.payment.CalculateTotalPrice;
import com.example.car.sharing.service.payment.PaymentServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

class PaymentServiceTest {

    private static final String SESSION_ID = "sessionId";
    private static final Long RENTAL_ID = 1L;
    private static final String SESSION_URL = "http://session.url";
    private static final BigDecimal AMOUNT_TO_PAY = BigDecimal.valueOf(100);
    private static final Long USER_ID = 2L;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CalculateTotalPrice calculator;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create a payment session should return payment response DTO")
    void createPaymentSession_ShouldReturnPaymentResponseDto() throws StripeException {
        PaymentRequest request = new PaymentRequest();
        request.setRentalId(RENTAL_ID);
        request.setType(Payment.Type.PAYMENT);

        Payment payment = new Payment();
        payment.setAmountToPay(AMOUNT_TO_PAY);

        Session session = new Session();
        session.setUrl(SESSION_URL);
        session.setId(SESSION_ID);
        session.setExpiresAt(System.currentTimeMillis());

        Rental rental = new Rental();
        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(calculator.calculate(any())).thenReturn(AMOUNT_TO_PAY);

        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            mocked.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(session);

            PaymentResponseDto responseDto = paymentService.createPaymentSession(request);
            assertThat(responseDto.amountToPay()).isEqualTo(AMOUNT_TO_PAY);
            assertThat(responseDto.sessionId()).isEqualTo(SESSION_ID);
            assertThat(responseDto.sessionUrl()).isEqualTo(SESSION_URL);
        }
    }

    @Test
    @DisplayName("Get payments by user ID should return a list of payments")
    void getPaymentsByUserId_ShouldReturnListOfPayments() {
        Rental rental = new Rental();
        Payment payment = new Payment();
        payment.setRental(rental);

        when(rentalRepository.findByUserId(USER_ID)).thenReturn(Collections.singletonList(rental));
        when(paymentRepository.findByRental(rental)).thenReturn(Collections.singletonList(payment));

        List<Payment> payments = paymentService.getPaymentsByUserId(USER_ID);
        assertThat(payments).containsExactly(payment);
    }

    @Test
    @DisplayName("Handle successful payment should update payment status to PAID")
    void handleSuccessfulPayment_ShouldUpdatePaymentStatusToPaid() {
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setSessionId(SESSION_ID);

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));
        paymentService.handleSuccessfulPayment(SESSION_ID);
        assertEquals(Payment.Status.PAID, payment.getStatus());
    }

    @Test
    @DisplayName("Handle canceled payment should update payment status to CANCELED")
    void handleCanceledPayment_ShouldUpdatePaymentStatusToCanceled() {
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setSessionId(SESSION_ID);

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));
        paymentService.handleCanceledPayment(SESSION_ID);
        assertEquals(Payment.Status.CANCELED, payment.getStatus());
    }
}
