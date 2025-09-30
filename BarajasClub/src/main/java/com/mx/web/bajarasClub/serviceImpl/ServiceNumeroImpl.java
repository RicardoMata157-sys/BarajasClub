package com.mx.web.bajarasClub.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;
import com.mx.web.bajarasClub.repository.RepositoryNumero;
import com.mx.web.bajarasClub.service.ServiceNumero;

@Service
public class ServiceNumeroImpl implements ServiceNumero {

	@Autowired
	private RepositoryNumero repositoryNumero;
	
	
	
	@Override
	public void guardaNumeroRifa(Numero numero) {
		
		repositoryNumero.save(numero);

	}



	@Override
	public List<Numero> regresaNumerosRifaActializada(Rifa rifa) {
		return repositoryNumero.findByrifa(rifa);
	}



	@Override
	public void actualizaNumeros(List<Numero> numero) {
		repositoryNumero.saveAll(numero);
		
	}



	@Override
	@Transactional
	public int deleteByRifaIdAndNumero(Rifa rifa, List<String> borrables) {
		return repositoryNumero.deletNumeroRifa(rifa.getId(), borrables);
		
	}



	@Override
	public List<String> regresaNumerosRandomBaseDisponibles(Long rifaId, int lim) {
		// TODO Auto-generated method stub
		return repositoryNumero.pickRandomDisponibles(rifaId, lim);
	}



	@Override
	public List<Numero> regresaNumerosSeleccionados(Rifa rifa, List<String> seleccionados) {
		// TODO Auto-generated method stub
		return repositoryNumero.regresaNumeroSeleccionado(rifa.getId(), seleccionados);
	}

}
