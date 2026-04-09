package com.clinic.management.service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.clinic.management.entity.Prescription;
import com.clinic.management.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    // 1. Declare the repository variable
    private final PrescriptionRepository prescriptionRepository;

    // 2. Inject it via constructor
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<Prescription> getPrescriptionsByPatientEmail(String email) {
        List<Prescription> list = prescriptionRepository.findByPatientEmail(email);
        // If list is null for some reason, return an empty list instead of null
        return (list != null) ? list : new ArrayList<>();
    }
    
    public void exportToPDF(Prescription prescription, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        
        // Add Fonts/Styles
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        
        // Title
        Paragraph title = new Paragraph("MEDICAL PRESCRIPTION", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        
        document.add(new Paragraph(" ")); // Spacer

        // Content
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
		// TODO Auto-generated method stub
		return prescriptionRepository.findById(id).orElse(null);
	}

	public List<Prescription> getPrescriptionsByDoctor(Long doctorId) {
    return prescriptionRepository.findByDoctorId(doctorId);
}
	public List<Prescription> getPrescriptionsByDoctorEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Prescription> getHistoryByDoctorAndPatient(String doctorEmail, Long patientId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Prescription> getHistoryByPatient(Long patientId) {
	    // ✅ FIX: Use the repository to find data
	    List<Prescription> history = prescriptionRepository.findByPatientId(patientId);
	    
	    // Always return an empty list [] instead of null to prevent frontend errors
	    return (history != null) ? history : new ArrayList<>();
	}
}
