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

            // 3. STATELESS SESSION (No cookies, use JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4. AUTHORIZE REQUESTS
            .authorizeHttpRequests(auth -> auth
    // 1. PUBLIC ENDPOINTS
    .requestMatchers("/", "/error", "/auth/**").permitAll()
    .requestMatchers("/api/patient/login", "/api/patient/signup").permitAll()
    .requestMatchers("/api/clinics/**", "/api/doctors/**", "/doctors/**").permitAll()
    
    // 2. ADMIN ACCESS (Unified to use 'hasRole')
    // Note: ensure your JwtFilter adds "ROLE_" prefix to the authority
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    
    // 3. DOCTOR ACCESS
    .requestMatchers("/doctor/dashboard/**", "/doctor/update/**").hasRole("DOCTOR")
    .requestMatchers("/doctor/**").hasAnyRole("DOCTOR", "ADMIN")
    
    // 4. PATIENT ACCESS
    .requestMatchers("/api/patient/**", "/patient/**").hasRole("PATIENT")
    
    // 5. SHARED ACCESS
    .requestMatchers("/appointments/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
    
    .anyRequest().authenticated()
)
            // 5. ADD JWT FILTER
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🌐 CORS Configuration: Allows React (3000) to communicate with Spring (8080)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ Add your Render Frontend URL here alongside localhost
        configuration.setAllowedOrigins(Arrays.asList(
        	    "http://localhost:3000",
        	    "http://localhost:5173",
        	    "https://nexhealth-frontend.vercel.app/" // Ensure this matches your Render Dashboard URL
        	));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        
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
