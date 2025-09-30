package com.mx.web.bajarasClub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;

public interface RepositoryNumero extends JpaRepository<Numero, String>{
	
	public List<Numero> findByrifa(Rifa rifa);
	
	
	@Modifying()
    @Query(nativeQuery = true, value = " DELETE FROM numero_boleto np "
    		+ " where VALOR in (:borrables) and RIFA_ID = :idRifa")
    
	public int deletNumeroRifa(@Param("idRifa") Integer idRifa,@Param("borrables") List<String> borrables);

	
	@Query(nativeQuery = true, value = "SELECT valor "
			+ "        FROM numero_boleto nb  "
			+ "        WHERE rifa_id = :rifaId AND nb.boleto_id is null "
			+ "        ORDER BY random() "
			+ "        LIMIT :lim")
	public List<String> pickRandomDisponibles(@Param("rifaId") Long rifaId, @Param("lim") int lim);
	
	
	
	@Query(nativeQuery = true, value = "SELECT * FROM numero_boleto np  where VALOR in (:numeros) and RIFA_ID = :rifaId ")
	public List<Numero> regresaNumeroSeleccionado(@Param("rifaId") Integer rifaId, @Param("numeros") List<String> numeros);
	
	
	
	
}
