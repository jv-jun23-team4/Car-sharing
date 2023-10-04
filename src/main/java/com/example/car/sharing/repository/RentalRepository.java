package com.example.car.sharing.repository;

import com.example.car.sharing.model.Rental;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserId(Long userId);

    @Query("FROM Rental r WHERE r.userId = :userId AND r.isActive = :isActive")
    List<Rental> findByUserIdAndIsActive(Long userId, boolean isActive);
}
