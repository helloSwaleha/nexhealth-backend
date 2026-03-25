package com.clinic.management.controller;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.AppointmentStatus;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.AppointmentRepository;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.repository.PrescriptionRepository;
import com.clinic.management.service.AppointmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorDashboardController {

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired 
    private PatientRepository patientRepository;
    

    // 1. Today's Appointment Count
    @GetMapping("/appointments/{doctorId}/today/count")
    public Long getTodayAppointmentCount(@PathVariable Long doctorId) {
        return appointmentRepository.countByDoctorIdAndDate(doctorId, LocalDate.now());
    }

    // 2. Total Unique Patients Count
    @GetMapping("/patients/{doctorId}/count")
    public Long getTotalPatientsCount(@PathVariable Long doctorId) {
        return appointmentRepository.countDistinctPatientsByDoctorId(doctorId);
    }

    // 3. Total Prescriptions Issued Count
    @GetMapping("/prescriptions/{doctorId}/count")
    public Long getPrescriptionCount(@PathVariable Long doctorId) {
        return prescriptionRepository.countByDoctorId(doctorId);
    }
    
    @GetMapping("appointments/{id}") 
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointment);
    }

 // Inside DoctorDashboardController.java

    @GetMapping("/appointments/{doctorId}/pending/count")
    public Long getPendingCount(@PathVariable Long doctorId) {
        // Replace 'AppointmentStatus' with the actual name of your Enum class
        return appointmentRepository.countByDoctorIdAndStatus(doctorId, AppointmentStatus.PENDING);
    }
    
    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        // This uses your existing patient repository
        // If you don't have patientRepository injected, add: @Autowired private PatientRepository patientRepository;
        List<Patient> patients = patientRepository.findAll();
        return ResponseEntity.ok(patients);
    }
    
    // 5. Recent 5 Appointments
    @GetMapping("/appointments/{doctorId}/recent")
    public List<Appointment> getRecentAppointments(@PathVariable Long doctorId) {
        // We will fetch the top 5 most recent appointments
        return appointmentRepository.findTop5ByDoctorIdOrderByDateDescTimeDesc(doctorId);
    }

	public PatientRepository getPatientRepository() {
		return patientRepository;
	}

	public void setPatientRepository(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}
}