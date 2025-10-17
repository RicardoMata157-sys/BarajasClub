package com.mx.web.bajarasClub.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.dto.CompraRequest;
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

	@Override
	public List<Cliente> findByEmailOrTelefono(String email, String telefono) {
		if ((email == null || email.isBlank()) && (telefono == null || telefono.isBlank())) {
			return new ArrayList<Cliente>();
		}
		return repositoryCliente.findFirstByEmailIgnoreCaseOrTelefono(email != null && !email.isBlank() ? email : null,
				telefono != null && !telefono.isBlank() ? telefono : null);
	}

	@Override
	public Optional<Cliente> findByTelefono(String telefono) {
		// TODO Auto-generated method stub
		return repositoryCliente.findByTelefono(telefono);
	}

	@Override
	public Cliente getById(Integer clienteId) {
		// TODO Auto-generated method stub
		return repositoryCliente.findByidCliente(clienteId);
	}

	@Override
	public Cliente createOrUpdateByTelefono(String telefono,CompraRequest request) {
		 String tel = normalizaTel(telefono);
	        if (tel == null || tel.isBlank()) {
	            throw new IllegalArgumentException("Teléfono inválido");
	        }
	        
	        Cliente c = repositoryCliente.findByTelefono(telefono).orElseGet(Cliente::new);
	        
	        if (c.getIdCliente() == null) {
	            // Nuevo
	            c.setTelefono(telefono.trim());
	        }
	        
	        if (request != null) {
	            if (notBlank(request.getNombre()))     c.setNombre(request.getNombre().trim());
//	            if (notBlank(request.getApellidos()))  c.setApellidos(request.getApellidos().trim());
	            if (notBlank(request.getEmail()))      c.setEmail(request.getEmail().trim());
//	            if (notBlank(request.getCiudad()))     c.setCiudad(request.getCiudad().trim());
	            if (notBlank(request.getEstado()))     c.setEstado(request.getEstado().trim());
	            if (notBlank(request.getCodigoPostal()))         c.setCodigo_postal((request.getCodigoPostal().trim()));
	        }
	        
	        return repositoryCliente.save(c);
	        
//	        if (existingOpt.isPresent()) {
//	            Cliente existing = existingOpt.get();
//	            if (updater != null) updater.accept(existing);
//	            return repositoryCliente.save(existing);
//	        }
//	        
//	        Cliente nuevo = new Cliente();
//	        nuevo = NumeroGenerator.generaCliente(request);
//	        nuevo.setTelefono(tel);
//	        if (updater != null) updater.accept(nuevo);
//	        
//	        
//	        try {
//	            return repositoryCliente.save(nuevo);
//	        } catch (DataIntegrityViolationException e) {
//	            // Carrera: otro hilo/req creó el mismo teléfono antes de este save
//	            // Releer y actualizar
//	            Optional<Cliente> again = repositoryCliente.findByTelefono(tel);
//	            if (again.isPresent()) {
//	                Cliente existente = again.get();
//	                if (updater != null) updater.accept(existente);
//	                return repositoryCliente.save(existente);
//	            }
//	            // Si realmente fue otra violación no relacionada, re-lanzar
//	            throw e;
//	        }
//	        
	        
	
	}
	
	private boolean notBlank(String s) { return s != null && !s.isBlank(); }
	
	 private String normalizaTel(String t) {
	        if (t == null) return null;
	        String digits = t.replaceAll("[^\\d]", ""); 
	        return digits; // por ahora, solo dígitos
	    }

}
