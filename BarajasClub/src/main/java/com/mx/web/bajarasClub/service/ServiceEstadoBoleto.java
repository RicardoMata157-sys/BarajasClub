package com.mx.web.bajarasClub.service;

import java.util.List;

import com.mx.web.bajarasClub.model.EstadoBoleto;

public interface ServiceEstadoBoleto {
	
	
	public EstadoBoleto regresaEstadoVendido();
	
	public EstadoBoleto regresaEstadoApartado();
	
	public List<EstadoBoleto> regresaAllEstados();
	

	public EstadoBoleto regresaEstadoCancelado();
	
	public EstadoBoleto regresaEstadoGanador();
}
