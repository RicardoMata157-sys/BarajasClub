package com.mx.web.bajarasClub.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.model.EstadoBoleto;
import com.mx.web.bajarasClub.repository.RepositoryEstadoBoleto;
import com.mx.web.bajarasClub.service.ServiceEstadoBoleto;


@Service
public class ServicioEstadoBoleto implements ServiceEstadoBoleto {

	@Autowired
	private RepositoryEstadoBoleto  repositoryEstadoBoleto;

	
	
	@Override
	public EstadoBoleto regresaEstadoVendido() {
		// TODO Auto-generated method stub
		return repositoryEstadoBoleto.regresaEstadoVendido();
	}



	@Override
	public EstadoBoleto regresaEstadoApartado() {
		// TODO Auto-generated method stub
		return repositoryEstadoBoleto.regresaEstadoApartado();
	}



	@Override
	public List<EstadoBoleto> regresaAllEstados() {
		// TODO Auto-generated method stub
		return repositoryEstadoBoleto.findAll();
	}

}
