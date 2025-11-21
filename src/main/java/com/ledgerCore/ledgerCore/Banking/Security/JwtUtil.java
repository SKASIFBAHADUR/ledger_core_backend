package com.ledgerCore.ledgerCore.Banking.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // put in config/env (must be 256-bit for HS256)
    private final String SECRET = System.getenv().getOrDefault("JWT_SECRET",
            "super-long-secret-must-be-32-bytes-min-change-me-for-prod-0123456789");
    private final long ACCESS_EXP_MS = 1000L * 60 * 10; // 10 minutes

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateAccessToken(String email, String role) {
        String fixedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return Jwts.builder()
                .setSubject(email)
                .claim("role", fixedRole)  // ALWAYS ROLE_*
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXP_MS))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
    }

    public String extractEmail(String token) {
        return parse(token).getBody().getSubject();
    }

    public Date getExpirationDate(String token) {
        return parse(token).getBody().getExpiration();
    }
}
