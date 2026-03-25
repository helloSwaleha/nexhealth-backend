package com.clinic.management.entity;

import java.security.Principal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    /* =========================
       PRIMARY KEY
       ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       FOREIGN KEYS (IDs)
       ========================= */
    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private Long appointmentId;

    /* =========================
       PRESCRIPTION DETAILS
       ========================= */
    @Column(nullable = false, length = 2000)
    private String medication;

    @Column(nullable = false)
    private String dosage;

    @Column(length = 3000)
    private String notes;

    /* =========================
       TIMESTAMP
       ========================= */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /* =========================
       CONSTRUCTORS
       ========================= */
    public Prescription() {
        this.createdAt = LocalDateTime.now();
    }

    public Prescription(
            Long patientId,
            Long doctorId,
            Long appointmentId,
            String medication,
            String dosage,
            String notes
    ) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    /* =========================
       GETTERS & SETTERS
       ========================= */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public Principal getDoctor() {
		// TODO Auto-generated method stub
		return null;
	}
	@Lob // Use this for large binary objects
	@Column(name = "pdf_data", columnDefinition = "LONGBLOB") // Adjust for your DB type
	private byte[] pdfData;

	public Object getMedicineName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Principal getPatient() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getPdfData() {
		// TODO Auto-generated method stub
		return null;
	}
}

