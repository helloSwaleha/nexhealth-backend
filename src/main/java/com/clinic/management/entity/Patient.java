package com.clinic.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "patients")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseEntity implements UserDetails {

    private static final long serialVersionUID = 1L;
     
    
    private String name;
    private String email;
    private String password;
    private Integer age;
    private String phone;
    private String role = "ROLE_PATIENT";
    @Enumerated(EnumType.STRING) // This stores "MALE" instead of 0 in the DB
    @Column(name = "gender", length = 10)
    private Gender gender;
    
    // ✅ Lombok (@Getter/@Setter) will handle this automatically as a String
    private String city;

    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT"));
    }

    @Override
    public String getUsername() { return this.email; }

    @Override
    public String getPassword() { return this.password; }

    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return true; }

	public void setStatus(Status status) {
		// TODO Auto-generated method stub
		
	}

    // If you need a status field, add it as a private variable at the top 
    // instead of a manual setter with a TODO.
    // private Status status; 
}