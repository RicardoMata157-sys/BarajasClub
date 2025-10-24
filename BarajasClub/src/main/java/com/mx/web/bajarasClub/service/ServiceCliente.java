package com.mx.web.bajarasClub.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.mx.web.bajarasClub.dto.CompraRequest;
import com.mx.web.bajarasClub.model.Cliente;

public interface ServiceCliente {
	
	
	public Cliente guardaClienteEdicion(Cliente cliente);
	
	public List<Cliente> findByEmailOrTelefono(String email, String telefono);
	
	public Optional<Cliente> findByTelefono(String telefono);

	public Cliente getById(Integer clienteId);

	
	Cliente createOrUpdateByTelefono(String telefonoRaw, CompraRequest cliente);
}
