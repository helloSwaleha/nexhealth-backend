package com.clinic.management.repository;

import com.clinic.management.entity.Prescription;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // These work perfectly with your Long patientId / doctorId fields
    List<Prescription> findByPatientId(Long patientId);

    List<Prescription> findByDoctorId(Long doctorId);

    List<Prescription> findByAppointmentId(Long appointmentId);

    List<Prescription> findByPatientId(Long patientId);

    // FIXED: Removed the @EntityGraph findAll() that was causing the error.
    // JpaRepository already provides a standard findAll().

    Long countByDoctorId(Long doctorId);

    @Nullable
    List<Prescription> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    
    // Cleanup: If you don't have Email logic yet, you can remove this or keep it as is.
    default List<Prescription> findByPatientEmail(String email) {
        return null;
    }
}
