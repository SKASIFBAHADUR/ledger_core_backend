package com.ledgerCore.ledgerCore.Banking.Security;

import com.ledgerCore.ledgerCore.Banking.entity.AppUser;
import com.ledgerCore.ledgerCore.Banking.repo.BlacklistedTokenRepo;
import com.ledgerCore.ledgerCore.Banking.repo.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final BlacklistedTokenRepo blacklistedTokenRepo;

    public JwtFilter(JwtUtil jwtUtil, UserRepo userRepo, BlacklistedTokenRepo blacklistedTokenRepo) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.blacklistedTokenRepo = blacklistedTokenRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 🔓 Completely public endpoints (no JWT needed)
        if (uri.startsWith("/auth/register")
                || uri.startsWith("/auth/login")
                || uri.startsWith("/auth/refresh")
                || uri.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // 😴 No Authorization header → just continue, SecurityConfig will decide
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        // Check blacklist
        boolean isBlacklisted = blacklistedTokenRepo.findByToken(token).isPresent();
        if (isBlacklisted || !jwtUtil.isValid(token)) {
            // invalid or blacklisted token, don't authenticate
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtUtil.extractEmail(token);

        AppUser appUser = userRepo.findByEmail(email).orElse(null);

        if (appUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            appUser,
                            null,
                            appUser.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
