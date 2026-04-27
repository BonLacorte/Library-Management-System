package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.modal.NotificationSettings;
import com.example.Library_Management_System.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    // Find settings by user
    Optional<NotificationSettings> findByUser(User user);

    // Check if settings exist for user
    boolean existsByUser(User user);
}

