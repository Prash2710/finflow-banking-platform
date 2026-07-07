package com.finflow.auth.service.interfaces;

import com.finflow.auth.dto.request.RegisterRequest;
import com.finflow.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

}