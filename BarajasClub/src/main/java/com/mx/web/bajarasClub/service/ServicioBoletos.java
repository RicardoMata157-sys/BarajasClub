package com.mx.web.bajarasClub.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mx.web.bajarasClub.model.Boleto;

public interface ServicioBoletos {
	
	
	public List<Boleto> regreseEstadosBoletos();
	
	
	public void guardaBoletoClienteAsignado(Boleto boleto);
	
	
	public  Page<Boleto> regresaBoletosEstado(Integer raffleId,Integer estadoId,String term, Pageable boletosPg);
	
	public Boleto regresaBoletoId(Integer id);
	
	
	public List<Boleto> matchTelefono(String telefono);
	
	public List<Boleto> matchNombre(String nombre,String apellidoP,String apellidoM);
	
	
	public List<Boleto> matchFolio(String folio);

}
