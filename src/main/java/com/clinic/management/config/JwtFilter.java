package com.clinic.management.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 1. Skip filter for /auth endpoints or if header is missing/invalid
        if (request.getServletPath().contains("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Validate Header Format
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.error("Could not extract username from token: {}", e.getMessage());
            }
        }

        // 3. Authenticate the user if token is valid and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Check if token is valid against the database user
                if (jwtUtil.validateToken(token, userDetails)) {
                    
                    // We use userDetails.getAuthorities() which pulls ROLE_ADMIN/ROLE_DOCTOR 
                    // from your CustomUserDetailsService
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Finalize authentication in Spring Security Context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.info("Successfully authenticated user: {} with roles: {}", 
                                username, userDetails.getAuthorities());
                } else {
                    logger.warn("Token validation failed for user: {}", username);
                }
            } catch (UsernameNotFoundException e) {
                logger.warn("User not found in database: {}", username);
            } catch (Exception e) {
                logger.error("Security Context setting error: ", e);
            }
        }

        // 4. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}