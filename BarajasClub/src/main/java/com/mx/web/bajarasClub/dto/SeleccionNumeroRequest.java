package com.mx.web.bajarasClub.dto;

public class SeleccionNumeroRequest {
	  private Integer rifaId;
	  private Integer numeroId;     // opcional: si ya tienes ID real
	  private String numero;        // opcional: si envías el label (e.g. "01")
	  private Boolean seleccionado; // true/false
	public Integer getRifaId() {
		return rifaId;
	}
	public void setRifaId(Integer rifaId) {
		this.rifaId = rifaId;
	}
	public Integer getNumeroId() {
		return numeroId;
	}
	public void setNumeroId(Integer numeroId) {
		this.numeroId = numeroId;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Boolean getSeleccionado() {
		return seleccionado;
	}
	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
}
