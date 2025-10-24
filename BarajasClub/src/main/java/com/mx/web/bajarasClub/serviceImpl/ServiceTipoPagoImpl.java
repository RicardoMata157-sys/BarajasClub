package com.mx.web.bajarasClub.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.model.TipoPago;
import com.mx.web.bajarasClub.repository.RepositoryTipoPago;
import com.mx.web.bajarasClub.service.ServiceTipoPago;

@Service
public class ServiceTipoPagoImpl implements ServiceTipoPago {

	
	@Autowired
	private RepositoryTipoPago repositoryTipoPago;
	
	@Override
	public List<TipoPago> getAllTipoPagos() {
		// TODO Auto-generated method stub
		return repositoryTipoPago.findAllByActivoTrueOrderByNombreAsc();
	}

	@Override
	public TipoPago regresaTipoPagoId(Long id) {
		// TODO Auto-generated method stub
		return repositoryTipoPago.regresaTipoPagoId(id);
	}

}
