package com.clinic.management.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.management.entity.Clinic;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Status;
import com.clinic.management.repository.ClinicRepository;
import com.clinic.management.repository.DoctorRepository;

@RestController
@RequestMapping("/admin/doctors")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminDoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* ================= ADD NEW DOCTOR ================= */
    @PostMapping
    public ResponseEntity<?> addDoctor(@RequestBody Doctor doctor) {

        // 🔐 Encode password before saving
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));

        // Default status
        doctor.setStatus(Status.ACTIVE);

        // Validate clinic
        Clinic clinic = clinicRepository.findById(doctor.getClinic().getId())
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        doctor.setClinic(clinic);

        Doctor savedDoctor = doctorRepository.save(doctor);
        return ResponseEntity.ok(savedDoctor);
    }

    /* ================= GET ALL DOCTORS ================= */
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }

    /* ================= GET DOCTOR BY ID ================= */
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return ResponseEntity.ok(doctor);
    }

    /* ================= ENABLE / DISABLE DOCTOR ================= */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateDoctorStatus(
            @PathVariable Long id,
            @RequestParam Status status
    ) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setStatus(status);
        doctorRepository.save(doctor);

        return ResponseEntity.ok("Doctor status updated to " + status);
    }
}
