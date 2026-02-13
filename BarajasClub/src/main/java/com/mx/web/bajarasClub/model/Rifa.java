package com.mx.web.bajarasClub.model;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rifa" )
public class Rifa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String nombre;
  
  private Integer digitos;
  
  @Column(name = "max_valor")
  private Integer maxValor;
  
  @Column(name = "numeros_por_boleto")
  private Integer numerosPorBoleto;
  
  @Column(name = "fecha_inicio")
  private LocalDate fechaInicio;
  
  
  @Column(name = "fecha_fin")
  private LocalDate fechaFin;
  
  @Column(name = "precio_boleto")
  private Double precioBoleto;

  
  @Column(name="ganador_rifa")
  private String ganadorRifa;
  
  @Column(name = "total_vendido")
  private Integer totalVeidido;
  
  
  @Column(name = "ruta_imagen")
  private String pathImagen;
  
  @Transient
  private String IdCompra;
  
  
  
  
 
  
  public String getIdCompra() {
	return IdCompra;
}

public void setIdCompra(String idCompra) {
	IdCompra = idCompra;
}

public void addNumero(Numero n) {
	    numeros.add(n);
	    n.setRifa(this);
	}
  
  

  // Rifa 1..N Boleto
  @OneToMany(mappedBy = "rifa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Boleto> boletos = new ArrayList<>();

  // Rifa 1..N Número (todos los números de la rifa)
  @OneToMany(mappedBy = "rifa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<Numero> numeros = new ArrayList<>();
  
  
  
  
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "estado_edicion_id", nullable = false,
              foreignKey = @ForeignKey(name = "fk_estado_edicion"))
  private EstadoEdicion estadoEdicion;  //efectivo o transferencias
  



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

public String getGanadorRifa() {
	return ganadorRifa;
}

public void setGanadorRifa(String ganadorRifa) {
	this.ganadorRifa = ganadorRifa;
}

public Integer getTotalVeidido() {
	return totalVeidido;
}

public void setTotalVeidido(Integer totalVeidido) {
	this.totalVeidido = totalVeidido;
}



public List<Boleto> getBoletos() {
	return boletos;
}

public void setBoletos(List<Boleto> boletos) {
	this.boletos = boletos;
}

public List<Numero> getNumeros() {
	return numeros;
}

public void setNumeros(List<Numero> numeros) {
	this.numeros = numeros;
}







public Double getPrecioBoleto() {
	return precioBoleto;
}

public void setPrecioBoleto(Double precioBoleto) {
	this.precioBoleto = precioBoleto;
}

public String getPathImagen() {
	return pathImagen;
}

public void setPathImagen(String pathImagen) {
	this.pathImagen = pathImagen;
}

public EstadoEdicion getEstadoEdicion() {
	return estadoEdicion;
}

public void setEstadoEdicion(EstadoEdicion estadoEdicion) {
	this.estadoEdicion = estadoEdicion;
}




public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}




public Rifa( String nombre, Integer digitos, Integer maxValor, Integer numerosPorBoleto,
		LocalDate fechaInicio, LocalDate fechaFin, Double precioBoleto, String ganadorRifa, Integer totalVeidido,
		String pathImagen, List<Boleto> boletos, List<Numero> numeros, EstadoEdicion estadoEdicion) {
	super();
	;
	
	this.nombre = nombre;
	this.digitos = digitos;
	this.maxValor = maxValor;
	this.numerosPorBoleto = numerosPorBoleto;
	this.fechaInicio = fechaInicio;
	this.fechaFin = fechaFin;
	this.precioBoleto = precioBoleto;
	this.ganadorRifa = ganadorRifa;
	this.totalVeidido = totalVeidido;
	this.pathImagen = pathImagen;
	this.boletos = boletos;
	this.numeros = numeros;
	this.estadoEdicion = estadoEdicion;
}

public Rifa() {
	
}




//  // Helpers bidireccionales
//  public void addBoleto(Boleto b) {
//    boletos.add(b);
//    b.setRifa(this);
//  }
//
//  public void removeBoleto(Boleto b) {
//    boletos.remove(b);
//    b.setRifa(null);
//  }
//
//  public void addNumero(Numero n) {
//    numeros.add(n);
//    n.setRifa(this);
//  }
//
//  public void removeNumero(Numero n) {
//    numeros.remove(n);
//    n.setRifa(null);
//  }

  // getters/setters …
  
  
  
  
}
