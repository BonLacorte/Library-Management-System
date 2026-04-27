package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.configurations.JwtProvider;
import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.mapper.UserMapper;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.dto.UserDTO;
import com.example.Library_Management_System.repository.UserRepository;
import com.example.Library_Management_System.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;



	/**
     * Update an existing user by self
     * @param id User ID
     * @param userDTO Updated user data
     * @return Updated user DTO
     * @throws UserException if user not found or validation fails
     */
	@Override
	public UserDTO updateUserBySelf(Long id, UserDTO userDTO) throws UserException {
		User user = userRepository.findById(id).orElseThrow(() -> new UserException("User not found with id: "+id));
		
		// If the user is admin, they can update their own profile but not other user profile
		if(user.getRole() == UserRole.ROLE_ADMIN) {
			if(!user.getId().equals(id)) {
				throw new UserException("Only admin can update their own profile");
			}
		}

		// If the user is user, they can update their own profile but not other user profile
		if(user.getRole() == UserRole.ROLE_USER) {
			if(!user.getId().equals(id)) {
				throw new UserException("Only user can update their own profile");
			}
		}

		// Update the book
		userMapper.updateEntityFromDTO(user, userDTO);

		// Save the updated user
		User updatedUser = userRepository.save(user);
		return userMapper.toDTO(updatedUser);
	}



		@Override
	public User getUserByEmail(String email) throws UserException {
		User user=userRepository.findByEmail(email);
		if(user==null){
			throw new UserException("User not found with email: "+email);
		}
		return user;
	}

	@Override
	public User getUserFromJwtToken(String jwt) throws UserException {
		String email = jwtProvider.getEmailFromJwtToken(jwt);
		User user = userRepository.findByEmail(email);
		if(user==null) throw new UserException("user not exist with email "+email);
		return user;
	}

	@Override
	public User getUserById(Long id) throws UserException {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public Set<User> getUserByRole(UserRole role) throws UserException {
		return userRepository.findByRole(role);
	}

	@Override
	public User getCurrentUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user= userRepository.findByEmail(email);
		if(user == null) {
			throw new EntityNotFoundException("User not found");
		}
		return user;
	}

	// Super Admin methods

	// @Override
	// public UserDTO createAdmin(UserDTO userDTO) throws UserException {
	// 	User createdUser = UserMapper.toEntity(userDTO);
	// 	createdUser.setEmail(userDTO.getEmail());
	// 	createdUser.setPassword(passwordEncoder.encode(createdUser.getPassword()));
	// 	createdUser.setPhone(userDTO.getPhone());
	// 	createdUser.setFullName(userDTO.getFullName());
	// 	createdUser.setLastLogin(LocalDateTime.now());
	// 	createdUser.setCreatedAt(LocalDateTime.now());
	// 	createdUser.setRole(UserRole.ROLE_ADMIN);
	// 	userRepository.save(createdUser);
	// 	return UserMapper.toDTO(createdUser);
	// }


	@Override
	public void deleteUser(Long userId) throws UserException {
        // Find the user or throw an exception if they don't exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with ID: " + userId));
        
        // // Ensure the target user is actually a user
        // if (user.getRole() != UserRole.ROLE_USER) {
        //     throw new UserException("Invalid request: You can only delete accounts with the USER role.");
        // }
        
        // Delete the user
        userRepository.delete(user);
    }


	// @Override
	// public UserDTO updateUserByAdmin(Long userId, UserDTO userDTO) throws UserException {
	// 	User existingUser = getUserById(userId);
	// 	if (existingUser == null) {
	// 		throw new UserException("User not found with ID: " + userId);
	// 	}
	// 	// Only update allowed profile fields
	// 	// Do NOT update Role, Email, or Password through this basic profile update
	// 	if (userDTO.getFullName() != null) {
	// 		existingUser.setFullName(userDTO.getFullName());
	// 	}
	// 	if (userDTO.getPhone() != null) {
	// 		existingUser.setPhone(userDTO.getPhone());
	// 	}

	// 	// Update the user entity
	// 	userMapper.updateEntityFromDTO(existingUser, userDTO);

	// 	User updatedUser = userRepository.save(existingUser);
	// 	return UserMapper.toDTO(updatedUser);
	// }

	@Override
	public List<User> getUsers() throws UserException {
		return userRepository.findAll();
	}

	@Override
	public long getTotalUserCount() {
		return userRepository.findByRole(UserRole.ROLE_USER).size();
	}

	
	
}
