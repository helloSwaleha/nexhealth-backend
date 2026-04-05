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
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;
    private Integer age;
    private String phone;
    private String city;
    private String role = "ROLE_PATIENT";

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    // ✅ ADDED: This field was missing, causing the "cannot find symbol" error
    @Enumerated(EnumType.STRING)
    private Status status;

    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Use the role field directly to stay flexible
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
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
}
