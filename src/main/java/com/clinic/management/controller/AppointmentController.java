package com.clinic.management.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.Doctor;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;

    public AppointmentController(AppointmentService appointmentService, DoctorRepository doctorRepository) {
        this.appointmentService = appointmentService;
        this.doctorRepository = doctorRepository;
    }

    /* ======================================================
        🔹 HELPER METHOD (Fixed transformToMap)
       ====================================================== */

    private List<Map<String, Object>> transformToMap(List<Appointment> appointments) {
        return appointments.stream().map(appt -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", appt.getId());
            map.put("doctorName", appt.getDoctor() != null ? appt.getDoctor().getName() : "General Specialist");
            map.put("clinicName", appt.getClinic() != null ? appt.getClinic().getName() : "Main Clinic");
            map.put("patientName", appt.getPatient() != null ? appt.getPatient().getName() : "Unknown Patient");
            map.put("date", appt.getDate().toString());
            map.put("time", appt.getTime().toString());
            map.put("status", appt.getStatus());
            return map;
        }).collect(Collectors.toList());
    }

    /* ======================================================
        🔹 DOCTOR APIs
       ====================================================== */

    @GetMapping("/doctor")
    public ResponseEntity<?> getDoctorAppointments(Principal principal) {
        String email = principal.getName();
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);
        
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Doctor not found");
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorOpt.get());
        return ResponseEntity.ok(transformToMap(appointments));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.completeAppointment(id);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/doctor-cancel")
    public ResponseEntity<?> cancelAppointmentByDoctor(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    
    /* ======================================================
        🔹 PATIENT APIs
       ====================================================== */

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(transformToMap(appointments));
    }

    @GetMapping("/patient/{patientId}/completed")
    public ResponseEntity<?> getCompletedAppointments(@PathVariable Long patientId) {
        List<Appointment> completed = appointmentService.getCompletedAppointmentsByPatient(patientId);
        return ResponseEntity.ok(transformToMap(completed));
    }

    @PutMapping("/patient/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        try {
            // This MUST be the object returned AFTER the service update
            Appointment appointment = appointmentService.cancelAppointment(id);
            
            System.out.println("Returning status to frontend: " + appointment.getStatus());

            return ResponseEntity.ok(Map.of(
                "message", "Cancelled",
                "status", appointment.getStatus().toString() // Convert to string explicitly
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /* ======================================================
        🔹 PATIENT DASHBOARD - UPCOMING
       ====================================================== */

    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<?> getUpcomingAppointments(@PathVariable Long patientId) {
        try {
            List<Appointment> upcoming = appointmentService.getUpcomingAppointmentsByPatient(patientId);
            return ResponseEntity.ok(transformToMap(upcoming));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching upcoming appointments");
        }
    }

    /* =========================
        🔹 ADMIN APIs
       ========================= */

    @GetMapping("/admin/all")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @PutMapping("/admin/cancel/{id}")
    public ResponseEntity<?> adminCancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(appointmentService.cancelAppointment(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Appointment>> getByDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok(appointmentService.getAppointmentsByDate(LocalDate.parse(date)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}