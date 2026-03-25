package com.clinic.management.controller;

import com.clinic.management.entity.*;
import com.clinic.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173") 
public class AdminController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // 🔹 Keep only the Patient related endpoints here 
    // since Clinic endpoints are already in AdminClinicController

   

    @GetMapping("/patients/{id}/appointments")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable Long id) {
        // Ensure this method exists in your AppointmentRepository
        return ResponseEntity.ok(appointmentRepository.findByPatientId(id));
    }

    @GetMapping("/patients/{id}/stats")
    public ResponseEntity<List<Map<String, Object>>> getPatientStats(@PathVariable Long id) {
        long totalAppointments = appointmentRepository.countByPatientId(id);
        
        List<Map<String, Object>> stats = new ArrayList<>();
        stats.add(Map.of("label", "Total Visits", "value", totalAppointments));
        stats.add(Map.of("label", "Status", "value", "Active"));
        
        return ResponseEntity.ok(stats);
    }
}