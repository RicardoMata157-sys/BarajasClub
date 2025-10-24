package com.mx.web.bajarasClub.service;

import java.time.LocalDate;
import java.util.List;

import com.mx.web.bajarasClub.model.EstadoEdicion;
import com.mx.web.bajarasClub.model.Rifa;

public interface RaffleService {

	public Rifa obtenerRifaPorId(Integer rifa);

	public List<Rifa> listAll();

	public void guardaRifaService(Rifa rifa);
	
	
	
	public void actualizaRifaService(Rifa numero);
	
	public Rifa regresaRifaEntidad(String nombre , LocalDate fechaInicio, EstadoEdicion estadoRifa);
	
	
	public List<Rifa> regresaRifaActivas(EstadoEdicion estadoRifa);

}
