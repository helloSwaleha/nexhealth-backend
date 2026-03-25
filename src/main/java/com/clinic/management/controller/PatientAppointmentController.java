package com.clinic.management.controller;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.AppointmentStatus;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.AppointmentRepository;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/patient-appointments")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientAppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, Object> payload) {
        try {
            Long doctorId = Long.parseLong(payload.get("doctorId").toString());
            Long patientId = Long.parseLong(payload.get("patientId").toString());

            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            
            // ✅ THIS IS THE MISSING LINK
            // Fetch the clinic from the doctor and set it to the appointment
            if (doctor.getClinic() != null) {
                appointment.setClinic(doctor.getClinic());
            }

            appointment.setDate(LocalDate.parse(payload.get("appointmentDate").toString()));
            appointment.setTime(LocalTime.parse(payload.get("appointmentTime").toString()));
            appointment.setStatus(AppointmentStatus.PENDING);

            Appointment saved = appointmentRepository.save(appointment);
            return ResponseEntity.ok(saved);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}