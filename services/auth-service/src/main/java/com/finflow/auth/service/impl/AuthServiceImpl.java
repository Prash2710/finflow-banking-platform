package com.finflow.auth.service.impl;

import com.finflow.auth.constants.RoleConstants;
import com.finflow.auth.constants.SecurityConstants;
import com.finflow.auth.dto.request.LoginRequest;
import com.finflow.auth.dto.request.RefreshTokenRequest;
import com.finflow.auth.dto.request.RegisterRequest;
import com.finflow.auth.dto.response.LoginResponse;
import com.finflow.auth.dto.response.RegisterResponse;
import com.finflow.auth.entity.RefreshToken;
import com.finflow.auth.entity.Role;
import com.finflow.auth.entity.User;
import com.finflow.auth.exception.EmailAlreadyExistsException;
import com.finflow.auth.exception.RoleNotFoundException;
import com.finflow.auth.exception.UsernameAlreadyExistsException;
import com.finflow.auth.repository.RoleRepository;
import com.finflow.auth.repository.UserRepository;
import com.finflow.auth.security.CustomUserDetails;
import com.finflow.auth.security.jwt.JwtService;
import com.finflow.auth.service.interfaces.AuthService;
import com.finflow.auth.service.interfaces.RefreshTokenService;
import com.finflow.auth.util.DateTimeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;
    
    private final JwtService jwtService;
    
    private final RefreshTokenService refreshTokenService;
    
    
    
    @Override
    public RegisterResponse register(RegisterRequest request) {

        // Step 1 - Incoming request
        log.info("Registration request received for email: {}", request.getEmail());

        // Step 2 - Username validation
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists.");
        }

        // Step 3 - Email validation
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        // Step 4 - Get CUSTOMER role
        Role customerRole = roleRepository.findByRoleName(RoleConstants.CUSTOMER)
                .orElseThrow(() ->
                        new RoleNotFoundException("Default CUSTOMER role not found"));

        // Step 5 - Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .role(customerRole)
                .build();

        user.setId(UUID.randomUUID());
        user.setCreatedAt(LocalDateTime.now());

        // Step 6 - Save
        userRepository.save(user);

        // ✅ Step 7 - Log success
        log.info("User {} registered successfully", user.getEmail());

        // Step 8 - Return response
        return RegisterResponse.builder()
                .message("User registered successfully")
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(

                                request.getEmail(),

                                request.getPassword()

                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken =
                jwtService.generateToken(userDetails.getUsername());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        userDetails.getUser().getId());


        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(900L)
                .build();
    }
    
    @Override
    public LoginResponse refreshToken(
            RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(
                        request.getRefreshToken());

        String accessToken =
                jwtService.generateToken(
                        refreshToken.getUser().getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType(SecurityConstants.BEARER.trim())
                .expiresIn(900L)
                .build();
    }
    
    @Override
    public void logout(UUID userId) {

        refreshTokenService.revokeRefreshToken(userId);

        log.info("User {} logged out successfully.", userId);
    }
}