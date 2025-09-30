package com.mx.web.bajarasClub.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.mx.web.bajarasClub.model.EstadoEdicion;
import com.mx.web.bajarasClub.model.Rifa;


public interface RaffleRepository extends JpaRepository<Rifa, Integer> {
	
	
	public Rifa findByid(Integer id);
	
	public Rifa findByNombreAndFechaInicioAndEstadoEdicion(
		        String nombre,
		        LocalDate fechaInicio,
		        EstadoEdicion estadoEdicion
		    );
	
	public List<Rifa> findByestadoEdicion(EstadoEdicion estadoEdicion);
	
	
	
	
	
}
