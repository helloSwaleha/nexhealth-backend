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
                // ✅ PUBLIC ENDPOINTS: Accessible without a token
            		.requestMatchers("/").permitAll() // Allows the root URL to be viewed
                .requestMatchers("/api/patient/login").permitAll() 
                .requestMatchers("/api/patient/signup").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/error").permitAll() 
                
                // 🔹 FIX: Move Clinics to public so the UI can load them immediately
                .requestMatchers("/api/clinics/**").permitAll() 
                
                // 🔐 APPOINTMENT ACCESS: Requires login
                .requestMatchers("/api/appointments/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                .requestMatchers("/appointments/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                .requestMatchers("/appointments/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")

                // 🔐 PATIENT ACCESS
                .requestMatchers("/api/patient/**").hasRole("PATIENT")
                .requestMatchers("/api/patient/prescriptions/**").hasRole("PATIENT")
                .requestMatchers("/patient/**").hasRole("PATIENT")
                .requestMatchers("/api/doctors/clinic/**").permitAll()
                .requestMatchers("/api/doctors/**").permitAll()
                .requestMatchers("/doctors/**").permitAll()
                .requestMatchers("/api/doctors/clinic/**").permitAll()
             // 1. Allow everyone (or at least Patients) to VIEW doctor details
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/doctor/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                
                // 🔐 DOCTOR ACCESS
                .requestMatchers("/doctor/**").hasRole("DOCTOR")
             // 2. Keep DOCTOR specific actions (like updating profile) restricted to Doctors
                .requestMatchers("/doctor/update/**", "/doctor/dashboard/**").hasRole("DOCTOR")
                
                // 🔐 ADMIN ACCESS
                .requestMatchers("/admin/**").hasRole("ADMIN")
             // In your SecurityConfig.java
                .requestMatchers("/admin/prescriptions/**").hasRole("ADMIN")
                
                // Everything else requires a valid JWT
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
            "https://nexhealth-frontend.onrender.com" // Replace with your actual Render frontend URL
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