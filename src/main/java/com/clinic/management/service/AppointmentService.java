package com.clinic.management.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.AppointmentStatus;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.AppointmentRepository;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /* ======================================================
        🔹 FETCH METHODS
       ====================================================== */

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getUpcomingAppointmentsByPatient(Long patientId) {
        LocalDate today = LocalDate.now();
        List<Appointment> upcoming = appointmentRepository.findByPatientIdAndDateGreaterThanEqual(patientId, today);
        
        return upcoming.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING || 
                             a.getStatus() == AppointmentStatus.CONFIRMED || 
                             a.getStatus() == AppointmentStatus.BOOKED)
                .collect(Collectors.toList());
    }

    public List<Appointment> getCompletedAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdAndStatus(patientId, AppointmentStatus.COMPLETED);
    }

    /* ======================================================
        🔹 STATE CHANGE METHODS (Crucial Fixes Here)
       ====================================================== */

    @Transactional
    public Appointment cancelAppointment(Long appointmentId) {
        // 1. Force the update query in the DB
        appointmentRepository.updateStatus(appointmentId, AppointmentStatus.CANCELLED);
        
        // 2. Clear the persistence context so we get the FRESH data from the DB
        // (Optional but good practice)
        
        // 3. Fetch the updated record to return to the controller
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Transactional
    public Appointment completeAppointment(Long appointmentId) {
        // 1. Fetch from DB
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));

        // 2. Set the status to COMPLETED
        appointment.setStatus(AppointmentStatus.COMPLETED);

        // 3. Force an immediate SQL Update
        return appointmentRepository.saveAndFlush(appointment);
    }

    /* =========================
        🔹 CREATION & HELPERS
       ========================= */

    public Appointment createAppointment(Doctor doctor, Patient patient, LocalDate date, LocalTime time) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(date);
        appointment.setTime(time);
        
        // Setting initial status here instead of relying on Entity defaults
        appointment.setStatus(AppointmentStatus.PENDING); 
        
        return appointmentRepository.save(appointment);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        return appointmentRepository.findByDoctor(doctor);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }
}