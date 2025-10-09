package com.mx.web.bajarasClub.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "numero_boleto")
public class Numero {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer idNumero;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "rifa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_numero_rifa"))
	private Rifa rifa;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "boleto_id", nullable = true, foreignKey = @ForeignKey(name = "fk_numero_boleto"))
	private Boleto boleto;
	
	@Column(nullable = false)
	private Boolean seleccionado = false;
	
	

	private String valor;

	
	
	public Numero() {
		
	}
	
	
	
	
	
	public Numero( Rifa rifa, Boleto boleto, String valor) {
		super();
		this.rifa = rifa;
		this.boleto = boleto;
		this.valor = valor;
	}





	
	
	
	

	

	public Integer getIdNumero() {
		return idNumero;
	}





	public void setIdNumero(Integer idNumero) {
		this.idNumero = idNumero;
	}





	public Boolean getSeleccionado() {
		return seleccionado;
	}





	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}





	public Rifa getRifa() {
		return rifa;
	}

	public void setRifa(Rifa rifa) {
		this.rifa = rifa;
	}

	public Boleto getBoleto() {
		return boleto;
	}

	public void setBoleto(Boleto boleto) {
		this.boleto = boleto;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
	
	

}
