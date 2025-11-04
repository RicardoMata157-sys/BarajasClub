package com.mx.web.bajarasClub.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.model.Boleto;
import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;
import com.mx.web.bajarasClub.repository.RepositoryNumero;
import com.mx.web.bajarasClub.service.ServiceNumero;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
	@Transactional
	public List<String> regresaNumerosRandomBaseDisponibles(String sid,Long rifaId, int lim) {
		List<String> nums = new ArrayList<String>();
		List<Integer> idNums = new ArrayList<Integer>();
		repositoryNumero.pickRandomDisponibles(rifaId, lim).stream().forEach(numero -> {
			nums.add(numero.getValor());
			idNums.add(numero.getIdNumero());
		});
		actulizaEstadoSeleccionado(sid,idNums);

		return nums;
	}

	@Override
	public List<Numero> regresaNumerosSeleccionados(Rifa rifa, List<String> seleccionados) {
		// TODO Auto-generated method stub
		return repositoryNumero.regresaNumeroSeleccionado(rifa.getId(), seleccionados);
	}

	@Transactional
	@Override
	public int actulizaEstadoSeleccionado(String sid, List<Integer> ids) {

		return repositoryNumero.actulizaEstadoSeleccionado(ids,sid,LocalDateTime.now().plusMinutes(10));
	}

	
	@Transactional
	@Override
	public int limpiarSeleccion(List<String> numeroIds, Integer rifaId) {
		// TODO Auto-generated method stub
		 return repositoryNumero.actulizaEstadoDeseleccionado(numeroIds,rifaId);
	}
	
	
	@Override
	public boolean SeleccionUnico(String sid, String numero, Integer rifaId, int minutos) {
		
		int updated = repositoryNumero.actulizaEstadoSeleccionadoUnico(sid, rifaId, numero,LocalDateTime.now().plusMinutes(minutos), true,LocalDateTime.now());
		return updated == 1;
	}

	@Transactional
	@Override
	public boolean limpiarSeleccionUnico(String sid,String numero, Integer rifaId) {
		// TODO Auto-generated method stub
//		Numero numeroData = repositoryNumero.consultaNumeroEstadoSeleccion(rifaId,numero);
//		if(numeroData.getSeleccionado()) {
//			return repositoryNumero.actulizaEstadoSeleccionadoUnico(sid,rifaId, numero,false);
//		}
//		if(!numeroData.getSeleccionado()) {
//			return repositoryNumero.actulizaEstadoSeleccionadoUnico(sid,rifaId, numero,true);
//		}
		
		int updated = repositoryNumero.liberar(rifaId, numero,sid);
		
		return updated == 1;
		
		
		 
	}

	@Override
	public long countByRifaAndEstadoNombre(Integer raffleId, String string) {
		// TODO Auto-generated method stub
		return repositoryNumero.countByRifaAndEstadoNombre(raffleId, string);
	}

	@Override
	public Page<Numero> searchByRifaAndEstado(Integer raffleId, Integer estadoId, Pageable numerosPg) {
		// TODO Auto-generated method stub
		return repositoryNumero.searchByRifaAndEstado(raffleId,estadoId,numerosPg);
	}

	@Override
	public List<Numero> regresaNumerosBoletosAsignado(Boleto boleto) {
		// TODO Auto-generated method stub
		return repositoryNumero.findByboleto(boleto);
	}

	@Override
	@Transactional
	public boolean limpiarNumeroAsigandoBoleto(Integer rifaId, Integer idNumero) {
		// TODO Auto-generated method stub

		int updated = repositoryNumero.liberarNumero(rifaId, idNumero);

		return updated == 1;
		
	}

	@Override
	public Numero regresaNumeroPorId(Integer id) {
		// TODO Auto-generated method stub
		return repositoryNumero.findByidNumero(id);
	}


	
	
	
	
	


	

}
