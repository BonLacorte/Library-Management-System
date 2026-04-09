package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.username}")
    private String adminUsername;

    @Override
    public void run(String... args) {
        // Debugging prints to verify what Spring is actually loading
        System.out.println("--- INIT CHECK ---");
        System.out.println("Loaded Email: " + adminEmail);
        System.out.println("Is Password Null?: " + (adminPassword == null));
        
        // Safety guard to completely prevent the crash
        if (adminPassword == null || adminPassword.trim().isEmpty()) {
            System.err.println("CRITICAL: Skipping Admin creation because password is null or empty.");
            return; 
        }

        initializeAdmin();
    }

    private void initializeAdmin() {

        if (userRepository.findByEmail(adminEmail) == null) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFullName("System Admin");
            admin.setRole(UserRole.ROLE_ADMIN); 

            
            userRepository.save(admin);
            System.out.println("Success: Admin created successfully!");
        } else {
            System.out.println("Admin already exists in the database. Skipping creation.");
        }
    }
}
