package com.mx.web.bajarasClub.dto;

public class CompraRequest {

    private String numeroTicket;
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private String email;
    private String telefono;
    private String municipio;
    private String estado;
    private String codigoPostal;
    
    private Long tipoPagoId; // <--- ESTA propiedad DEBE existir + getters/setters

    
    
    
    
    
    
	public Long getTipoPagoId() {
		return tipoPagoId;
	}
	public void setTipoPagoId(Long tipoPagoId) {
		this.tipoPagoId = tipoPagoId;
	}
	public String getNumeroTicket() {
		return numeroTicket;
	}
	public void setNumeroTicket(String numeroTicket) {
		this.numeroTicket = numeroTicket;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidoP() {
		return apellidoP;
	}
	public void setApellidoP(String apellidoP) {
		this.apellidoP = apellidoP;
	}
	public String getApellidoM() {
		return apellidoM;
	}
	public void setApellidoM(String apellidoM) {
		this.apellidoM = apellidoM;
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
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

    // Getters y setters
    
    
    
}
