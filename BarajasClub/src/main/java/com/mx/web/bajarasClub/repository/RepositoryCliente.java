package com.mx.web.bajarasClub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mx.web.bajarasClub.model.Cliente;

@Repository
public interface RepositoryCliente extends JpaRepository<Cliente, Integer> {
	
	
	List<Cliente> findByEmailAndTelefono(String email,String telefono);
	
//	List<Cliente> findFirstByEmailIgnoreCaseOrTelefono(String email, String telefono);
	
	
	List<Cliente> findByTelefono(String telefono);
	
	Cliente findByidCliente(Integer id);
	
	
	  @Query("""
			    select c
			    from Cliente c
			    where ( :email   is not null and :email   <> '' and c.email    is not null and lower(c.email) = lower(:email) )
			       or ( :telefono is not null and :telefono <> '' and c.telefono is not null and c.telefono = :telefono )
			  """)
			  List<Cliente> findFirstByEmailOrTelefono(@Param("email") String email,
			                                               @Param("telefono") String telefono);

}
