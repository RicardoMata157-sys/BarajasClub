package com.mx.web.bajarasClub.service;

import java.util.List;

import com.mx.web.bajarasClub.model.Cliente;

public interface ServiceCliente {
	
	
	public Cliente guardaClienteEdicion(Cliente cliente);
	
	public List<Cliente> findByEmailOrTelefono(String email, String telefono);

}
