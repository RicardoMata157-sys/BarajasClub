package com.mx.web.bajarasClub.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
	public List<Cliente> findByTelefono(String telefono) {
		// TODO Auto-generated method stub
		return repositoryCliente.findByTelefono(telefono);
	}

	@Override
	public Cliente getById(Integer clienteId) {
		// TODO Auto-generated method stub
		return repositoryCliente.findByidCliente(clienteId);
	}

	@Transactional
	@Override
	public Cliente createOrUpdateByTelefono(String telefonoRaw, CompraRequest request) {
	    String tel = normalizaTel(telefonoRaw);
	    if (tel == null || tel.isBlank()) {
	        throw new IllegalArgumentException("Teléfono inválido");
	    }

	    // 1) Buscar por teléfono normalizado
	    Cliente c = repositoryCliente.findByTelefono(tel).stream().findFirst().orElseGet(() -> {
	        Cliente nuevo = new Cliente();
	        nuevo.setTelefono(tel);
	        return nuevo;
	    });

	    // 2) Merge no destructivo desde el request
	    if (request != null) {
	        if (notBlank(request.getNombre()))         c.setNombre(request.getNombre().trim());
	        if (notBlank(request.getApellidoP()))      c.setApellido_patrno(request.getApellidoP().trim());   // si tienes Apellido P
	        if (notBlank(request.getApellidoM()))      c.setApellido_materno(request.getApellidoM().trim());
	        if (notBlank(request.getEmail()))          c.setEmail(request.getEmail().trim().toLowerCase());
	        if (notBlank(request.getEstado()))         c.setEstado(request.getEstado().trim());
	        if (notBlank(request.getCodigoPostal()))   c.setCodigo_postal(request.getCodigoPostal().trim());
	        // si tienes más campos opcionales, agrégalos aquí con el mismo patrón
	    }

	    // 3) Guardar (con retry simple por duplicado)
	    try {
	        return repositoryCliente.save(c);
	    } catch (DataIntegrityViolationException e) {
	        // Otro hilo pudo insertar el mismo teléfono antes de este save
	        Cliente again = repositoryCliente.findByTelefono(tel).stream().findFirst()
	                .orElseThrow(() -> e); // si no estaba relacionado al teléfono, relanzamos
	        // Volvemos a aplicar el merge por si el 'again' es distinto de 'c'
	        if (request != null) {
	            if (notBlank(request.getNombre()))         again.setNombre(request.getNombre().trim());
	            if (notBlank(request.getApellidoP()))      again.setApellido_patrno(request.getApellidoP().trim());
	            if (notBlank(request.getApellidoM()))      again.setApellido_materno(request.getApellidoM().trim());
	            if (notBlank(request.getEmail()))          again.setEmail(request.getEmail().trim().toLowerCase());
	            if (notBlank(request.getEstado()))         again.setEstado(request.getEstado().trim());
	            if (notBlank(request.getCodigoPostal()))   again.setCodigo_postal(request.getCodigoPostal().trim());
	        }
	        return repositoryCliente.save(again);
	    }
	}

	
	private boolean notBlank(String s) { return s != null && !s.isBlank(); }
	
	 private String normalizaTel(String t) {
	        if (t == null) return null;
	        String digits = t.replaceAll("[^\\d]", ""); 
	        return digits; // por ahora, solo dígitos
	    }

}
