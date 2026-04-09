package com.example.Library_Management_System.payload.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

