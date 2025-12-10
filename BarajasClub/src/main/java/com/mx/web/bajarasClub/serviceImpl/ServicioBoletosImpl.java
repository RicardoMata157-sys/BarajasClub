package com.mx.web.bajarasClub.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.model.Boleto;
import com.mx.web.bajarasClub.repository.RespoitoryBoleto;
import com.mx.web.bajarasClub.service.ServicioBoletos;

@Service
public class ServicioBoletosImpl implements ServicioBoletos {

	@Autowired
	private RespoitoryBoleto  respoitoryBoleto;
	@Override
	public List<Boleto> regreseEstadosBoletos() {
		// TODO Auto-generated method stub
		return respoitoryBoleto.findAll();
	}
	@Override
	public void guardaBoletoClienteAsignado(Boleto boleto) {
		respoitoryBoleto.save(boleto);
		
	}
	@Override
	public Page<Boleto> regresaBoletosEstado(Integer raffleId, Integer estadoId, String term, Pageable boletosPg) {
		// TODO Auto-generated method stub
		if(raffleId == null &&  estadoId == null) {
			return respoitoryBoleto.searchSinId(raffleId, estadoId, term, boletosPg);
		}else {
			return respoitoryBoleto.search(raffleId, estadoId, term, boletosPg);
		}
		
	}
	@Override
	public Boleto regresaBoletoId(Integer id) {
		// TODO Auto-generated method stub
		return respoitoryBoleto.findByid(id);
	}
	@Override
	public List<Boleto> matchTelefono(String telefono) {
		// TODO Auto-generated method stub
		return respoitoryBoleto.matchTelefono(telefono);
	}
	@Override
	public List<Boleto> matchNombre(String nombre,String apellidoP,String apellidoM) {
		// TODO Auto-generated method stub
		return respoitoryBoleto.findByNombreAtomizadoNativeLike(nombre, apellidoP, apellidoM);
	}
	
	
	@Override
	public List<Boleto> matchFolio(String folio) {
		// TODO Auto-generated method stub
		return respoitoryBoleto.findByfolio(folio);
	}

}
