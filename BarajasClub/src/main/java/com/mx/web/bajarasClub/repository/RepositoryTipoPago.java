package com.mx.web.bajarasClub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mx.web.bajarasClub.model.TipoPago;

public interface RepositoryTipoPago extends JpaRepository<TipoPago, Long> {
	
	 List<TipoPago> findAllByActivoTrueOrderByNombreAsc();
	 
	 @Query(nativeQuery = true, value = "SELECT id, estado_tipo_pago, descipcion_forma_pago  "
	 		+ "FROM public.tipo_pago  "
	 		+ "WHERE id=:id")
	 public TipoPago regresaTipoPagoId(@Param("id")Long id);
	

}
