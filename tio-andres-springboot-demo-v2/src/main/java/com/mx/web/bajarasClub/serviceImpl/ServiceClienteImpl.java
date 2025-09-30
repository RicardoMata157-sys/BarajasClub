package com.mx.web.bajarasClub.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.model.Cliente;
import com.mx.web.bajarasClub.repository.RepositoryCliente;
import com.mx.web.bajarasClub.service.ServiceCliente;

@Service
public class ServiceClienteImpl implements ServiceCliente {

	@Autowired
	private RepositoryCliente repositoryCliente;

	@Override
	public Cliente guardaClienteEdicion(Cliente cliente) {
		Cliente clienteRegistrado = repositoryCliente.findByEmailAndTelefono(cliente.getEmail(), cliente.getTelefono())
				.stream().findFirst().orElse(null);

		if (clienteRegistrado != null) {
			return clienteRegistrado;
		}

		return repositoryCliente.save(cliente);

		
	}

}
