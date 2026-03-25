package com.clinic.management.controller;

import com.clinic.management.entity.Prescription;
import com.clinic.management.repository.PrescriptionRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/admin/prescriptions")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminPrescriptionController {

    private final PrescriptionRepository prescriptionRepository;

    public AdminPrescriptionController(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Fetches all prescriptions for the Admin UI Table.
     */
    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionRepository.findAll());
    }

    /**
     * Retrieves the PDF. If the blob is null in DB, it generates one dynamically.
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPrescriptionPdf(@PathVariable Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found with ID: " + id));

        byte[] pdfContent = prescription.getPdfData();

        // If the database column is empty, generate a PDF from the text fields
        if (pdfContent == null || pdfContent.length == 0) {
            pdfContent = generateDynamicPdf(prescription);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=prescription_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    private byte[] generateDynamicPdf(Prescription p) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Styling
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Add Content
            document.add(new Paragraph("MEDICAL PRESCRIPTION", titleFont));
            document.add(new Paragraph(" ")); // Spacer
            
            document.add(new Paragraph("Record ID: " + p.getId(), contentFont));
            document.add(new Paragraph("Date: " + p.getCreatedAt(), contentFont));
            document.add(new Paragraph("-----------------------------------------------------------"));
            
            document.add(new Paragraph("Patient ID: ", labelFont));
            document.add(new Paragraph("PID-" + p.getPatientId(), contentFont));
            
            document.add(new Paragraph("Doctor ID: ", labelFont));
            document.add(new Paragraph("DOC-" + p.getDoctorId(), contentFont));
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Medication & Dosage:", labelFont));
            document.add(new Paragraph(p.getMedication() + " - " + p.getDosage(), contentFont));
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Additional Notes:", labelFont));
            document.add(new Paragraph(p.getNotes() != null ? p.getNotes() : "No additional notes.", contentFont));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating dynamic PDF", e);
        }
    }
}