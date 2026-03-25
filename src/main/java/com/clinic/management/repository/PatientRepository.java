package com.clinic.management.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinic.management.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email
    );
    Optional<Patient> findByEmail(String email);
	boolean existsByEmail(String email);
	long countByCreatedAtAfter(LocalDateTime monthStart);
	
	
    
}

