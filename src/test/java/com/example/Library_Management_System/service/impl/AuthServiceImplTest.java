package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.configurations.JwtProvider;
import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.modal.PasswordResetToken;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.dto.UserDTO;
import com.example.Library_Management_System.payload.response.AuthResponse;
import com.example.Library_Management_System.repository.PasswordResetTokenRepository;
import com.example.Library_Management_System.repository.UserRepository;
import com.example.Library_Management_System.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CustomUserImplementation customUserImplementation;


    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User dummyUser;
    private UserDTO dummyUserDTO;
    private UserDetails dummyUserDetails;
    private PasswordResetToken dummyResetToken;

    @BeforeEach
    void setUp() {
        // Inject the @Value property using ReflectionTestUtils
        ReflectionTestUtils.setField(authService, "frontendResetUrl", "http://localhost:3000/reset/");

        dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setEmail("test@example.com");
        dummyUser.setPassword("encodedPassword");
        dummyUser.setFullName("Test User");
        dummyUser.setRole(UserRole.ROLE_USER);

        dummyUserDTO = new UserDTO();
        dummyUserDTO.setEmail("test@example.com");
        dummyUserDTO.setPassword("rawPassword");
        dummyUserDTO.setFullName("Test User");

        dummyUserDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com",
                "encodedPassword",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        dummyResetToken = PasswordResetToken.builder()
                .id(1L)
                .token("sample-uuid-token")
                .user(dummyUser)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    // ==================== SIGNUP TESTS ====================

    @Test
    void testSignup_Success() throws UserException {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn("mockJwtToken");

        // ACT
        AuthResponse response = authService.signup(dummyUserDTO);

        // ASSERT
        assertNotNull(response);
        assertEquals("Register success", response.getMessage());
        assertEquals("mockJwtToken", response.getJwt());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSignup_WhenEmailAlreadyExists_ShouldThrowException() {
        // ARRANGE
        when(userRepository.findByEmail(dummyUserDTO.getEmail())).thenReturn(dummyUser);

        // ACT & ASSERT
        UserException exception = assertThrows(UserException.class, () -> {
            authService.signup(dummyUserDTO);
        });

        assertEquals("Email id already registered ", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== LOGIN & AUTHENTICATE TESTS ====================

    @Test
    void testLogin_Success() throws UserException {
        // ARRANGE
        when(customUserImplementation.loadUserByUsername("test@example.com")).thenReturn(dummyUserDetails);
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn("mockJwtToken");
        when(userRepository.findByEmail("test@example.com")).thenReturn(dummyUser);

        // ACT
        AuthResponse response = authService.login("test@example.com", "rawPassword");

        // ASSERT
        assertNotNull(response);
        assertEquals("Login success", response.getTitle());
        assertEquals("mockJwtToken", response.getJwt());
        verify(userRepository, times(1)).save(dummyUser); // Verifies lastLogin was updated
    }

    @Test
    void testAuthenticate_WhenUserNotFound_ShouldThrowException() {
        // ARRANGE
        when(customUserImplementation.loadUserByUsername("unknown@example.com")).thenReturn(null);

        // ACT & ASSERT
        UserException exception = assertThrows(UserException.class, () -> {
            authService.authenticate("unknown@example.com", "password");
        });

        assertTrue(exception.getMessage().contains("email id doesn't exist"));
    }

    @Test
    void testAuthenticate_WhenWrongPassword_ShouldThrowException() {
        // ARRANGE
        when(customUserImplementation.loadUserByUsername("test@example.com")).thenReturn(dummyUserDetails);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // ACT & ASSERT
        UserException exception = assertThrows(UserException.class, () -> {
            authService.authenticate("test@example.com", "wrongPassword");
        });

        assertEquals("Wrong Password ", exception.getMessage());
    }

    // ==================== PASSWORD RESET TESTS ====================

    @Test
    void testCreatePasswordResetToken_Success() throws UserException {
        // ARRANGE
        when(userRepository.findByEmail("test@example.com")).thenReturn(dummyUser);

        // ACT
        authService.createPasswordResetToken("test@example.com");

        // ASSERT
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository, times(1)).save(tokenCaptor.capture());
        
        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getToken());
        assertEquals(dummyUser, savedToken.getUser());

        verify(emailService, times(1)).sendEmail(
                eq("test@example.com"),
                eq("Password Reset Request"),
                contains("http://localhost:3000/reset/" + savedToken.getToken())
        );
    }

    @Test
    void testCreatePasswordResetToken_WhenUserNotFound_ShouldThrowException() {
        // ARRANGE
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        // ACT & ASSERT
        UserException exception = assertThrows(UserException.class, () -> {
            authService.createPasswordResetToken("unknown@example.com");
        });

        assertEquals("user not found with given email", exception.getMessage());
        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testResetPassword_Success() {
        // ARRANGE
        when(passwordResetTokenRepository.findByToken("sample-uuid-token")).thenReturn(Optional.of(dummyResetToken));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        // ACT
        authService.resetPassword("sample-uuid-token", "newPassword123");

        // ASSERT
        assertEquals("newEncodedPassword", dummyUser.getPassword());
        verify(userRepository, times(1)).save(dummyUser);
        verify(passwordResetTokenRepository, times(1)).delete(dummyResetToken);
    }

    @Test
    void testResetPassword_WhenTokenIsInvalid_ShouldThrowException() {
        // ARRANGE
        when(passwordResetTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // ACT & ASSERT
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.resetPassword("invalid-token", "newPassword123");
        });

        assertEquals("Invalid or expired token", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testResetPassword_WhenTokenIsExpired_ShouldThrowExceptionAndDeleteToken() {
        // ARRANGE
        dummyResetToken.setExpiryDate(LocalDateTime.now().minusMinutes(10)); // Set token to expired
        when(passwordResetTokenRepository.findByToken("sample-uuid-token")).thenReturn(Optional.of(dummyResetToken));

        // ACT & ASSERT
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.resetPassword("sample-uuid-token", "newPassword123");
        });

        assertEquals("Invalid or expired token", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepository, times(1)).delete(dummyResetToken); // Ensures cleanup logic fires
    }
}
