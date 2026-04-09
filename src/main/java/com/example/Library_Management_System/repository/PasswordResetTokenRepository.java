package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.modal.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteAllByExpiryDateBefore(LocalDateTime dateTime);
}
