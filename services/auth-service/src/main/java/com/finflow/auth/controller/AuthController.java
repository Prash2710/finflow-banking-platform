package com.finflow.auth.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import com.finflow.auth.common.ApiResponse;
import com.finflow.auth.dto.request.LoginRequest;
import com.finflow.auth.dto.request.RefreshTokenRequest;
import com.finflow.auth.dto.request.RegisterRequest;
import com.finflow.auth.dto.response.LoginResponse;
import com.finflow.auth.dto.response.RegisterResponse;
import com.finflow.auth.security.CustomUserDetails;
import com.finflow.auth.service.interfaces.AuthService;
import com.finflow.auth.util.DateTimeUtil;
import com.finflow.auth.constants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiConstants.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        ApiResponse<RegisterResponse> apiResponse =
                ApiResponse.<RegisterResponse>builder()
                        .success(true)
                        .message("Registration Successful")
                        .timestamp(DateTimeUtil.now())
                        .data(response)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        ApiResponse<LoginResponse> apiResponse =
                ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Login Successful")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build();

        return ResponseEntity.ok(apiResponse);

    }
    
    @GetMapping("/profile")
    public ResponseEntity<String> profile(Authentication authentication) {

        return ResponseEntity.ok(
                "Welcome " + authentication.getName());

    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(

            @Valid
            @RequestBody RefreshTokenRequest request) {

        LoginResponse response =
                authService.refreshToken(request);

        return ResponseEntity.ok(

                ApiResponse.success(
                        "Token Refreshed Successfully",
                        response));

    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            Authentication authentication) {

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        authService.logout(user.getUserId());

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Logged out successfully")
                        .timestamp(LocalDateTime.now())
                        .data("Success")
                        .build());
    }

}