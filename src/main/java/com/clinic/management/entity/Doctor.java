package com.clinic.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Doctor extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Security: Never send password to frontend
    private String password;

    private String specialization;
    
    @Column(name = "qualification")
    private String qualification; // Added this so it shows up in your UI Profile

    private int experience;

    @Column(name = "consultation_fee", nullable = false)
    @JsonProperty("fee") // Matches your Frontend 'doctor.fee'
    private Double fee; 
    
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE; 
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_id", referencedColumnName = "id", nullable = false)
    private Clinic clinic;

	

	public void setDegree(String string) {
		// TODO Auto-generated method stub
		
	}

	// --- MANUALLY ADD THESE SETTERS (Used by the Controller) ---
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setSpecialization(String spec) { this.specialization = spec; }
    public void setQualification(String qual) { this.qualification = qual; }
    public void setExperience(int exp) { this.experience = exp; }
    public void setFee(double fee) { this.fee = fee; }
    public void setClinic(Clinic clinic) { this.clinic = clinic; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setStatus(Status status) { this.status = status; }

	// --- ADD GETTERS (Used by AuthService/Security) ---
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Lombok handles Getters/Setters
}
