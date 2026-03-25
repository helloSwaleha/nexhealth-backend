package com.clinic.management.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.AppointmentStatus;
import com.clinic.management.entity.Prescription;
import com.clinic.management.repository.AppointmentRepository;
import com.clinic.management.repository.PrescriptionRepository;
import com.clinic.management.service.PrescriptionService;

@RestController
@RequestMapping("/api/doctor/prescriptions")
@CrossOrigin(origins = "http://localhost:3000")
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    @Autowired
    private PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionRepository prescriptionRepository, 
                                  AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /* ======================================================
       1. SAVE PRESCRIPTION (POST)
       ====================================================== */
    @PostMapping
    public ResponseEntity<?> createPrescription(@RequestBody Prescription prescription, Principal principal) {
        return appointmentRepository.findById(prescription.getAppointmentId())
            .map(appt -> {
                // 1. Security Check
                if (!appt.getDoctor().getEmail().equalsIgnoreCase(principal.getName())) {
                    return ResponseEntity.status(403).body("Access Denied");
                }

                // 2. Set IDs and Save Prescription
                prescription.setDoctorId(appt.getDoctor().getId());
                prescription.setPatientId(appt.getPatient().getId());
                Prescription saved = prescriptionRepository.save(prescription);

                // 3. ✅ UPDATE STATUS (Using the Enum constant)
                appt.setStatus(AppointmentStatus.COMPLETED); 
                
                // 4. PERSIST TO DATABASE
                appointmentRepository.save(appt);

                return ResponseEntity.ok(saved);
            })
            .orElse(ResponseEntity.status(404).body("Appointment not found"));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByPatient(@PathVariable Long patientId) {
        List<Prescription> history = prescriptionService.getHistoryByPatient(patientId);
        return ResponseEntity.ok(history);
    }
    
   

    /* ======================================================
       2. GET PRESCRIPTION BY APPOINTMENT ID (GET)
       URL: GET http://localhost:8080/doctor/prescriptions/appointment/{id}
       ====================================================== */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionByAppointment(@PathVariable Long appointmentId, Principal principal) {
        var prescriptionOpt = prescriptionRepository.findByAppointmentId(appointmentId).stream().findFirst();
        if (prescriptionOpt.isEmpty()) return ResponseEntity.status(404).body("No prescription found");

        var apptOpt = appointmentRepository.findById(appointmentId);
        if (apptOpt.isEmpty()) return ResponseEntity.status(404).body("Appointment not found");

        Appointment appt = apptOpt.get();
        String currentUserEmail = principal.getName();

        // ✅ ALLOW BOTH DOCTOR AND PATIENT
        boolean isDoctor = appt.getDoctor().getEmail().equalsIgnoreCase(currentUserEmail);
        boolean isPatient = appt.getPatient().getEmail().equalsIgnoreCase(currentUserEmail);

        if (!isDoctor && !isPatient) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        return ResponseEntity.ok(prescriptionOpt.get());
    }

	public PrescriptionService getPrescriptionService() {
		return prescriptionService;
	}

	public void setPrescriptionService(PrescriptionService prescriptionService) {
		this.prescriptionService = prescriptionService;
	}
}