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
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // ✅ CRITICAL: Allow all OPTIONS requests for CORS pre-flight checks
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 1. PUBLIC ENDPOINTS
                .requestMatchers("/", "/error", "/auth/**").permitAll()
                .requestMatchers("/api/patient/login", "/api/patient/signup").permitAll()
                .requestMatchers("/api/clinics/**", "/doctor/clinic/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/doctor/{id}").permitAll()

                // 2. PRESCRIPTION RULES
                // View Access
                .requestMatchers(HttpMethod.GET, "/api/doctor/prescriptions/**", "/api/patient/prescriptions/**", "/api/doctor/my-patients-history")
                    .hasAnyAuthority("DOCTOR", "PATIENT", "ADMIN", "ROLE_DOCTOR", "ROLE_PATIENT", "ROLE_ADMIN")
                
                // Creation Access
                .requestMatchers(HttpMethod.POST, "/api/doctor/prescriptions/**")
                    .hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")

                // 3. APPOINTMENT RULES
                .requestMatchers("/appointments/patient/**").hasAnyAuthority("PATIENT", "ROLE_PATIENT", "ADMIN", "ROLE_ADMIN")
                .requestMatchers("/appointments/doctor/**", "/appointments/{id}/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")
                .requestMatchers("/appointments/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN", "ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_ADMIN")

                // 4. ROLE SPECIFIC DASHBOARDS
                .requestMatchers("/admin/**", "/api/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                .requestMatchers("/doctor/dashboard/**", "/doctor/appointments/**", "/doctor/profile/**", "/api/doctor/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")
                .requestMatchers("/api/patient/**", "/patient/**").hasAnyAuthority("PATIENT", "ROLE_PATIENT", "ADMIN", "ROLE_ADMIN")

                // 5. CATCH-ALL
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed Origins (Vercel + Local)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://nexhealth-frontend.vercel.app" 
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // ✅ Wildcard allowed headers to prevent pre-flight 403s on Admin Login
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        configuration.setAllowCredentials(true);
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
