package com.finflow.auth.service.interfaces;

import java.util.UUID;

import com.finflow.auth.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(UUID userId);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(UUID userId);
}