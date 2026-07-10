package com.finflow.auth.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "jwt.access-token")
public class JwtProperties {

    private String secret;

    private long expiration;

}