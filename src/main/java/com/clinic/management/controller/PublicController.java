package com.clinic.management.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.management.entity.Clinic;
import com.clinic.management.entity.ClinicStatus; // Changed from Status to ClinicStatus
import com.clinic.management.repository.ClinicRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicController {

    private final ClinicRepository clinicRepository;

    // ✅ Manual constructor
    public PublicController(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    /* =========================
       GET ALL ACTIVE CLINICS
       ========================= */
    @GetMapping("/clinic")
    public List<Clinic> getActiveClinics() {
        // Use ClinicStatus.ACTIVE to match the Repository signature
        return clinicRepository.findByStatus(ClinicStatus.ACTIVE);
    }
}