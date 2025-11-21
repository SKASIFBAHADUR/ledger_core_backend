package com.ledgerCore.ledgerCore.Banking.controller;

import com.ledgerCore.ledgerCore.Banking.entity.AppUser;
import com.ledgerCore.ledgerCore.Banking.service.AuthService;
import com.ledgerCore.ledgerCore.Banking.service.AuthService.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthService authService;

    // Login — returns access + refresh tokens (refresh stored as httpOnly cookie recommended)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String password) {

        AuthResponse r = authService.login(email, password, true);

        // Set httpOnly secure cookie for refresh token (front-end should send with credentials)
        // secure(false) for development on localhost (set to true in production with HTTPS)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", r.getRefreshToken())
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(30 * 24 * 60 * 60) // 30 days
                .sameSite("Lax") // Changed from Strict to Lax for better compatibility
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new AccessPayload(r.getAccessToken(), r.getAccessTokenExpiryEpochMs()));
    }

    // Refresh endpoint — reads refresh token from cookie (or request body)
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshCookie,
                                     @RequestBody(required = false) RefreshRequest body) {

        String refresh = refreshCookie;
        if ((refresh == null || refresh.isBlank()) && body != null) refresh = body.getRefreshToken();

        AuthResponse r = authService.refresh(refresh);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", r.getRefreshToken())
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .sameSite("Lax") // Changed from Strict to Lax for better compatibility
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new AccessPayload(r.getAccessToken(), r.getAccessTokenExpiryEpochMs()));
    }

    // Logout — blacklist access token and revoke refresh (cookie + optional body)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value="Authorization", required = false) String authorization,
                                    @CookieValue(value = "refreshToken", required = false) String refreshCookie) {

        String accessToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            accessToken = authorization.substring(7);
        }
        authService.logout(accessToken, refreshCookie);

        // Clear cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax").build();

        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body("Logged out");
    }

    // Register endpoint for public registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUser u) {
        AppUser saved = authService.registerUser(u);
        return ResponseEntity.ok(saved);
    }

    // Admin-only: create employee user
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create-user")
    public ResponseEntity<?> adminCreate(@RequestBody AppUser u) {
        AppUser saved = authService.registerUser(u);
        return ResponseEntity.ok(saved);
    }

    static class AccessPayload {
        public String accessToken;
        public long expiresAt;
        public AccessPayload(String t, long e) { this.accessToken = t; this.expiresAt = e; }
    }

    static class RefreshRequest {
        private String refreshToken;
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}
