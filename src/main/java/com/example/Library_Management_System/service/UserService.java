package com.example.Library_Management_System.service;

import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.exception.BookException;
import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.dto.UserDTO;

import java.util.List;
import java.util.Set;


public interface UserService {
	/**
	 * Get user by email
	 * @param email User email
	 * @return User entity
	 * @throws UserException if user not found
	 */
	User getUserByEmail(String email) throws UserException;

	/**
	 * Get user from JWT token
	 * @param jwt JWT token
	 * @return User entity
	 * @throws UserException if user not found
	 */
	User getUserFromJwtToken(String jwt) throws UserException;

	/**
     * Update an existing user by self
     * @param id User ID
     * @param userDTO Updated user data
     * @return Updated user DTO
     * @throws UserException if user not found or validation fails
     */
	UserDTO updateUserBySelf(Long id, UserDTO userDTO) throws UserException;



	// /**
    //  * Update an existing user by admin (Admin only)
    //  * @param id User ID
    //  * @param userDTO Updated user data
    //  * @return Updated user DTO
    //  * @throws UserException if user not found or validation fails
    //  */
	// UserDTO updateUserByAdmin(Long id, UserDTO userDTO) throws UserException;

	/**
	 * Get user by ID
	 * @param id User ID
	 * @return User entity
	 * @throws UserException if user not found
	 */
	User getUserById(Long id) throws UserException;

	/**
	 * Get users by role
	 * @param role User role
	 * @return Set of user entities
	 * @throws UserException if role not found
	 */
	Set<User> getUserByRole(UserRole role) throws UserException;

	/**
	 * Get all users (Admin only)
	 * @return List of user entities
	 * @throws UserException if no users found
	 */
	List<User> getUsers() throws UserException;

	/**
	 * Get current user
	 * @return Current user entity
	 * @throws UserException if current user not found
	 */
	User getCurrentUser() throws UserException;

	/**
	 * Get total count of all registered users (Admin only)
	 * @return Total user count
	 */
	long getTotalUserCount();


	/**
     * Permanently delete an admin from the database (Admin only)
     * @param adminId User ID
     * @throws UserException if admin not found
     */
	void deleteUser(Long userId) throws UserException;


	// Super Admin methods

	// /**
    //  * Create a new admin (Admin only)
    //  * @param userDTO Admin data
    //  * @return Created admin DTO
    //  * @throws UserException if email already exists or validation fails
    //  */
	// UserDTO createAdmin(UserDTO userDTO) throws UserException;
}