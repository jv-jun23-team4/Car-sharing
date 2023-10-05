package com.example.car.sharing.repository.user;

import com.example.car.sharing.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByChatId(long chatId);

    @Query("FROM User u WHERE u.role =: role")
    List<User> findAllByRole(User.UserRole role);
}

