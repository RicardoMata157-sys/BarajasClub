package com.mx.web.bajarasClub.repository;

import java.time.LocalDateTime;
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
			+ "        WHERE rifa_id = :rifaId "
			+ "        AND nb.boleto_id is null "
			+ "        AND seleccionado is false"
			+ "        AND  sel_session_id is  NULL "
			+ "        ORDER BY random() "
			+ "        LIMIT :lim")
	public List<Numero> pickRandomDisponibles(@Param("rifaId") Long rifaId, @Param("lim") int lim);
	
	
	
	@Query(nativeQuery = true, value = "SELECT * FROM numero_boleto np  where VALOR in (:numeros) and RIFA_ID = :rifaId ")
	public List<Numero> regresaNumeroSeleccionado(@Param("rifaId") Integer rifaId, @Param("numeros") List<String> numeros);
	
	
	@Modifying()
	@Query(nativeQuery = true, value = "UPDATE numero_boleto "
			+ "SET seleccionado=true , sel_session_id = :idsession ,sel_expira  = :until "
			+ "where idnumero in (:ids)"
			)
	public int actulizaEstadoSeleccionado(
			@Param("ids") List<Integer> ids, 
			@Param("idsession") String idSession,
			@Param("until") LocalDateTime until
			);
	
	
	@Modifying()
	@Query(nativeQuery = true, value = "UPDATE numero_boleto "
			+ "SET seleccionado= :estado , sel_session_id = :idSession, sel_expira = :until  "
			+ "WHERE rifa_id = :rifaId " +
		      "  AND valor   = :numero " +
		      "  AND boleto_id IS NULL " + // si tienes esta columna; si no, quítala
		      "  AND (seleccionado = FALSE " +
		      "       OR sel_expira < :now " +
		      "       OR sel_session_id = :idSession)" )
	public int actulizaEstadoSeleccionadoUnico(
			@Param("idSession") String sessionId,
			@Param("rifaId") Integer ids,
			@Param("numero") String  valor,
			@Param("until") LocalDateTime until,
			@Param("estado") boolean  estado,
			 @Param("now") java.time.LocalDateTime now
			);
	
	
	@Modifying()
	@Query(nativeQuery = true, value = "UPDATE numero_boleto "
			+ "SET seleccionado=false, sel_session_id = NULL,sel_expira = NULL  "
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

	   
	    
	    
	    @Modifying
	    @Query("""
	    UPDATE Numero n
	       SET n.seleccionado = FALSE,
	           n.seleccionadoSessionId    = NULL,
	           n.seleccionadoExpira    = NULL
	     WHERE n.rifa.id = :rifaId
	       AND n.valor = :label
	       AND (n.seleccionadoSessionId = :owner )
	    """)
	    int liberar(@Param("rifaId") Integer rifaId,
	                @Param("label") String label,
	                @Param("owner") String owner
	               );
	   
	   
	
}
