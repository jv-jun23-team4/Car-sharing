package com.example.car.sharing.repository;

import com.example.car.sharing.model.Payment;
import com.example.car.sharing.model.Rental;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRental(Rental rental);

    Optional<Payment> findBySessionId(String sessionId);

    List<Payment> findByStatusAndExpiredTimeAfter(Payment.Status status, Instant expiredTime);
}
