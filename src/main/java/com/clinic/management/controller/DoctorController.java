package com.clinic.management.controller;

import java.security.Principal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinic.management.dto.StatusUpdateRequest;
import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.AppointmentRepository;
import com.clinic.management.repository.DoctorRepository;

@RestController
// CHANGED: Base path to /doctor to match your React frontend calls
@RequestMapping("/doctor") 
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    
    

    public DoctorController(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    /* ======================================================
    1. THE FETCH METHOD (Fixes your current error)
    URL: GET http://localhost:8080/doctor/profile
    ====================================================== */
 @GetMapping("/profile")
 public ResponseEntity<?> getDoctorProfile(Principal principal) {
     if (principal == null) return ResponseEntity.status(401).body("Unauthorized");

     return doctorRepository.findByEmail(principal.getName())
         .<ResponseEntity<?>>map(ResponseEntity::ok)
         .orElse(ResponseEntity.status(404).body("Doctor profile not found"));
 }
 
 
 @GetMapping("/{doctorId}/schedule")
 public ResponseEntity<List<Appointment>> getDoctorSchedule(@PathVariable Long doctorId) {
     // This will find all appointments for this doctor
     return ResponseEntity.ok(appointmentRepository.findByDoctorId(doctorId));
 }

 /* ======================================================
    2. THE UPDATE METHOD (For when you click Save)
    URL: PUT http://localhost:8080/doctor/profile
    ====================================================== */
 @PutMapping("/profile")
 public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates, Principal principal) {
     if (principal == null) return ResponseEntity.status(401).body("Unauthorized");

     return doctorRepository.findByEmail(principal.getName())
         .map(doctor -> {
             if (updates.containsKey("name")) doctor.setName((String) updates.get("name"));
             if (updates.containsKey("phone")) doctor.setPhone((String) updates.get("phone"));
             if (updates.containsKey("qualification")) doctor.setQualification((String) updates.get("qualification"));
             if (updates.containsKey("specialization")) doctor.setSpecialization((String) updates.get("specialization"));
             
             if (updates.containsKey("fee")) {
                 doctor.setFee(Double.parseDouble(updates.get("fee").toString()));
             }

             doctorRepository.save(doctor);
             return ResponseEntity.ok("Profile updated successfully");
         })
         .orElse(ResponseEntity.status(404).body("Doctor not found"));
 }

 
 
 @GetMapping("/{id}")
 public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
     return doctorRepository.findById(id)
             .map(ResponseEntity::ok)
             .orElse(ResponseEntity.notFound().build());
 }
    /* ======================================================
       SECURE: GET LOGGED-IN DOCTOR'S APPOINTMENTS
       URL: GET http://localhost:8080/doctor/appointments
       ====================================================== */
    @GetMapping("/appointments")
    public ResponseEntity<?> getMyAppointments(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body("Unauthorized");

        return doctorRepository.findByEmail(principal.getName())
            .<ResponseEntity<?>>map(doctor -> {
                List<Appointment> appointments = appointmentRepository.findByDoctorId(doctor.getId());
                return ResponseEntity.ok(appointments);
            })
            .orElse(ResponseEntity.status(404).body("Doctor not found in database"));
    }

    /* ======================================================
       SECURE: DOCTOR DASHBOARD STATS
       URL: GET http://localhost:8080/doctor/dashboard
       ====================================================== */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body("Unauthorized");

        return doctorRepository.findByEmail(principal.getName())
            .<ResponseEntity<?>>map(doctor -> {
                List<Appointment> appointments = appointmentRepository.findByDoctorId(doctor.getId());
                
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalAppointments", appointments.size());
                stats.put("pendingAppointments", appointments.stream()
                    .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus().name())).count());
                stats.put("completedAppointments", appointments.stream()
                    .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus().name())).count());
                stats.put("doctorName", doctor.getName());

                return ResponseEntity.ok(stats);
            })
            .orElse(ResponseEntity.status(404).body("Doctor profile not found"));
    }

    /* ======================================================
       SECURE: UPDATE APPOINTMENT STATUS
       URL: PUT http://localhost:8080/doctor/appointments/{id}/status
       ====================================================== */
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body("Unauthorized");

        return appointmentRepository.findById(id).map(appt -> {
            // Security check: ensure doctor owns this appointment
            if (!appt.getDoctor().getEmail().equalsIgnoreCase(principal.getName())) {
                return ResponseEntity.status(403).body("Access Denied: Not your appointment");
            }
            appt.setStatus(request.getStatus());
            appointmentRepository.save(appt);
            return ResponseEntity.ok("Status successfully updated to " + request.getStatus());
        }).orElse(ResponseEntity.status(404).body("Appointment ID not found"));
    }
    
    @GetMapping("/{doctorId}/patients")
    public ResponseEntity<List<Patient>> getDoctorPatients(@PathVariable Long doctorId) {
        // 1. Fetch appointments for this doctor
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);

        // 2. Extract unique patients from those appointments
        List<Patient> patients = appointments.stream()
                .map(Appointment::getPatient) // Extract the patient from each appointment
                .filter(Objects::nonNull)      // Ensure no nulls
                .distinct()                   // Remove duplicates (so a patient shows only once)
                .collect(Collectors.toList());

        return ResponseEntity.ok(patients);
    }

    /* ======================================================
       PUBLIC/UTILITY: GET DOCTORS BY CLINIC ID
       URL: GET http://localhost:8080/doctor/clinic/{clinicId}
       ====================================================== */
 // This matches: GET /api/doctors/clinic/{id}
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<Doctor>> getDoctorsByClinic(@PathVariable Long clinicId) {
        List<Doctor> doctors = doctorRepository.findByClinicId(clinicId);
        return ResponseEntity.ok(doctors);
    }
}