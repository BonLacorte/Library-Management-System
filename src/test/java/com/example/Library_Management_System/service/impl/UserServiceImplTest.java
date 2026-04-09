package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.configurations.JwtProvider;
import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User dummyUser;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setEmail("johndoe@example.com");
        dummyUser.setFullName("John Doe");
        dummyUser.setPassword("encodedPassword123");
        dummyUser.setRole(UserRole.ROLE_USER);
    }

    // ==================== READ OPERATIONS ====================

    @Test
    void testgetUserFromJwtToken_Success() throws UserException {
        // ARRANGE
        String mockJwt = "Bearer mock-jwt-token";
        when(jwtProvider.getEmailFromJwtToken(mockJwt)).thenReturn("johndoe@example.com");
        when(userRepository.findByEmail("johndoe@example.com")).thenReturn(dummyUser);

        // ACT
        User result = userService.getUserFromJwtToken(mockJwt);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("johndoe@example.com", result.getEmail());
        verify(jwtProvider, times(1)).getEmailFromJwtToken(mockJwt);
        verify(userRepository, times(1)).findByEmail("johndoe@example.com");
    }

    @Test
    void testgetUserById_Success() throws UserException {
        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));

        // ACT
        User result = userService.getUserById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testgetUsers_ShouldReturnList() throws UserException {
        // ARRANGE
        List<User> userList = Arrays.asList(dummyUser);
        when(userRepository.findAll()).thenReturn(userList);

        // ACT
        List<User> result = userService.getUsers();

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("johndoe@example.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    
}