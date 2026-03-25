package com.clinic.management.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.clinic.management.entity.Admin;
import com.clinic.management.repository.AdminRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class DataInitializer {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        if (adminRepository.findByEmail("swalo@gmail.com").isEmpty()) {

            Admin admin = new Admin();
            admin.setName("System Admin");
            admin.setEmail("swalo@gmail.com");
            admin.setPassword(passwordEncoder.encode("swalo123"));

            adminRepository.save(admin);

            System.out.println("✅ Default Admin Created: swalo@gmail.com / swalo123");
        }
    }
}

