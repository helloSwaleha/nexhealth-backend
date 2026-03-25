package com.clinic.management.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinic.management.entity.Clinic;
import com.clinic.management.entity.ClinicStatus;
import com.clinic.management.entity.Doctor;
import com.clinic.management.repository.ClinicRepository;
import com.clinic.management.repository.DoctorRepository;

@RestController
@RequestMapping("/admin/clinics")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminClinicController {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    /* =========================
       1. GET ALL CLINICS
       URL: GET http://localhost:8080/admin/clinics
       ========================= */
    @GetMapping
    public ResponseEntity<List<Clinic>> getAllClinics() {
        List<Clinic> clinics = clinicRepository.findAll();
        return ResponseEntity.ok(clinics);
    }

    /* =========================
       2. GET CLINIC BY ID
       URL: GET http://localhost:8080/admin/clinics/{id}
       ========================= */
    @GetMapping("/{id}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable Long id) {
        return clinicRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Clinic not found with id: " + id));
    }

    /* =========================
       3. ADD NEW CLINIC
       URL: POST http://localhost:8080/admin/clinics
       ========================= */
    @PostMapping
    public ResponseEntity<?> addClinic(@RequestBody Clinic clinic) {
        // Automatically set status to ACTIVE when created by Admin
        clinic.setStatus(ClinicStatus.ACTIVE); 
        Clinic savedClinic = clinicRepository.save(clinic);
        return ResponseEntity.ok(savedClinic);
    }

    /* =========================
       4. UPDATE CLINIC STATUS
       URL: PUT http://localhost:8080/admin/clinics/{id}/status?status=INACTIVE
       ========================= */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateClinicStatus(
            @PathVariable Long id,
            @RequestParam ClinicStatus status
    ) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        clinic.setStatus(status);
        clinicRepository.save(clinic);
        return ResponseEntity.ok("Clinic status updated to " + status);
    }

    /* =========================
       5. DELETE CLINIC
       URL: DELETE http://localhost:8080/admin/clinics/{id}
       ========================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClinic(@PathVariable Long id) {
        return clinicRepository.findById(id)
                .map(clinic -> {
                    clinicRepository.delete(clinic);
                    return ResponseEntity.ok("Clinic deleted successfully");
                })
                .orElse(ResponseEntity.status(404).body("Clinic not found"));
    }

    /* =========================================
       6. GET ALL DOCTORS FOR A SPECIFIC CLINIC
       URL: GET http://localhost:8080/admin/clinics/{id}/doctors
       ========================================= */
    @GetMapping("/{id}/doctors")
    public ResponseEntity<List<Doctor>> getDoctorsByClinicId(@PathVariable Long id) {
        if (!clinicRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<Doctor> doctors = doctorRepository.findByClinicId(id);
        return ResponseEntity.ok(doctors);
    }
}