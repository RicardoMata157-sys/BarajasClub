package com.mx.web.bajarasClub.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.model.EstadoEdicion;
import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;
import com.mx.web.bajarasClub.repository.RaffleRepository;
import com.mx.web.bajarasClub.service.RaffleService;


@Service 
public class RaflleServiceImpl implements RaffleService {

	@Autowired
	 private  RaffleRepository repo;
	
	
	
	
	
	@Override
	public List<Rifa> listAll() {
		  return repo.findAll();
	}





	@Override
	public void guardaRifaService(Rifa rifa) {
		repo.save(rifa);
		
	}





	@Override
	public Rifa obtenerRifaPorId(Integer id) {
		// TODO Auto-generated method stub
		return repo.findByid(id);
	}





	@Override
	public Rifa regresaRifaEntidad(String nombre, LocalDate fechaInicio, EstadoEdicion estadoRifa) {
		// TODO Auto-generated method stub
		return repo.findByNombreAndFechaInicioAndEstadoEdicion(nombre, fechaInicio, estadoRifa);
	}





	@Override
	public void actualizaRifaService(Rifa rifa) {
		repo.save(rifa);
		
	}





	@Override
	public List<Rifa> regresaRifaActivas(EstadoEdicion estadoRifa) {
		
		return repo.findByestadoEdicion(estadoRifa);
	}

}
