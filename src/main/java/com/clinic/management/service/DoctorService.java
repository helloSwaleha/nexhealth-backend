package com.clinic.management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Status; // Use the Doctor-specific Status Enum
import com.clinic.management.repository.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(
            DoctorRepository doctorRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ================= ADD DOCTOR ================= */
    public Doctor addDoctor(Doctor doctor) {
        // Fix for PasswordEncoder (String vs CharSequence)
        if (doctor.getPassword() != null) {
            doctor.setPassword(passwordEncoder.encode(doctor.getPassword().toString()));
        }

        // Use the Doctor Status Enum
        doctor.setStatus(Status.ACTIVE);

        return doctorRepository.save(doctor);
    }

    /* ================= GET ALL DOCTORS ================= */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /* ================= GET ACTIVE DOCTORS ================= */
    public List<Doctor> getActiveDoctors() {
        // Use Status.ACTIVE instead of ClinicStatus
        return doctorRepository.findByStatus(Status.ACTIVE);
    }

    /* ================= GET DOCTOR BY ID ================= */
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found with id: " + id)
                );
    }

    /* ================= GET DOCTOR BY EMAIL ================= */
    public Optional<Doctor> getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    /* ================= UPDATE DOCTOR ================= */
    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        Doctor existingDoctor = getDoctorById(id);

        existingDoctor.setName(updatedDoctor.getName());
        existingDoctor.setEmail(updatedDoctor.getEmail());
        existingDoctor.setPhone(updatedDoctor.getPhone());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setExperience(updatedDoctor.getExperience());
        existingDoctor.setFee(updatedDoctor.getFee());
        existingDoctor.setClinic(updatedDoctor.getClinic());
        existingDoctor.setStatus(updatedDoctor.getStatus()); // This will now use Status enum

        return doctorRepository.save(existingDoctor);
    }

    /* ================= ENABLE / DISABLE DOCTOR ================= */
    public Doctor changeDoctorStatus(Long id, Status status) {
        Doctor doctor = getDoctorById(id);
        doctor.setStatus(status);
        return doctorRepository.save(doctor);
    }

    /* ================= DELETE ================= */
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found");
        }
        doctorRepository.deleteById(id);
    }
}