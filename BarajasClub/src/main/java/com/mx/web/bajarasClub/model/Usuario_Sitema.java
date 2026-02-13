package com.mx.web.bajarasClub.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "users ")
@Entity
public class Usuario_Sitema {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	private String username;
	
	private String password;
	
	private Boolean enabled;
	
	private String rol_sistema;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getRol_sistema() {
		return rol_sistema;
	}

	public void setRol_sistema(String rol_sistema) {
		this.rol_sistema = rol_sistema;
	}
	
	
	
	
	

}
