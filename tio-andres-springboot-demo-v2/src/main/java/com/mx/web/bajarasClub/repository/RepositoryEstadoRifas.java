package com.mx.web.bajarasClub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mx.web.bajarasClub.model.EstadoEdicion;

@Repository
public interface RepositoryEstadoRifas extends JpaRepository<EstadoEdicion, Integer> {
	 List<EstadoEdicion> findAllByActivoTrueOrderByNombreAsc();
	 
	 
	 
	 List<EstadoEdicion> findBynombre(String nombre);
	 
	 EstadoEdicion findByid(Integer id);

}
