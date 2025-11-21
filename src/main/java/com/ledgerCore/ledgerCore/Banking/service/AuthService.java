    package com.ledgerCore.ledgerCore.Banking.service;

    import com.ledgerCore.ledgerCore.Banking.entity.*;
    import com.ledgerCore.ledgerCore.Banking.repo.*;
    import com.ledgerCore.ledgerCore.Banking.Security.JwtUtil;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.bind.annotation.PostMapping;

    import java.security.SecureRandom;
    import java.time.LocalDateTime;
    import java.util.Base64;
    import java.util.Optional;

    @Service
    public class AuthService   {

        private final SecureRandom secureRandom = new SecureRandom();

        @Autowired private UserRepo userRepo;
        @Autowired private RefreshTokenRepo refreshTokenRepo;
        @Autowired private BlacklistedTokenRepo blacklistedTokenRepo;
        @Autowired private JwtUtil jwtUtil;
        @Autowired private PasswordEncoder passwordEncoder;

        private String generateOpaqueToken() {
            byte[] bytes = new byte[64];
            secureRandom.nextBytes(bytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        }


        @Transactional
        public AuthResponse login(String email, String password, boolean rotateRefresh) {
            AppUser user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid credentials"));
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            String access = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());

            // Optional: delete old refresh tokens for user (single active token policy)
            refreshTokenRepo.deleteByUser(user);

            String refresh = generateOpaqueToken();
            LocalDateTime expires = LocalDateTime.now().plusDays(30); // refresh token valid 30 days

            RefreshToken rt = new RefreshToken(refresh, user, expires);
            refreshTokenRepo.save(rt);

            return new AuthResponse(access, refresh, jwtUtil.getExpirationDate(access).getTime());
        }


        @Transactional
        public AuthResponse refresh(String refreshTokenValue) {
            RefreshToken rt = refreshTokenRepo.findByToken(refreshTokenValue)
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            if (rt.isRevoked() || rt.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Refresh token expired or revoked");
            }

            AppUser user = rt.getUser();

            // ROTATE: revoke current refresh token and issue a new one
            rt.setRevoked(true);
            refreshTokenRepo.save(rt);

            String newRefresh = generateOpaqueToken();
            RefreshToken newRt = new RefreshToken(newRefresh, user, LocalDateTime.now().plusDays(30));
            refreshTokenRepo.save(newRt);

            String access = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());

            return new AuthResponse(access, newRefresh, jwtUtil.getExpirationDate(access).getTime());
        }


        @Transactional
        public void logout(String accessToken, String refreshToken) {
            // Blacklist access token (store until its expiry)
            if (accessToken != null && !accessToken.isBlank()) {
                LocalDateTime expiry = jwtUtil.getExpirationDate(accessToken).toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                BlacklistedToken bt = new BlacklistedToken(accessToken, expiry);
                blacklistedTokenRepo.save(bt);
            }

            if (refreshToken != null && !refreshToken.isBlank()) {
                refreshTokenRepo.findByToken(refreshToken).ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepo.save(rt);
                });
            }
        }

        public boolean isBlacklisted(String token) {
            return blacklistedTokenRepo.findByToken(token).isPresent();
        }


        @Transactional
        public AppUser registerUser(AppUser u) {
            // Check if email already exists
            if (userRepo.findByEmail(u.getEmail()).isPresent()) {
                throw new RuntimeException("Email already registered");
            }
            u.setPassword(passwordEncoder.encode(u.getPassword()));
            u.setStatus("ACTIVE");
            // Set default role to USER if not provided
            if (u.getRole() == null) {
                u.setRole(Role.ROLE_USER);
            }
            return userRepo.save(u);
        }

        // DTO for responses
        public static class AuthResponse {
            private String accessToken;
            private String refreshToken;
            private long accessTokenExpiryEpochMs;

            public AuthResponse(String accessToken, String refreshToken, long accessTokenExpiryEpochMs) {
                this.accessToken = accessToken; this.refreshToken = refreshToken; this.accessTokenExpiryEpochMs = accessTokenExpiryEpochMs;
            }
            // getters
            public String getAccessToken() { return accessToken; }
            public String getRefreshToken() { return refreshToken; }
            public long getAccessTokenExpiryEpochMs() { return accessTokenExpiryEpochMs; }
        }
    }
