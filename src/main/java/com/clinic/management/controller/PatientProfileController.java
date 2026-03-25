package com.clinic.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.PatientRepository;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientProfileController {

    private final PatientRepository patientRepository;

    public PatientProfileController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /* ======================================================
       🔹 GET PROFILE
       ====================================================== */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        return patientRepository.findById(id)
                .<ResponseEntity<?>>map(patient -> ResponseEntity.ok(patient)) 
                .orElse(ResponseEntity.status(404).body("Patient not found"));
    }

    /* ======================================================
       🔹 UPDATE PROFILE
       ====================================================== */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Patient updatedData) {
        return patientRepository.findById(id)
                .<ResponseEntity<?>>map(existingPatient -> {
                    // Update fields
                    existingPatient.setName(updatedData.getName());
                    existingPatient.setEmail(updatedData.getEmail());
                    existingPatient.setPhone(updatedData.getPhone());
                    existingPatient.setAge(updatedData.getAge());
                    existingPatient.setCity(updatedData.getCity());
                    
                    Patient savedPatient = patientRepository.saveAndFlush(existingPatient);
                    return ResponseEntity.ok(savedPatient);
                })
                .orElse(ResponseEntity.status(404).body("Patient record not found"));
    }
}