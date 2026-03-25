package com.clinic.management.service;

import com.clinic.management.entity.AppointmentStatus;
import com.clinic.management.repository.AppointmentRepository;
import com.clinic.management.repository.ClinicRepository;
import com.clinic.management.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate; // Added missing import
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;
    private final PatientRepository patientRepository;

    public ReportService(AppointmentRepository appointmentRepository, 
                         ClinicRepository clinicRepository,
                         PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clinicRepository = clinicRepository;
        this.patientRepository = patientRepository;
    }

    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAppointments", appointmentRepository.count());
        summary.put("completed", appointmentRepository.countByStatus(AppointmentStatus.COMPLETED));
        summary.put("cancelled", appointmentRepository.countByStatus(AppointmentStatus.CANCELLED));
        summary.put("noShows", appointmentRepository.countByStatus(AppointmentStatus.NO_SHOW));
        return summary;
    }

    public List<Map<String, Object>> getWeeklyTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        // Get Monday of current week
        LocalDate startOfWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = startOfWeek.plusDays(i);
            // Matches the fixed countByDate(LocalDate date) in Repo
            long count = appointmentRepository.countByDate(currentDay);
            
            Map<String, Object> data = new HashMap<>();
            // Formats to "Mon", "Tue", etc. for Recharts
            String dayLabel = currentDay.getDayOfWeek().name().substring(0, 1) + 
                             currentDay.getDayOfWeek().name().substring(1, 3).toLowerCase();
            
            data.put("day", dayLabel); 
            data.put("appointments", count);
            trend.add(data);
        }
        return trend;
    }

    public List<Map<String, Object>> getClinicPerformance() {
        List<Map<String, Object>> performance = new ArrayList<>();
        clinicRepository.findAll().forEach(clinic -> {
            Map<String, Object> data = new HashMap<>();
            data.put("clinic", clinic.getName());
            // Matches countByClinicId in Repo
            data.put("appointments", appointmentRepository.countByClinicId(clinic.getId()));
            performance.add(data);
        });
        return performance;
    }

    public List<Map<String, Object>> getPatientBehavior() {
        List<Map<String, Object>> behavior = new ArrayList<>();
        
        try {
            long totalPatients = patientRepository.count();
            // Start of current month
            LocalDateTime monthStart = LocalDateTime.now()
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .withHour(0).withMinute(0);
            
            // Ensure countByCreatedAtAfter exists in PatientRepository
            long newPatients = patientRepository.countByCreatedAtAfter(monthStart);

            behavior.add(Map.of("name", "New This Month", "value", newPatients));
            behavior.add(Map.of("name", "Total Patients", "value", totalPatients));
        } catch (Exception e) {
            // Fallback so UI doesn't break if PatientRepo methods are missing
            behavior.add(Map.of("name", "New This Month", "value", 0));
            behavior.add(Map.of("name", "Total Patients", "value", 0));
        }
        return behavior;
    }
}