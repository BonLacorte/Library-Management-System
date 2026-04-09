package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.exception.BookException;
import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.mapper.UserMapper;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.dto.BookDTO;
import com.example.Library_Management_System.payload.dto.UserDTO;
import com.example.Library_Management_System.payload.response.ApiResponse;
import com.example.Library_Management_System.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// ==================== CRUD OPERATIONS ====================

	/**
     * Get own user profile 
     * GET /api/users/profile
     */
	@GetMapping("/api/users/profile")
	public ResponseEntity<UserDTO> getUserProfileFromJwtHandler(
			@RequestHeader("Authorization") String jwt) throws UserException {
		User user = userService.getUserFromJwtToken(jwt);
		UserDTO userDTO=UserMapper.toDTO(user);

		return new ResponseEntity<>(userDTO,HttpStatus.OK);
	}

	// ================= Admin methods ====================

	/**
     * Get list of all users (Admin only)
     * GET /api/users/list
     */
	@GetMapping("/api/users/list")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<User>> getUsersListHandler() throws UserException {
		List<User> users = userService.getUsers();

		return new ResponseEntity<>(users,HttpStatus.OK);
	}

	/**
     * Get user by ID (Admin only)
     * GET /api/users/{userId}
     */
	@GetMapping("/api/users/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> getUserByIdHandler(
			@PathVariable Long userId
	) throws UserException {
		User user = userService.getUserById(userId);
		UserDTO userDTO=UserMapper.toDTO(user);

		return new ResponseEntity<>(userDTO,HttpStatus.OK);
	}

	/**
	 * Get total user statistics (Admin only)
	 * GET /api/users/statistics
	 *
	 * Returns total number of registered users in the system
	 *
	 * Example response:
	 * {
	 *   "totalUsers": 245
	 * }
	 */
	@GetMapping("/api/users/statistics")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserStatisticsResponse> getUserStatistics() {
		long totalUsers = userService.getTotalUserCount();
		return ResponseEntity.ok(new UserStatisticsResponse(totalUsers));
	}

	/**
	 * Delete admin by ID (Super Admin only)
	 * DELETE /api/super-admin/users/{userId}
	 *
	 * Example request:
	 * DELETE /api/super-admin/users/123
	 *
	 * Example response:
	 * {
	 *   "message": "Admin deleted successfully",
	 *   "success": true
	 * }
	 */
	@DeleteMapping("/api/users/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) throws UserException {
		userService.deleteUser(userId);
		return ResponseEntity.ok(new ApiResponse("User deleted successfully", true));
	}

	/**
	 * Response DTO for user statistics endpoint
	 */
	public static class UserStatisticsResponse {
		public long totalUsers;

		public UserStatisticsResponse(long totalUsers) {
			this.totalUsers = totalUsers;
		}
	}

}
