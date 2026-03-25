package com.clinic.management.repository;

import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Status;
import com.clinic.management.entity.ClinicStatus; // Or your specific Status Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // ✅ Get the 5 most recently registered doctors
    List<Doctor> findTop5ByOrderByIdDesc();

    // ✅ Find a doctor by their login email
    Optional<Doctor> findByEmail(String email);

    
    // ✅ Find all doctors belonging to a specific clinic
    // (Keep only one version of this)
    List<Doctor> findByClinicId(Long clinicId);

    // ✅ Find doctors by their account status (Active/Inactive)
    // Ensure "ClinicStatus" matches the name of your Enum class
    List<Doctor> findByStatus(ClinicStatus status);

	List<Doctor> findByStatus(Status active);
}