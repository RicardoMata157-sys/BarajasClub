package com.mx.web.bajarasClub.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;

public interface ServiceNumero {
	
	
	public void guardaNumeroRifa(Numero numero);
	
	public void actualizaNumeros(List<Numero> numero);
	
	
	public List<Numero> regresaNumerosRifaActializada(Rifa rifa);
	
	
	public int deleteByRifaIdAndNumero(Rifa rifa, List<String> borrables);
	
	public List<Numero> regresaNumerosSeleccionados(Rifa rifa, List<String> seleccionados);
	
	
	public List<String> regresaNumerosRandomBaseDisponibles(Long rifaId ,int lim);

}
