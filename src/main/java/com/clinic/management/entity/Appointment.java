package com.clinic.management.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointments")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonIgnoreProperties({"appointments", "password", "enabled"}) 
    private Doctor doctor;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"appointments", "password"})
    private Patient patient;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status; 

    // currently added this line
    public Doctor getDoctor() { return doctor; }
public Patient getPatient() { return patient; }
public void setStatus(AppointmentStatus status) { this.status = status; }

    
 // Inside Appointment.java
    public String getPatientName() {
        return this.patient != null ? this.patient.getName() : "Unknown";
    }

}
    // ✅ DELETE EVERYTHING BELOW THIS LINE
    // The "public static void setStatus" was the reason it was failing.
