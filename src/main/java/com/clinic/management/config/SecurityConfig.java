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
import org.springframework.http.HttpMethod;

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
        .cors(Customizer.withDefaults()) 
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            // 1. PUBLIC ENDPOINTS (No Token Needed)
            .requestMatchers("/", "/error", "/auth/**").permitAll()
            .requestMatchers("/api/patient/login", "/api/patient/signup").permitAll()
            .requestMatchers("/api/clinics/**", "/doctor/clinic/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/doctor/{id}").permitAll()

            // 2. PRESCRIPTION RULES (CRITICAL ORDERING)
            // Rule A: Allow Patients AND Doctors to VIEW prescriptions
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/doctor/prescriptions/**")
                .hasAnyAuthority("DOCTOR", "PATIENT", "ROLE_DOCTOR", "ROLE_PATIENT", "ADMIN")
            
            // Rule B: Allow ONLY Doctors to CREATE prescriptions
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/doctor/prescriptions/**")
                .hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN")

            // 3. APPOINTMENT RULES
            .requestMatchers("/appointments/patient/**").hasAnyAuthority("PATIENT", "ROLE_PATIENT", "ADMIN", "ROLE_ADMIN")
            .requestMatchers("/appointments/doctor/**", "/appointments/{id}/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")
            .requestMatchers("/appointments/**").hasAnyAuthority("PATIENT", "ROLE_PATIENT", "DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")

            // 4. ROLE SPECIFIC DASHBOARDS
            .requestMatchers("/admin/**", "/api/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
            .requestMatchers("/doctor/dashboard/**", "/doctor/appointments/**", "/doctor/profile/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR")
            .requestMatchers("/api/patient/**", "/patient/**").hasAnyAuthority("PATIENT", "ROLE_PATIENT")

            // 5. CATCH-ALL DOCTOR PATH (Keep this low in the list)
            .requestMatchers("/doctor/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")
            
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}    @Bean
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
