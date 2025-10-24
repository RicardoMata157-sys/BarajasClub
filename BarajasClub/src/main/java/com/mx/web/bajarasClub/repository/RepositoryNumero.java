package com.mx.web.bajarasClub.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	
	@Query(nativeQuery = true, value = "SELECT * "
			+ "        FROM numero_boleto nb  "
			+ "        WHERE rifa_id = :rifaId AND nb.boleto_id is null and seleccionado is false "
			+ "        ORDER BY random() "
			+ "        LIMIT :lim")
	public List<Numero> pickRandomDisponibles(@Param("rifaId") Long rifaId, @Param("lim") int lim);
	
	
	
	@Query(nativeQuery = true, value = "SELECT * FROM numero_boleto np  where VALOR in (:numeros) and RIFA_ID = :rifaId ")
	public List<Numero> regresaNumeroSeleccionado(@Param("rifaId") Integer rifaId, @Param("numeros") List<String> numeros);
	
	
	@Modifying()
	@Query(nativeQuery = true, value = "UPDATE numero_boleto "
			+ "SET seleccionado=true "
			+ "where idnumero in (:ids)  " )
	public int actulizaEstadoSeleccionado(@Param("ids") List<Integer> ids);
	
	
	@Modifying()
	@Query(nativeQuery = true, value = "UPDATE numero_boleto "
			+ "SET seleccionado= :estado "
			+ "where rifa_id in (:ids) "
			+ "and valor in (:numero)   " )
	public int actulizaEstadoSeleccionadoUnico(@Param("ids") Integer ids, @Param("numero") String  valor, @Param("estado") boolean  estado);
	
	
	@Modifying()
	@Query(nativeQuery = true, value = "UPDATE numero_boleto "
			+ "SET seleccionado=false "
			+ "where rifa_id in (:ids) "
			+ "and valor in (:numeros) " )
	public int actulizaEstadoDeseleccionado(@Param("numeros") List<String> numeros, @Param("ids") Integer id);
	
	
	
	
	
	@Query(nativeQuery = true, value = "select * "
			+ "from numero_boleto nb  "
			+ "where nb.rifa_id  = :id "
			+ "and nb.valor = :valor"  )
	public Numero consultaNumeroEstadoSeleccion(@Param("id")Integer id, @Param("valor")String valor);
	
	   @Query("""
			      SELECT COUNT(n) FROM Numero n
			      LEFT JOIN n.rifa r
			      LEFT JOIN n.boleto b
			      LEFT JOIN b.EstadoBoleto eb
			      WHERE (:rifaId IS NULL OR r.id = :rifaId)
			        AND (eb IS NOT NULL AND UPPER(eb.nombre) = UPPER(:estadoNombre))
			    """)
			    long countByRifaAndEstadoNombre(Integer rifaId, String estadoNombre);
			
	   
	   
	   
	   
	   

	    @Query("""
	      SELECT n FROM Numero n
	      LEFT JOIN n.rifa r
	      LEFT JOIN n.boleto b
	      LEFT JOIN b.EstadoBoleto eb
	      WHERE (:rifaId IS NULL OR r.id = :rifaId)
	        AND (
	              :estadoId IS NULL
	              OR (eb IS NOT NULL AND eb.idEstadoBoleto = :estadoId)
	            )
	      ORDER BY n.valor ASC
	    """)
	  public  Page<Numero> searchByRifaAndEstado(Integer rifaId, Integer estadoId, Pageable pageable);

	   
	   
	   
	
}
