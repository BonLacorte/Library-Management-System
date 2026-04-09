package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.modal.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    // Find all users with the role of USER
    // @return Set of users with the USER role
    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findByRole(
        @Param("role") UserRole role, 
        Pageable pageable
    );

    Set<User> findByRole(UserRole role);
}