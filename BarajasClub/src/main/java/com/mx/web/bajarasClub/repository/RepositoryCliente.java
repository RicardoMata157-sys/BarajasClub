package com.mx.web.bajarasClub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mx.web.bajarasClub.model.Cliente;

@Repository
public interface RepositoryCliente extends JpaRepository<Cliente, Integer> {
	
	
	List<Cliente> findByEmailAndTelefono(String email,String telefono);
	
	List<Cliente> findFirstByEmailIgnoreCaseOrTelefono(String email, String telefono);

}
