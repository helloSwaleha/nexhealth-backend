package com.clinic.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORS & CSRF
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable())
            
            // 2. DISABLE FORM LOGIN & HTTP BASIC
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 3. STATELESS SESSION
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4. AUTHORIZE REQUESTS
            .authorizeHttpRequests(auth -> auth
                // PUBLIC ENDPOINTS
                .requestMatchers("/", "/error", "/auth/**").permitAll()
                .requestMatchers("/api/patient/login", "/api/patient/signup").permitAll()
                .requestMatchers("/api/clinics/**",  "/doctors/**").permitAll()
                .requestMatchers("/api/doctors/clinic/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/doctors/**").permitAll()
                
                // ADMIN ACCESS
                // We use hasAnyAuthority to match "ADMIN" or "ROLE_ADMIN" exactly as it is in your DB
                .requestMatchers("/admin/**", "/api/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/doctors/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                
                // DOCTOR ACCESS
                .requestMatchers("/doctor/dashboard/**", "/doctor/update/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR")
                .requestMatchers("/doctor/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")
                
                // PATIENT ACCESS
                .requestMatchers("/api/patient/**", "/patient/**").hasAnyAuthority("PATIENT", "ROLE_PATIENT")
                
                // SHARED ACCESS
                .requestMatchers("/appointments/**").hasAnyAuthority("PATIENT", "ROLE_PATIENT", "DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")
                
                .anyRequest().authenticated()
            )

            // 5. ADD JWT FILTER
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Removed trailing slash from Vercel URL and added common variations
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://nexhealth-frontend.vercel.app" 
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allowed all common headers to prevent pre-flight 403s
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        // Important for the frontend to read the token if sent in headers
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
