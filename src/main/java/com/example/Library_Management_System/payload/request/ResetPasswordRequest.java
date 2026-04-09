package com.example.Library_Management_System.payload.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String password;
}
