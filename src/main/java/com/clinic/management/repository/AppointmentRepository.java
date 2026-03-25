package com.clinic.management.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.AppointmentStatus;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Patient;

import jakarta.transaction.Transactional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /* =========================
       BASIC RETRIEVAL
       ========================= */
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByPatient(Patient patient);
    
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") AppointmentStatus status);
    
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByPatientIdAndDateGreaterThanEqual(Long patientId, LocalDate date);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);
    List<Appointment> findByDate(LocalDate date);

    /* =========================
       FILTERED RETRIEVAL
       ========================= */
    List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

    /* =========================
       REPORTING & ANALYTICS (Used by AdminReports UI)
       ========================= */
    
    // 1. Summary Cards
    long countByStatus(AppointmentStatus status);

    // 2. Clinic Performance (Looking through Appointment -> Clinic connection)
    // Note: Use countByClinicId because your Appointment entity has a 'clinic' field
    long countByClinicId(Long clinicId);

    // 3. Weekly Trend (Count by LocalDate)
    long countByDate(LocalDate date);

    // 4. Patient Behavior
    @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a")
    long countDistinctPatients();
    
    /* =========================
       DOCTOR DASHBOARD & UTILS
       ========================= */
    List<Appointment> findTop5ByOrderByIdDesc();

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId")
    List<Patient> findDistinctPatientsByDoctorId(@Param("doctorId") Long doctorId);

    List<Appointment> findByDoctorIdOrderByDateAscTimeAsc(Long doctorId);

    Long countByDoctorIdAndDate(Long doctorId, LocalDate date);

    @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a WHERE a.doctor.id = :doctorId")
    Long countDistinctPatientsByDoctorId(@Param("doctorId") Long doctorId);

    long countByPatientId(Long id);
	Object countByDoctorClinicId(Long id);
	Long countByDoctorIdAndStatus(Long doctorId, AppointmentStatus pending);
	List<Appointment> findTop5ByDoctorIdOrderByDateDescTimeDesc(Long doctorId);

    // REMOVED: countByAppointmentDateBetween (This was causing your crash!)
}