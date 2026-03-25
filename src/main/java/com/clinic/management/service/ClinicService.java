package com.clinic.management.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.clinic.management.entity.Clinic;
import com.clinic.management.entity.ClinicStatus; // Correct Enum Import
import com.clinic.management.repository.ClinicRepository;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    /* ================= CONSTRUCTOR ================= */
    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    /* ================= ADD CLINIC ================= */
    public Clinic addClinic(Clinic clinic) {
        // Use ClinicStatus.ACTIVE instead of Status.ACTIVE
        clinic.setStatus(ClinicStatus.ACTIVE); 
        return clinicRepository.save(clinic);
    }

    /* ================= GET ALL CLINICS ================= */
    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    /* ================= GET ACTIVE CLINICS ================= */
    public List<Clinic> getActiveClinics() {
        return clinicRepository.findByStatus(ClinicStatus.ACTIVE);
    }

    /* ================= GET CLINIC BY ID ================= */
    public Clinic getClinicById(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Clinic not found with id: " + id)
                );
    }

    /* ================= UPDATE CLINIC ================= */
    public Clinic updateClinic(Long id, Clinic updatedClinic) {
        Clinic existingClinic = getClinicById(id);

        existingClinic.setName(updatedClinic.getName());
        existingClinic.setEmail(updatedClinic.getEmail());
        existingClinic.setPhone(updatedClinic.getPhone());
        existingClinic.setAddress(updatedClinic.getAddress());
        existingClinic.setCity(updatedClinic.getCity());
        existingClinic.setState(updatedClinic.getState());
        existingClinic.setImageUrl(updatedClinic.getImageUrl());
        
        // Ensure updatedClinic.getStatus() returns ClinicStatus
        existingClinic.setStatus(updatedClinic.getStatus());

        return clinicRepository.save(existingClinic);
    }

    /* ================= ENABLE / DISABLE ================= */
    public Clinic changeClinicStatus(Long id, ClinicStatus status) {
        Clinic clinic = getClinicById(id);
        clinic.setStatus(status);
        return clinicRepository.save(clinic);
    }

    /* ================= DELETE ================= */
    public void deleteClinic(Long id) {
        if (!clinicRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Clinic not found");
        }
        clinicRepository.deleteById(id);
    }
}