package com.mx.web.bajarasClub.service;

import java.util.List;

import com.mx.web.bajarasClub.model.TipoPago;

public interface ServiceTipoPago {
	
	public List<TipoPago> getAllTipoPagos();
	
	public TipoPago regresaTipoPagoId(Long id);

}
