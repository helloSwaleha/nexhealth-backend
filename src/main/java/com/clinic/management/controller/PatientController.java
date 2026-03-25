package com.clinic.management.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinic.management.entity.Patient;
import com.clinic.management.entity.Status;
import com.clinic.management.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/patients")
@RequiredArgsConstructor 
@CrossOrigin(origins = "http://localhost:3000") // Matches your React port
public class PatientController {

    private final PatientRepository patientRepository;

    /**
     * GET ALL OR SEARCH PATIENTS
     * Handles: GET /admin/patients?search=keyword
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getPatients(
            @RequestParam(required = false) String search
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(
                patientRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search)
            );
        }
        return ResponseEntity.ok(patientRepository.findAll());
    }

    /**
     * FETCH SINGLE PATIENT BY ID
     * Handles: GET /admin/patients/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ENABLE / DISABLE PATIENT
     * Handles: PUT /admin/patients/1/status?status=ACTIVE
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updatePatientStatus(
            @PathVariable Long id,
            @RequestParam Status status
    ) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setStatus(status);
        patientRepository.save(patient);

        return ResponseEntity.ok("Patient status updated to " + status);
    }
}