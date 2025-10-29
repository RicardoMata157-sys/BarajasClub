package com.mx.web.bajarasClub.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;

public interface ServiceNumero {
	
	
	public void guardaNumeroRifa(Numero numero);
	
	public void actualizaNumeros(List<Numero> numero);
	
	
	public List<Numero> regresaNumerosRifaActializada(Rifa rifa);
	
	
	public int deleteByRifaIdAndNumero(Rifa rifa, List<String> borrables);
	
	public List<Numero> regresaNumerosSeleccionados(Rifa rifa, List<String> seleccionados);
	
	
	public List<String> regresaNumerosRandomBaseDisponibles(String idSession,Long rifaId ,int lim);
	
    public int actulizaEstadoSeleccionado(String sid,List<Integer> ids);

	public int limpiarSeleccion(List<String> numeroIds, Integer rifaId);

	public boolean SeleccionUnico(String sid,String numero, Integer rifaId,int minutos);

	public boolean limpiarSeleccionUnico(String sid,String numero, Integer rifaId);

	
	public long countByRifaAndEstadoNombre(Integer raffleId, String string);

	public Page<Numero> searchByRifaAndEstado(Integer raffleId, Integer estadoId, Pageable numerosPg);

	

}
