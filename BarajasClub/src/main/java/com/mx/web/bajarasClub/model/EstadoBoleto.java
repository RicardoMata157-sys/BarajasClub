package com.mx.web.bajarasClub.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "estado_boleto")
@Entity
public class EstadoBoleto {
	
	
	 @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idEstadoBoleto;
	
	private String nombre;
	
	private Boolean isEstado;

	public Integer getIdEstadoBoleto() {
		return idEstadoBoleto;
	}

	public void setIdEstadoBoleto(Integer idEstadoBoleto) {
		this.idEstadoBoleto = idEstadoBoleto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Boolean getIsEstado() {
		return isEstado;
	}

	public void setIsEstado(Boolean isEstado) {
		this.isEstado = isEstado;
	}

	
	
	
	
	

}
