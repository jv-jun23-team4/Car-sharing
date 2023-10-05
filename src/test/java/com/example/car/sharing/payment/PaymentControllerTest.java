package com.example.car.sharing.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.car.sharing.dto.payment.PaymentRequest;
import com.example.car.sharing.dto.payment.PaymentResponseDto;
import com.example.car.sharing.model.Payment;
import com.example.car.sharing.service.payment.PaymentService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PaymentControllerTest {
    private static final String PATH_FOR_ADD_DEFAULT_PAYMENTS_SQL_FILE
            = "database/payment/add-default-payments.sql";
    private static final String PATH_FOR_REMOVE_ALL_PAYMENTS_SQL_FILE
            = "database/payment/remove-all-payments.sql";
    private static final Long RENTAL_ID = 1L;
    private static final String SESSION_ID = "session1";
    private static final String SESSION_URL = "https://sample.url/session1";
    private static final BigDecimal AMOUNT_TO_PAY = BigDecimal.valueOf(50.0);
    private static final String CREATE_ENDPOINT = "/payments/";
    private static final String RENEW_ENDPOINT = "/payments";
    private static final String PARAM_NAME_FOR_RENEW_METHOD = "sessionId";
    private static final String PARAM_NAME_FOR_HANDLE_CANCELED_METHOD = "session_id";
    private static final Long USER_ID = 1L;
    private static final String ENDPOINT_URL = "/payments/{user_id}";
    private static final int EXPECTED_PAYMENTS_SIZE = 1;
    private static final String EXPECTED_SUCCESS_MESSAGE
            = "Payment successful! Thank you for your payment.";
    private static final String SUCCESS_ENDPOINT = "/payments/success/";
    private static final String CANCEL_ENDPOINT = "/payments/cancel/";
    private static final String EXPECTED_RESPONSE_MESSAGE
            = "Payment canceled. You can pay later but the session will be available for 24 hours";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @MockBean
    private PaymentService paymentServiceMock;

    @BeforeEach
    void beforeEach() throws Exception {
        teardown();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(PATH_FOR_ADD_DEFAULT_PAYMENTS_SQL_FILE)
            );
        }
    }

    @AfterEach
    void afterEach() {
        teardown();
    }

    private void teardown() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(PATH_FOR_REMOVE_ALL_PAYMENTS_SQL_FILE)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test creating a new payment session")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void createPaymentSession_ValidRequest_ReturnsOk() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setRentalId(RENTAL_ID);
        request.setType(Payment.Type.PAYMENT);

        PaymentResponseDto expectedResponse = new PaymentResponseDto(
                Payment.Status.PENDING,
                SESSION_URL,
                SESSION_ID,
                AMOUNT_TO_PAY
        );
        when(paymentServiceMock.createPaymentSession(
                any(PaymentRequest.class))).thenReturn(expectedResponse);

        MvcResult result = mockMvc.perform(post(CREATE_ENDPOINT)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        PaymentResponseDto actualResponse = mapper.readValue(
                responseBody, PaymentResponseDto.class);
        assertNotNull(responseBody);
        assertEquals(expectedResponse.status(), actualResponse.status());
        assertEquals(expectedResponse.sessionUrl(), actualResponse.sessionUrl());
        assertEquals(expectedResponse.sessionId(), actualResponse.sessionId());
        assertEquals(expectedResponse.amountToPay(), actualResponse.amountToPay());
    }

    @Test
    @DisplayName("Test renewing a payment session")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void renewPaymentSession_ValidSessionId_ReturnsOk() throws Exception {
        PaymentResponseDto mockResponse = new PaymentResponseDto(
                Payment.Status.PENDING,
                SESSION_URL,
                SESSION_ID,
                AMOUNT_TO_PAY
        );

        when(paymentServiceMock.renewPaymentSession(SESSION_ID)).thenReturn(mockResponse);

        MvcResult result = mockMvc.perform(post(RENEW_ENDPOINT)
                        .param(PARAM_NAME_FOR_RENEW_METHOD, SESSION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        PaymentResponseDto responseDto = mapper.readValue(responseBody, PaymentResponseDto.class);
        assertNotNull(responseBody);
        assertEquals(Payment.Status.PENDING, responseDto.status());
    }

    @Test
    @DisplayName("Test getting user payments by user ID")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void getPaymentsByUserId_ValidUserId_ReturnsPayments() throws Exception {
        List<Payment> mockPayments = Collections.singletonList(new Payment());
        when(paymentServiceMock.getPaymentsByUserId(USER_ID)).thenReturn(mockPayments);
        String responseBody = mockMvc.perform(get(ENDPOINT_URL, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(responseBody);
        List<Payment> payments = new ObjectMapper().readValue(
                responseBody, new TypeReference<>() {});
        assertEquals(EXPECTED_PAYMENTS_SIZE, payments.size());
    }

    @Test
    @DisplayName("Test handling successful payment")
    public void handleSuccessfulPayment_ValidSessionId_ReturnsOk() throws Exception {
        String responseBody = mockMvc.perform(get(SUCCESS_ENDPOINT)
                        .param(PARAM_NAME_FOR_HANDLE_CANCELED_METHOD,
                                SESSION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(responseBody);
        assertEquals(EXPECTED_SUCCESS_MESSAGE, responseBody);
    }

    @Test
    @DisplayName("Test handling canceled payment")
    public void handleCanceledPayment_ValidSessionId_ReturnsOk() throws Exception {
        String responseBody = mockMvc.perform(get(CANCEL_ENDPOINT)
                        .param(PARAM_NAME_FOR_HANDLE_CANCELED_METHOD,
                                SESSION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(responseBody);
        assertEquals(EXPECTED_RESPONSE_MESSAGE, responseBody);
    }
}

