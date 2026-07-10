package com.finflow.auth.service.interfaces;

import java.util.UUID;

import com.finflow.auth.dto.request.LoginRequest;
import com.finflow.auth.dto.request.RefreshTokenRequest;
import com.finflow.auth.dto.request.RegisterRequest;
import com.finflow.auth.dto.response.LoginResponse;
import com.finflow.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
    
    LoginResponse refreshToken(RefreshTokenRequest request);
    
    void logout(UUID userId);

}