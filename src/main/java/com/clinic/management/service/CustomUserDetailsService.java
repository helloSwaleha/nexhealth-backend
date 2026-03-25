package com.clinic.management.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clinic.management.entity.Admin;
import com.clinic.management.entity.Doctor;
import com.clinic.management.repository.AdminRepository;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.repository.PatientRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    @Autowired private PatientRepository patientRepository;

    public CustomUserDetailsService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Attempting to load user for Security: " + username);

        /* =========================
           CHECK ADMIN
           ========================= */
        Optional<Admin> adminOpt = adminRepository.findByEmail(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPassword().toString()) // Added .toString() to fix type error
                    .authorities("ROLE_ADMIN")
                    .disabled(false) // Adjusted based on your entity capability
                    .build();
        }

        /* =========================
           CHECK DOCTOR
           ========================= */
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(username);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            return User.builder()
                    .username(doctor.getEmail())
                    .password(doctor.getPassword().toString()) // Added .toString() to fix type error
                    .authorities("ROLE_DOCTOR")
                    .disabled(false) 
                    .build();
        }
     // 3. Try to find in Patient table
        var patient = patientRepository.findByEmail(username);
        if (patient.isPresent()) {
            return (UserDetails) patient.get();
        }

        /* =========================
           USER NOT FOUND
           ========================= */
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}