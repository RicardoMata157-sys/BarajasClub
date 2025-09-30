package com.mx.web.bajarasClub.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

}
