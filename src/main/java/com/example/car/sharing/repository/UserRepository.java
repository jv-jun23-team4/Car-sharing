package com.example.car.sharing.repository;

import com.example.car.sharing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
