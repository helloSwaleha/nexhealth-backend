package com.clinic.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String role;
    private Long userId;
    private Long doctorId;
    private Long clinicId;
    private Long patientId;

    public LoginResponse(String token) {
        this.token = token;
    }
    public LoginResponse(String token, String role, Long userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
    }
 // If you don't use Lombok, write this manually:
    public LoginResponse(Long patientId, String token) {
        this.patientId = patientId;
        this.token = token;
    }
    /* ===== Getters & Setters ===== */

    public LoginResponse(String token2, String string, Object id) {
		// TODO Auto-generated constructor stub
	}

	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }
}

