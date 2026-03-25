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

    // Lombok handles Getters/Setters
}