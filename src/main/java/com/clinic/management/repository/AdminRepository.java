
package com.clinic.management.repository;
import java.util.List;
import java.util.Optional;
import com.clinic.management.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clinic.management.entity.Doctor;
@Repository
// Specify <Admin, Long> to tell Spring this repo is for the Admin entity
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    Optional<Admin> findByEmail(String email);
    static List<Doctor> findByClinicId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
}


