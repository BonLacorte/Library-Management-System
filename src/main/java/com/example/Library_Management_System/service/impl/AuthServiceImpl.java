package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.repository.PasswordResetTokenRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Library_Management_System.configurations.JwtProvider;
import com.example.Library_Management_System.domain.UserRole;
import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.mapper.UserMapper;
import com.example.Library_Management_System.modal.PasswordResetToken;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.dto.UserDTO;
import com.example.Library_Management_System.payload.response.AuthResponse;
import com.example.Library_Management_System.repository.UserRepository;
import com.example.Library_Management_System.service.AuthService;
import com.example.Library_Management_System.service.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserImplementation customUserImplementation;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    // @Value("${app.frontend.reset-url}")
    // private String frontendResetUrl;
    private String frontendResetUrl = "http://localhost:5173";


    /**
     * Login user
     * POST /auth/login
     */
    @Override
    public AuthResponse login(String username, String password) throws UserException {

        // Authenticate user by username and password through CustomUserImplementation which implements UserDetailsService
        // The value of authentication is UsernamePasswordAuthenticationToken which is a implementation of Authentication interface
        Authentication authentication = authenticate(username, password);
        // Set authentication in SecurityContextHolder, SecurityContextHolder is used for 
        // storing authentication information after successful authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Get user authorities, authorities are roles or permissions that user has
        // which can get from authentication object
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role =  authorities.iterator().next().getAuthority();
        // Generate JWT token based on authentication object
        String token = jwtProvider.generateToken(authentication);

        User user = userRepository.findByEmail(username);

        // Update last login time
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setTitle("Login success");
        response.setMessage("Welcome Back" + username);
        response.setJwt(token);
        response.setUser(UserMapper.toDTO(user));

        return response;
    }

    /**
     * Authenticate user by email and password
     * 
     */
    public Authentication authenticate(String email, String password) throws UserException {
        // Load user details by email from CustomUserImplementation which implements UserDetailsService
        UserDetails userDetails = customUserImplementation.loadUserByUsername(email);
        if(userDetails == null) {
            throw new UserException("email id doesn't exist "+ email);
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UserException("Wrong Password ");
        }
        return new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
    }

    /**
     * Register user
     * POST /auth/register
     */
    @Override
    public AuthResponse signup(UserDTO req) throws UserException {
        User user = userRepository.findByEmail(req.getEmail());
        if(user != null) {
            throw new UserException("Email id already registered ");
        }

        // if(req.getRole().equals(UserRole.ROLE_ADMIN)){
        //    throw new UserException("Role admin is not allowed");
        // }


        User createdUser = new User();
        createdUser.setEmail(req.getEmail());
        createdUser.setPassword(passwordEncoder.encode(req.getPassword()));
        createdUser.setPhone(req.getPhone());
        createdUser.setFullName(req.getFullName());
        createdUser.setLastLogin(LocalDateTime.now());
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setRole(UserRole.ROLE_USER);


        User savedUser = userRepository.save(createdUser);
        //        UserDTO userDTO=new UserDTO();
        //        userDTO.setEmail(savedUser.getEmail());
        //        userDTO.setFullName(savedUser.getFullName());
        //        userDTO.setId(savedUser.getId());

        //        userEventProducer.userCreatedEvent(userDTO);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse response = new AuthResponse();
        response.setTitle("Welcome " + createdUser.getEmail());
        response.setMessage("Register success");
        response.setUser(UserMapper.toDTO(savedUser));
        response.setJwt(jwt);
        return response;
    }

    /**
     * Create password reset token
     * POST /auth/reset-password
     */
    @Override
    @Transactional
    public void createPasswordResetToken(String email) throws UserException  {
        User user = userRepository.findByEmail(email);

        // Always return/give same response to caller to avoid enumeration attacks.
        if (user==null) {
            throw new UserException("user not found with given email");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // 5 minutes expiry
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink =  frontendResetUrl + token;
        String subject = "Password Reset Request";
        String body = "You requested to reset your password. Use this link (valid 5 minutes): " + resetLink;

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    /**
     * For reset password
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {

        // Find token in DB and check if it exists 
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);
        
        //Check if token exists
        if (optionalToken.isEmpty()) {
            throw new BadCredentialsException("Invalid or expired token");
        }

        // Get token from Optional 
        PasswordResetToken resetToken = optionalToken.get();

        // Check if token is expired
        if (resetToken.isExpired()) {
            // token expired — delete it
            passwordResetTokenRepository.delete(resetToken);
            throw new BadCredentialsException("Invalid or expired token");
        }
        
        // Get user from token
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // delete token after successful reset
        passwordResetTokenRepository.delete(resetToken);
    }
}
