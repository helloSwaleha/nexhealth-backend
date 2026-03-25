package com.clinic.management.controller;

import com.clinic.management.repository.*;
import com.clinic.management.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminDashboardController {

    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    // --- COUNTS ---
    @GetMapping("/clinics/count")
    public long getClinicCount() { return clinicRepository.count(); }

    @GetMapping("/doctors/count")
    public long getDoctorCount() { return doctorRepository.count(); }

    @GetMapping("/patients/count")
    public long getPatientCount() { return patientRepository.count(); }

    @GetMapping("/appointments/today/count")
    public long getTodayCount() { 
        return appointmentRepository.countByDate(LocalDate.now()); 
    }

    // --- RECENT LISTS ---
    @GetMapping("/doctors/recent")
    public List<Doctor> getRecentDoctors() {
        // Returns last 5 added doctors
        return doctorRepository.findTop5ByOrderByIdDesc();
    }

    @GetMapping("/clinics/recent")
    public List<Clinic> getRecentClinics() {
        return clinicRepository.findTop5ByOrderByIdDesc();
    }

    @GetMapping("/appointments/recent")
    public List<Map<String, Object>> getRecentAppointments() {
        List<Appointment> appts = appointmentRepository.findTop5ByOrderByIdDesc();
        List<Map<String, Object>> response = new ArrayList<>();
        
        for (Appointment a : appts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("patientName", a.getPatient().getName());
            map.put("doctorName", a.getDoctor().getName());
            map.put("date", a.getDate().toString());
            map.put("status", a.getStatus());
            response.add(map);
        }
        return response;
    }
}