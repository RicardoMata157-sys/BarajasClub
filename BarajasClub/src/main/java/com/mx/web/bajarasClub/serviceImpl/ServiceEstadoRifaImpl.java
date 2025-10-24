package com.mx.web.bajarasClub.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.model.EstadoEdicion;
import com.mx.web.bajarasClub.repository.RepositoryEstadoRifas;
import com.mx.web.bajarasClub.service.ServiceEstadoRifa;


@Service
public class ServiceEstadoRifaImpl implements ServiceEstadoRifa {

	@Autowired
	private RepositoryEstadoRifas  repositoryEstadoRifas;
	@Override
	public List<EstadoEdicion> getConsultaEstados() {
		// TODO Auto-generated method stub
		return repositoryEstadoRifas.findAllByActivoTrueOrderByNombreAsc();
	}
	@Override
	public List<EstadoEdicion> getConsultaEstado(String nombre) {
		// TODO Auto-generated method stub
		return repositoryEstadoRifas.findBynombre(nombre);
	}
	@Override
	public List<EstadoEdicion> getEstadosActivos() {
		// TODO Auto-generated method stub
		return repositoryEstadoRifas.findBynombre("ACTIVA");
	}
	@Override
	public EstadoEdicion consultaEdicionEstado(String id) {
		// TODO Auto-generated method stub
		return repositoryEstadoRifas.findByid(Integer.parseInt(id));
	}

}
