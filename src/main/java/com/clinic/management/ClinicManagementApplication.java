package com.clinic.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.clinic.management.repository")
@ComponentScan(basePackages = {"com.clinic.management.config", "com.clinic.management.controller",
		"com.clinic.management.dto","com.clinic.management.entity","com.clinic.management.exception", 
		"com.clinic.management.service", "com.clinic.management.repository"})
@EntityScan(basePackages = {"com.clinic.management.entity"})
public class ClinicManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClinicManagementApplication.class, args);
    }
}

