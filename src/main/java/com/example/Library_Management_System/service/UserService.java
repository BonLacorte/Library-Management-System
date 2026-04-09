package com.example.Library_Management_System.service;

import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.exception.BookException;
import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.dto.UserDTO;

import java.util.List;
import java.util.Set;


public interface UserService {
	User getUserByEmail(String email) throws UserException;
	User getUserFromJwtToken(String jwt) throws UserException;

	// /**
    //  * Update an existing user
    //  * @param id User ID
    //  * @param userDTO Updated user data
    //  * @return Updated user DTO
    //  * @throws UserException if user not found or validation fails
    //  */
	// UserDTO updateUser(Long id, UserDTO userDTO) throws UserException;

	User getUserById(Long id) throws UserException;
	Set<User> getUserByRole(UserRole role) throws UserException;
	List<User> getUsers() throws UserException;
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