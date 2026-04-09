package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.payload.dto.UserDTO;
import com.example.Library_Management_System.payload.request.ForgotPasswordRequest;
import com.example.Library_Management_System.payload.request.LoginRequest;
import com.example.Library_Management_System.payload.request.ResetPasswordRequest;
import com.example.Library_Management_System.payload.response.ApiResponse;
import com.example.Library_Management_System.payload.response.AuthResponse;
// import com.example.Library_Management_System.payload.request.LoginRequest;


import com.example.Library_Management_System.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ==================== CRUD OPERATIONS ====================

    /**
     * Update a book
     * PUT /api/books/{id}
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signupHandler(@RequestBody @Valid UserDTO req) throws UserException {
        AuthResponse response=authService.signup(req);

        return ResponseEntity.ok(response);
    }

    /**
     * Login a user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req) throws UserException {
        AuthResponse response=authService.login(req.getEmail(), req.getPassword());

        return ResponseEntity.ok(response);
    }

    /**
     * Request a password reset
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) throws UserException {
        // Create a password reset token for the user with the provided email
        authService.createPasswordResetToken(request.getEmail());
        // Return a success response indicating that a reset link was sent to the user's email
        ApiResponse res= new ApiResponse("A Reset link was sent to your email.",true);
        return ResponseEntity.ok(res);
    }

    /**
     * Reset password using a token
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getPassword());
        ApiResponse res= new ApiResponse(
                "Password reset successful",true
        );
        return ResponseEntity.ok(res);
    }
}