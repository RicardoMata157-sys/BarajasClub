package com.mx.web.bajarasClub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mx.web.bajarasClub.model.Boleto;

public interface RespoitoryBoleto extends JpaRepository<Boleto, Integer> {
	

//	@Query(value = """
//			  SELECT b FROM Boleto b
//			  LEFT JOIN b.rifa r
//			  LEFT JOIN b.cliente c
//			  LEFT JOIN b.estadoBoleto eb
//			  WHERE (rifaId IS NULL OR r.id = :rifaId)
//			    AND (estadoId IS NULL OR (eb IS NOT NULL AND eb.idEstadoBoleto = :estadoId))
//			    AND (
//			      COALESCE(:q, '') = '' OR
//			      UPPER(b.folio)            LIKE CONCAT('%', UPPER(:q), '%') OR
//			      UPPER(c.nombre)           LIKE CONCAT('%', UPPER(:q), '%') OR
//			      UPPER(c.apellido_patrno)  LIKE CONCAT('%', UPPER(:q), '%') OR   -- ajusta el nombre exacto
//			      UPPER(c.apellido_materno)  LIKE CONCAT('%', UPPER(:q), '%') OR   -- ajusta el nombre exacto
//			      UPPER(c.email)            LIKE CONCAT('%', UPPER(:q), '%') OR
//			      c.telefono                LIKE CONCAT('%', :q, '%')
//			    )
//			  ORDER BY b.fechaCompra DESC, b.id DESC
//			""",nativeQuery = true)
//			Page<Boleto> search(@Param("rifaId") Integer rifaId,
//			                    @Param("estadoId") Long estadoId,
//			                    @Param("q") String q,
//			                    Pageable pageable);
	
	 @Query("""
			    SELECT b FROM Boleto b
			    LEFT JOIN b.rifa r
			    LEFT JOIN b.cliente c
			    LEFT JOIN b.EstadoBoleto eb
			    WHERE (:rifaId IS NULL OR r.id = :rifaId)
			      AND (:estadoId IS NULL OR (eb IS NOT NULL AND eb.idEstadoBoleto = :estadoId))
			      AND (
			        COALESCE(:q, '') = '' OR
			        UPPER(b.folio)           LIKE CONCAT('%', UPPER(:q), '%') OR
			        UPPER(c.nombre)          LIKE CONCAT('%', UPPER(:q), '%') OR
			        UPPER(c.apellido_patrno) LIKE CONCAT('%', UPPER(:q), '%') OR
			        UPPER(c.apellido_materno) LIKE CONCAT('%', UPPER(:q), '%') OR
			        UPPER(c.email)           LIKE CONCAT('%', UPPER(:q), '%') OR
			        c.telefono               LIKE CONCAT('%', :q, '%')
			      )
			    ORDER BY b.fechaCompra DESC, b.id DESC
			  """)
			  Page<Boleto> search(@Param("rifaId") Integer rifaId,
			                      @Param("estadoId") Integer estadoId,
			                      @Param("q") String q,
			                      Pageable pageable);
			

	

}
