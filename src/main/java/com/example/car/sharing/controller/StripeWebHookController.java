package com.example.car.sharing.controller;

import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.model.Payment;
import com.example.car.sharing.model.User;
import com.example.car.sharing.repository.payment.PaymentRepository;
import com.example.car.sharing.repository.user.UserRepository;
import com.example.car.sharing.service.NotificationService;
import com.example.car.sharing.service.payment.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StripeWebHookController {
    private static final String PAYMENT_SUCCESS_TEMPLATE = """
    ┌───────────────────────────────────┐
           Payment Confirmation        \s
    ├───────────────────────────────────┤
      Status: Successful               \s
    │───────────────────────────────────│
      Total Price: $%s
    │───────────────────────────────────│
      Thank you for choosing our       \s
      service!                 \s
    └───────────────────────────────────┘
            """;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(StripeWebHookController.class);
    private final ObjectMapper objectMapper;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public String handleStripeEvent(
            @RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        if (sigHeader == null) {
            return "";
        }

        Event event;
        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );
        } catch (SignatureVerificationException e) {
            logger.warn("⚠️  Webhook error while validating signature.");
            return "";
        }

        JsonNode payloadNode;
        try {
            payloadNode = objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            logger.warn("Error parsing JSON payload: " + e.getMessage());
            return "";
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                Session session = (Session) event.getDataObjectDeserializer().getObject()
                        .orElse(null);
                if (session != null) {
                    logger.info("Payment for session " + session.getId() + " succeeded.");
                    paymentService.handleSuccessfulPayment(session.getId());
                    sendNotificationAboutSucceededPayment(payloadNode);
                } else {
                    logger.warn("Session object is null. Unable to process payment.");
                }
            }
            case "checkout.session.expired" -> {
                logger.warn("Payment was not paid and expiration time is ended");
                String sessionId = payloadNode.at("/data/object/id").asText();
                Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                        () -> new EntityNotFoundException("There is no payment with session id "
                                + sessionId)
                );
                payment.setStatus(Payment.Status.EXPIRED);
                paymentRepository.save(payment);
            }
            case "checkout.session.async_payment_succeeded" -> {
                Session session = (Session) event.getDataObjectDeserializer().getObject()
                        .orElse(null);
                if (session != null) {
                    logger.info("Async payment succeeded.");
                    String sessionId = payloadNode.at("/data/object/id").asText();
                    paymentService.handleSuccessfulPayment(sessionId);
                    sendNotificationAboutSucceededPayment(payloadNode);
                } else {
                    logger.warn("Session object is null. Unable to process payment.");
                }
            }
            default -> logger.warn("Unhandled event type: " + event.getType());
        }
        return "";
    }

    private void sendNotificationAboutSucceededPayment(JsonNode payloadNode) {
        String email = payloadNode.at("/data/object/customer_details/email").asText();
        Optional<User> userOptional = userRepository.findByEmail(email);
        String amountTotal = payloadNode.at("/data/object/amount_total").asText();
        Integer price = Integer.parseInt(amountTotal) / 100;
        userOptional.ifPresent(
                user -> notificationService.sendMessage(
                        user.getChatId(),
                        String.format(PAYMENT_SUCCESS_TEMPLATE, price))
        );
    }
}
