package com.mx.web.bajarasClub.model;



import java.time.LocalDateTime;
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

@Entity
@Table(name = "boleto")
public class Boleto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "rifa_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_boleto_rifa"))
  private Rifa rifa;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cliente_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_boleto_cliente"))
  private Cliente cliente;

  @Column(name = "fecha_compra")
  private LocalDateTime fechaCompra;
  
  @Column(name = "folio")
  private String folio;

 
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "estado_boleto", nullable = false,
              foreignKey = @ForeignKey(name = "fk_estado_pago"))
  private EstadoBoleto EstadoBoleto;   // DISPONIBLE / APARTADO / LIQUIDADO, etc.

  // Boleto 1..N Número (los números asignados a este boleto)
  @OneToMany(mappedBy = "boleto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Numero> numeros = new ArrayList<>();
  
  
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tipo_pago_id", nullable = false,
              foreignKey = @ForeignKey(name = "fk_rifa_tipo_pago"))
  private TipoPago tipoPago;  //efectivo o transferencias


public Integer getId() {
	return id;
}


public void setId(Integer id) {
	this.id = id;
}


public Rifa getRifa() {
	return rifa;
}


public void setRifa(Rifa rifa) {
	this.rifa = rifa;
}


public Cliente getCliente() {
	return cliente;
}


public void setCliente(Cliente cliente) {
	this.cliente = cliente;
}


public LocalDateTime getFechaCompra() {
	return fechaCompra;
}


public void setFechaCompra(LocalDateTime fechaCompra) {
	this.fechaCompra = fechaCompra;
}


public EstadoBoleto getEstadoBoleto() {
	return EstadoBoleto;
}


public void setEstadoBoleto(EstadoBoleto estadoBoleto) {
	EstadoBoleto = estadoBoleto;
}


public List<Numero> getNumeros() {
	return numeros;
}


public void setNumeros(List<Numero> numeros) {
	this.numeros = numeros;
}


public TipoPago getTipoPago() {
	return tipoPago;
}


public void setTipoPago(TipoPago tipoPago) {
	this.tipoPago = tipoPago;
}


public String getFolio() {
	return folio;
}


public void setFolio(String folio) {
	this.folio = folio;
}
  
  
  
  
  
  
  //numero de boletos debes tener la siguiente conbinacion  
  
}

