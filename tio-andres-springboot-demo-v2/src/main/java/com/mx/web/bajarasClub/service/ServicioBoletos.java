package com.mx.web.bajarasClub.service;

import java.util.List;

import com.mx.web.bajarasClub.model.Boleto;

public interface ServicioBoletos {
	
	
	public List<Boleto> regreseEstadosBoletos();
	
	
	public void guardaBoletoClienteAsignado(Boleto boleto);

}
