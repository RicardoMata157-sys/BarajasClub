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
		return respoitoryBoleto.search(raffleId, estadoId, term, boletosPg);
	}

}
