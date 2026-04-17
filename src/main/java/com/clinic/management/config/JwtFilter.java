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
        String path = request.getServletPath();

        // 1. Skip filter for public endpoints
        if (path.contains("/auth/") || path.equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Validate Header Format
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.error("JWT Error for path {}: {}", path, e.getMessage());
            }
        }

        // 3. Authenticate the user if token is valid and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    // Create authentication token with user's authorities (roles)
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
                    
                    // ✅ DIAGNOSTIC LOG: This is critical. Check your Render logs for this!
                    logger.info("AUTH SUCCESS: User [{}] Path [{}] Roles {}", 
                                username, path, userDetails.getAuthorities());
                } else {
                    logger.warn("AUTH FAILED: Token invalid for user [{}] on path [{}]", username, path);
                }
            } catch (UsernameNotFoundException e) {
                logger.warn("AUTH FAILED: User not found [{}]", username);
            } catch (Exception e) {
                logger.error("AUTH ERROR: Internal security error: ", e);
            }
        }

        // 4. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
