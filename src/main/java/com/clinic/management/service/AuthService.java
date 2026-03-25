package com.clinic.management.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinic.management.config.JwtUtil;
import com.clinic.management.dto.LoginRequest;
import com.clinic.management.dto.LoginResponse;
import com.clinic.management.entity.Admin;
import com.clinic.management.entity.Doctor;
import com.clinic.management.repository.AdminRepository;
import com.clinic.management.repository.DoctorRepository;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            AdminRepository adminRepository, 
            DoctorRepository doctorRepository,
            PasswordEncoder passwordEncoder, 
            JwtUtil jwtUtil
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        System.out.println("🔐 Login attempt for: " + request.getEmail());

        if (request.getEmail() == null || request.getPassword() == null) {
            throw new InvalidCredentialsException("Email and password are required");
        }

        /* =========================
           CHECK ADMIN TABLE
           ========================= */
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            // Added .toString() to the second argument to ensure it is a String
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword().toString())) {
                System.out.println("✅ Admin authenticated: " + admin.getEmail());
                String token = jwtUtil.generateToken(admin.getEmail(), "ADMIN");
                return new LoginResponse(token, "ADMIN", admin.getId());
            }
        }

        /* =========================
           CHECK DOCTOR TABLE
           ========================= */
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(request.getEmail());
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            // Added .toString() to the second argument to ensure it is a String
            if (passwordEncoder.matches(request.getPassword(), doctor.getPassword().toString())) {
                System.out.println("✅ Doctor authenticated: " + doctor.getEmail());
                String token = jwtUtil.generateToken(doctor.getEmail(), "DOCTOR");
                return new LoginResponse(token, "DOCTOR", doctor.getId());
            }
        }

        System.out.println("❌ Login failed for: " + request.getEmail());
        throw new InvalidCredentialsException("Invalid email or password");
    }

    public static class InvalidCredentialsException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }
}