package com.clinic.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.clinic.management.dto.AddDoctorRequest;
import com.clinic.management.entity.Clinic;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Status;
import com.clinic.management.repository.ClinicRepository;
import com.clinic.management.repository.DoctorRepository;

import java.util.Optional;

@RestController
@RequestMapping("/admin/doctors")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminAddDoctorController {

    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAddDoctorController(
            DoctorRepository doctorRepository,
            ClinicRepository clinicRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.doctorRepository = doctorRepository;
        this.clinicRepository = clinicRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDoctor(@RequestBody AddDoctorRequest request) {

        // 1. Validate Basic Input
        if (request.getClinicId() == null) {
            return ResponseEntity.badRequest().body("Error: Clinic ID is missing.");
        }

        // 2. Lookup Clinic (This defines clinicOpt so it can be resolved below)
        Optional<Clinic> clinicOpt = clinicRepository.findById(request.getClinicId());
        
        if (!clinicOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Error: Clinic ID " + request.getClinicId() + " not found.");
        }

        // 3. Check for Duplicate Email
        if (doctorRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Doctor with email " + request.getEmail() + " already exists.");
        }

        try {
            Doctor doctor = new Doctor();
            
            // Standard Fields
            doctor.setName(request.getName());
            doctor.setEmail(request.getEmail());
            doctor.setSpecialization(request.getSpecialization());
            
            // Qualification & Fee (Fixing the column mismatch issue)
            doctor.setQualification(request.getQualification()); 
            doctor.setExperience(request.getExperience());
            doctor.setFee(request.getFee()); 
            
            // Setting Relationship (clinicOpt is now resolved from step 2)
            doctor.setClinic(clinicOpt.get());

            // Optional Fields
            if (request.getPhone() != null) {
                doctor.setPhone(request.getPhone());
            }
            
            if (request.getPassword() != null) {
                doctor.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            // Account Defaults
            doctor.setEnabled(true);
            doctor.setStatus(Status.ACTIVE); 

            doctorRepository.save(doctor);
            return ResponseEntity.ok("Doctor added successfully.");

        } catch (Exception e) {
            // Logs the full error if the database columns are still mismatched
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
    }
}