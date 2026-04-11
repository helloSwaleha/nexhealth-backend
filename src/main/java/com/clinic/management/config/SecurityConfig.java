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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * ✅ THE FIX: Explicit CorsFilter Bean
     * This ensures CORS is handled at the highest priority before any security filters.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://nexhealth-frontend.vercel.app"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Use the corsFilter bean defined above
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Allow pre-flight OPTIONS requests for all paths
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 1. PUBLIC ENDPOINTS
                .requestMatchers("/", "/error", "/auth/**").permitAll()
                .requestMatchers("/api/patient/login", "/api/patient/signup").permitAll()
                .requestMatchers("/api/clinics/**", "/doctor/clinic/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/doctor/{id}").permitAll()

                // 2. PRESCRIPTION RULES
                .requestMatchers(HttpMethod.GET, "/api/doctor/prescriptions/**", "/api/patient/prescriptions/**", "/api/doctor/my-patients-history")
                    .hasAnyAuthority("DOCTOR", "PATIENT", "ADMIN", "ROLE_DOCTOR", "ROLE_PATIENT", "ROLE_ADMIN")
                
                .requestMatchers(HttpMethod.POST, "/api/doctor/prescriptions/**")
                    .hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")

                // 3. APPOINTMENT RULES
                .requestMatchers("/appointments/patient/**").hasAnyAuthority("PATIENT", "ROLE_PATIENT", "ADMIN", "ROLE_ADMIN")
                .requestMatchers("/appointments/doctor/**", "/appointments/{id}/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN", "ROLE_ADMIN")
                .requestMatchers("/appointments/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN", "ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_ADMIN")
                 .requestMatchers("/api/doctor-my-data/**").hasAnyAuthority("DOCTOR", "ROLE_DOCTOR", "ADMIN")
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/appointments/admin/**").hasAuthority("ADMIN")

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
