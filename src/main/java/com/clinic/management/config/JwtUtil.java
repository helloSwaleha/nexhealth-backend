package com.clinic.management.config;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${jwt.secret:MyDefaultSecretKeyMyDefaultSecretKey123456}")
    private String secret;

    private Key getSecretKey() {
        try {
            return Keys.hmacShaKeyFor(secret.getBytes());
        } catch (WeakKeyException e) {
            throw new IllegalArgumentException("JWT secret key is too weak. Use at least 256 bits.", e);
        }
    }

    @PostConstruct
    public void checkSecret() {
        System.out.println("JWT Secret Loaded: " + secret.length() + " chars");
    }

    private static final long JWT_TOKEN_VALIDITY = 10 * 60 * 60 * 1000; // 10 hours

    // Main logic for generating token
    public String generateToken(String username, String role) {
        if (username == null || role == null) {
            throw new IllegalArgumentException("Username and role cannot be null");
        }
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // FIXED: Now this actually generates a token instead of returning null
    public String generateToken(UserDetails userDetails) {
        // Extract the first role/authority from userDetails
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_PATIENT");
        
        return generateToken(userDetails.getUsername(), role);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}