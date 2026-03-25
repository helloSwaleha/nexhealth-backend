package com.clinic.management.controller;

import com.clinic.management.entity.Doctor;
import com.clinic.management.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // This sets the base to /api
@CrossOrigin(origins = "http://localhost:3000")
public class PublicClinicController {

    private final DoctorRepository doctorRepository;

    public PublicClinicController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /* Matches: GET http://localhost:8080/api/doctors/clinic/2 
    */
    @GetMapping("/doctors/clinic/{clinicId}")
    public ResponseEntity<List<Doctor>> getDoctorsByClinic(@PathVariable("clinicId") Long clinicId) {
        System.out.println("Public API: Fetching doctors for clinic ID: " + clinicId);
        List<Doctor> doctors = doctorRepository.findByClinicId(clinicId);
        return ResponseEntity.ok(doctors);
    }
}