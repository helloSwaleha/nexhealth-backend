package com.clinic.management.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import com.clinic.management.entity.Prescription;
import com.clinic.management.repository.PrescriptionRepository;
import com.clinic.management.entity.Doctor;
import com.clinic.management.repository.DoctorRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, DoctorRepository doctorRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Prescription> getPrescriptionsByPatientEmail(String email) {
        List<Prescription> list = prescriptionRepository.findByPatientEmail(email);
        return (list != null) ? list : Collections.emptyList();
    }
    
    public void exportToPDF(Prescription prescription, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        
        Paragraph title = new Paragraph("MEDICAL PRESCRIPTION", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        
        document.add(new Paragraph(" ")); // Spacer

        document.add(new Paragraph("Date: " + prescription.getCreatedAt()));
        document.add(new Paragraph("Doctor: " + prescription.getDoctor().getName()));
        document.add(new Paragraph("Patient: " + prescription.getPatient().getName()));
        document.add(new Paragraph("-----------------------------------------------------------"));
        
        document.add(new Paragraph("Medicine: " + prescription.getMedicineName()));
        document.add(new Paragraph("Dosage: " + prescription.getDosage()));
        document.add(new Paragraph("Instructions: " + prescription.getInstructions()));

        document.close();
    }

    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id).orElse(null);
    }

    public List<Prescription> getPrescriptionsByDoctor(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }

    public List<Prescription> getHistoryByDoctorAndPatient(String email, Long patientId) {
        // ✅ Fixed the Optional conversion here
        return doctorRepository.findByEmail(email)
                .map(doctor -> prescriptionRepository.findByDoctorIdAndPatientId(doctor.getId(), patientId))
                .orElse(Collections.emptyList());
    }

    public List<Prescription> getPrescriptionsByDoctorEmail(String email) {
        // ✅ Correctly unwrapping the Optional with a RuntimeException
        Doctor doctor = doctorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Doctor not found with email: " + email));
        
        return prescriptionRepository.findByDoctorId(doctor.getId());
    }
    
    public List<Prescription> getHistoryByPatient(Long patientId) {
        List<Prescription> history = prescriptionRepository.findByPatientId(patientId);
        return (history != null) ? history : Collections.emptyList();
    }
}
