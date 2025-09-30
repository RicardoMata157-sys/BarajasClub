// RaffleForm.java
package com.mx.web.bajarasClub.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.mx.web.bajarasClub.model.EstadoEdicion;

public class RaffleForm {
	public String nombre;
	  public Integer digitos;
	  public Integer maxValor;
	  public Integer numerosPorBoleto;
	  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	  public LocalDate fechaInicio;
	  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	  public LocalDate fechaFin;
	  public Double precioBoleto;
	  public String ganadorRifa;
	  public Integer totalVendido; // readonly en UI pero viaja en el POST
	  public MultipartFile image;
	  
	  public String estadoEdicion; 
	  
	  
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Integer getDigitos() {
		return digitos;
	}
	public void setDigitos(Integer digitos) {
		this.digitos = digitos;
	}
	public Integer getMaxValor() {
		return maxValor;
	}
	public void setMaxValor(Integer maxValor) {
		this.maxValor = maxValor;
	}
	public Integer getNumerosPorBoleto() {
		return numerosPorBoleto;
	}
	public void setNumerosPorBoleto(Integer numerosPorBoleto) {
		this.numerosPorBoleto = numerosPorBoleto;
	}
	public LocalDate getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public LocalDate getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}
	public Double getPrecioBoleto() {
		return precioBoleto;
	}
	public void setPrecioBoleto(Double precioBoleto) {
		this.precioBoleto = precioBoleto;
	}
	public String getGanadorRifa() {
		return ganadorRifa;
	}
	public void setGanadorRifa(String ganadorRifa) {
		this.ganadorRifa = ganadorRifa;
	}
	public Integer getTotalVendido() {
		return totalVendido;
	}
	public void setTotalVendido(Integer totalVendido) {
		this.totalVendido = totalVendido;
	}
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}
	public String getEstadoEdicion() {
		return estadoEdicion;
	}
	public void setEstadoEdicion(String estadoEdicion) {
		this.estadoEdicion = estadoEdicion;
	}
	
    
	  
	  
    
    
}
