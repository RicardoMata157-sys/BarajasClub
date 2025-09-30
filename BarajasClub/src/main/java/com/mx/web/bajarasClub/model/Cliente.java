package com.mx.web.bajarasClub.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "cliente_rifa")
public class Cliente {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCliente;
	
	private String nombre;
	
	private String apellido_patrno;
	
	
	private String apellido_materno;
	
	
	private String municipio;
	
	
	private String estado;
	
	private String codigo_postal;;
	
	private String email;
	
	private String telefono;
	
	public Cliente() {
		
	}
	 
	
	
	public Cliente(String nombre, String apellido_patrno, String apellido_materno, String municipio, String estado,
			String codigo_postal, String email, String telefono) {
		super();
		this.nombre = nombre;
		this.apellido_patrno = apellido_patrno;
		this.apellido_materno = apellido_materno;
		this.municipio = municipio;
		this.estado = estado;
		this.codigo_postal = codigo_postal;
		this.email = email;
		this.telefono = telefono;
	}


	// Cliente 1..N Boleto
	  @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
	  private List<Boleto> boletos = new ArrayList<>();





	public Integer getIdCliente() {
		return idCliente;
	}


	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getApellido_patrno() {
		return apellido_patrno;
	}


	public void setApellido_patrno(String apellido_patrno) {
		this.apellido_patrno = apellido_patrno;
	}


	public String getApellido_materno() {
		return apellido_materno;
	}


	public void setApellido_materno(String apellido_materno) {
		this.apellido_materno = apellido_materno;
	}


	public String getMunicipio() {
		return municipio;
	}


	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}


	public String getCodigo_postal() {
		return codigo_postal;
	}


	public void setCodigo_postal(String codigo_postal) {
		this.codigo_postal = codigo_postal;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getTelefono() {
		return telefono;
	}


	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	public List<Boleto> getBoletos() {
		return boletos;
	}


	public void setBoletos(List<Boleto> boletos) {
		this.boletos = boletos;
	}
	
	

}
