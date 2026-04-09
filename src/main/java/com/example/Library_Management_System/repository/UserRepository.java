package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.modal.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by its email address
     * @param email The email address to search for
     * @return The user with the specified email address, or null if not found
     */
    User findByEmail(String email);

    /**
     * Find all users with the role of USER
     * @return Set of users with the USER role
     */
    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findByRole(
        @Param("role") UserRole role, 
        Pageable pageable
    );

    
    /**
     * Find users by its unique role
     * @param role The role to search for
     * @param pageable The pagination information
     * @return A page of users with the specified role
     */
    Set<User> findByRole(UserRole role);
}