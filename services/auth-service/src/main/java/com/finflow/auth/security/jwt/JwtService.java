package com.finflow.auth.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private Key signingKey() {

        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret()
                        .getBytes(StandardCharsets.UTF_8));

    }

    public String generateToken(String email) {
    	


        Date now = new Date();

        Date expiry =
                new Date(now.getTime()
                        + jwtProperties.getExpiration());

        return Jwts.builder()

                .subject(email)

                .issuedAt(now)

                .expiration(expiry)

                .signWith(signingKey())

                .compact();

    }
    
    private Claims extractAllClaims(String token) {

        return Jwts.parser()

                .verifyWith((javax.crypto.SecretKey) signingKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
    
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);

    }
    
    public <T> T extractClaim(String token,
            Function<Claims, T> claimsResolver) {

Claims claims = extractAllClaims(token);

return claimsResolver.apply(claims);
}
//====================================================================================================================    
    public boolean isTokenExpired(String token) {

        return extractClaim(token, Claims::getExpiration)
                .before(new Date());

    }
    
    public boolean isTokenValid(String token, String email) {

			return extractUsername(token).equals(email)
			&& !isTokenExpired(token);

}
//====================================================================================================================
}