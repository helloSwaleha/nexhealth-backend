package com.clinic.management.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.clinic.management.entity.Clinic;
import com.clinic.management.entity.ClinicStatus; // Import the correct Enum

public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    // ✅ Match the parameter type to the field in Clinic.java
    List<Clinic> findByStatus(ClinicStatus status);

    // Using Optional is safer for findByName to avoid NullPointerExceptions
    Optional<Clinic> findByName(String name);
    
    @Query("SELECT c FROM Clinic c WHERE c.status = 'ACTIVE'")
    List<Clinic> findAllActiveClinics();
    
    List<Clinic> findTop5ByOrderByIdDesc();

	//Optional<Clinic> findByName(Object clinicName);
}
