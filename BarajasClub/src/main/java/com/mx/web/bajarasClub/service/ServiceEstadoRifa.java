package com.mx.web.bajarasClub.service;

import java.util.List;

import com.mx.web.bajarasClub.model.EstadoEdicion;

public interface ServiceEstadoRifa {
	
	
	public List<EstadoEdicion> getConsultaEstados();
	
	
	public List<EstadoEdicion> getConsultaEstado(String nombre);
	
	

	public List<EstadoEdicion> getEstadosActivos();
	
	public EstadoEdicion consultaEdicionEstado(String id);

}
