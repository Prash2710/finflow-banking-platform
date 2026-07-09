package com.finflow.auth.service.impl;

import com.finflow.auth.dto.request.RegisterRequest;
import com.finflow.auth.dto.response.RegisterResponse;
import com.finflow.auth.entity.Role;
import com.finflow.auth.entity.User;
import com.finflow.auth.exception.EmailAlreadyExistsException;
import com.finflow.auth.exception.UsernameAlreadyExistsException;
import com.finflow.auth.repository.RoleRepository;
import com.finflow.auth.repository.UserRepository;
import com.finflow.auth.service.interfaces.AuthService;
import com.finflow.auth.util.DateTimeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    
    @Override
    public RegisterResponse register(RegisterRequest request) {

    	if (userRepository.existsByUsername(request.getUsername())) {
    	    throw new UsernameAlreadyExistsException(
    	            "Username already exists.");
    	}
    	
    	if (userRepository.existsByEmail(request.getEmail())) {
    	    throw new EmailAlreadyExistsException(
    	            "Email already exists.");
    	}
    	
    	Role customerRole = roleRepository.findByRoleName("CUSTOMER")
    	        .orElseThrow(() -> new RuntimeException("Customer role not found"));
    	
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
    	user.setCreatedAt(DateTimeUtil.now());

    	userRepository.save(user);
    	
    	return RegisterResponse.builder()
    	        .message("User registered successfully")
    	        .username(user.getUsername())
    	        .email(user.getEmail())
    	        .build();

    }
}