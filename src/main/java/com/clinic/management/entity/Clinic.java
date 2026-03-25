package com.clinic.management.entity;

//Add this import
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clinics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Clinic extends BaseEntity {

 @Column(nullable = false, unique = true)
 private String name;

 @Column(nullable = false)
 private String email;

 private String phone;
 private String address;
 private String city;
 private String state;

 @Column(name = "image_url", columnDefinition = "LONGTEXT")
 @JsonProperty("imageUrl") // Forces the JSON key to be "imageUrl"
 private String imageUrl;

 @Enumerated(EnumType.STRING)
 private ClinicStatus status;
}