package com.clinic.management.controller;

import com.clinic.management.dto.LoginRequest;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.config.JwtUtil; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientAuthController {

    private final AuthenticationManager authenticationManager;
    private final PatientRepository patientRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public PatientAuthController(AuthenticationManager authenticationManager, 
                                 PatientRepository patientRepository, 
                                 JwtUtil jwtUtil,
                                 PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.patientRepository = patientRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder; 
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerPatient(@RequestBody Patient patient) {
        if (patientRepository.existsByEmail(patient.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        // Encode password before saving!
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patientRepository.save(patient);
        return ResponseEntity.ok("User registered successfully!");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. Authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Get Details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // 3. Generate Token (This now calls the fixed method in JwtUtil)
        String jwt = jwtUtil.generateToken(userDetails); 

        // 4. Find patient for ID
        Patient patient = patientRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Patient not found."));

        // 5. Response
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("patientId", patient.getId());
        response.put("email", patient.getEmail());

        return ResponseEntity.ok(response);
    }
    
}