package com.clinic.management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends BaseEntity {

    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	private String email;
    private String password;
    private boolean enabled;
	public Admin orElseThrow(Object object) {
		// TODO Auto-generated method stub
		return null;
	}
	public Admin orElse(Object object) {
		// TODO Auto-generated method stub
		return null;
	}
	public void setName(String string) {
		// TODO Auto-generated method stub
		
	}

    // getters & setters
}
