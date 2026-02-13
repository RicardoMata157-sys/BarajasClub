package com.mx.web.bajarasClub.service;

import java.util.List;

import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;

public interface ServiceNumero {
	
	
	public void guardaNumeroRifa(Numero numero);
	
	public void actualizaNumeros(List<Numero> numero);
	
	
	public List<Numero> regresaNumerosRifaActializada(Rifa rifa);
	
	
	public int deleteByRifaIdAndNumero(Rifa rifa, List<String> borrables);
	
	public List<Numero> regresaNumerosSeleccionados(Rifa rifa, List<String> seleccionados);
	
	
	public List<String> regresaNumerosRandomBaseDisponibles(Long rifaId ,int lim);
	
    public int actulizaEstadoSeleccionado(List<Integer> ids);

	public int limpiarSeleccion(List<String> numeroIds, Integer rifaId);

	public int limpiarSeleccionUnico(String numero, Integer rifaId);

	

}
