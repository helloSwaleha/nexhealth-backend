package com.clinic.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.clinic.management.service.ReportService;
import java.util.*;

@RestController
// Base path matches your frontend axios calls
@RequestMapping("/admin/reports") 
@CrossOrigin(origins = "http://localhost:3000")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // URL: GET http://localhost:8080/admin/reports/summary
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getReportSummary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    // URL: GET http://localhost:8080/admin/reports/appointments/weekly
    @GetMapping("/appointments/weekly")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyTrend() {
        return ResponseEntity.ok(reportService.getWeeklyTrend());
    }

    // URL: GET http://localhost:8080/admin/reports/clinics/performance
    // FIX: Ensure this exact path is called by frontend
    @GetMapping("/clinics/performance")
    public ResponseEntity<List<Map<String, Object>>> getClinicPerformance() {
        return ResponseEntity.ok(reportService.getClinicPerformance());
    }

    // URL: GET http://localhost:8080/admin/reports/patient-behavior
    @GetMapping("/patient-behavior")
    public ResponseEntity<List<Map<String, Object>>> getPatientBehavior() {
        return ResponseEntity.ok(reportService.getPatientBehavior());
    }
}