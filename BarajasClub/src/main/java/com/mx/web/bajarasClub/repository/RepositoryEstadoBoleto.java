package com.mx.web.bajarasClub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mx.web.bajarasClub.model.EstadoBoleto;

@Repository
public interface RepositoryEstadoBoleto extends JpaRepository<EstadoBoleto, Integer> {
	
	@Query(nativeQuery = true, value = "SELECT idestadoboleto, isestado, nombre "
			+ "FROM public.estado_boleto "
			+ "WHERE idestadoboleto IN (2);")
	public EstadoBoleto regresaEstadoVendido();

}
