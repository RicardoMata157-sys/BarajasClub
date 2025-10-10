package com.mx.web.bajarasClub.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mx.web.bajarasClub.dto.LimpiarSeleccionRequest;
import com.mx.web.bajarasClub.dto.SeleccionNumeroRequest;
import com.mx.web.bajarasClub.model.Rifa;
import com.mx.web.bajarasClub.model.TipoPago;
import com.mx.web.bajarasClub.service.RaffleService;
import com.mx.web.bajarasClub.service.ServiceNumero;
import com.mx.web.bajarasClub.service.ServiceTipoPago;
import com.mx.web.bajarasClub.serviceImpl.ServicioEstadoBoleto;

@Controller
public class PublicParticiparController {
	
	


	  @Autowired RaffleService servicioRifa;
	  @Autowired ServiceNumero serviceNumero;
//	  @Autowired ServicioBoletos servicioBoletos;
//	  @Autowired ServicioCliente serviceCliente;
	  @Autowired ServiceTipoPago serviceTipoPago;
	  @Autowired ServicioEstadoBoleto serviceEstadoBoleto;

	  // A) Entrar desde "Participar ahora" (carga SOLO esa edición)
	  @GetMapping("/detalle/{id}")
	  public String detallePublico(@PathVariable Integer id, Model model) {
	    Rifa rifa = servicioRifa.obtenerRifaPorId(id);
	    if (rifa == null) return "redirect:/"; // o 404

	    model.addAttribute("modoPublico", true); // <- bandera clave
	    model.addAttribute("rifaSeleccionada", rifa);
	    model.addAttribute("nombreRifaSeleccionada", rifa.getNombre());
	    model.addAttribute("numPorBoleto",
	        rifa.getNumerosPorBoleto() != null ? rifa.getNumerosPorBoleto() : 1);

	    // Tipos de pago SIN “EFECTIVO”
	    List<TipoPago> tipos = serviceTipoPago.getAllTipoPagos()
	        .stream().filter(tp -> !"EFECTIVO".equalsIgnoreCase(tp.getNombre()))
	        .toList();
	    model.addAttribute("tiposPago", tipos);

	    // Reusa el MISMO template admin
	    return "compra_public";
	  }
	  
	  
	
	  
	  
//	  @GetMapping(value = "/detalle/{id}/numeros-simple", produces = MediaType.APPLICATION_JSON_VALUE)
//	  @ResponseBody
//	  public ResponseEntity<Map<String,Object>> numerosSimplePublic(
//	      @PathVariable Integer id,
//	      @RequestParam(defaultValue = "1") int page,
//	      @RequestParam(defaultValue = "120") int size) {
//
//	    // Reusa tu lógica actual (idéntica a admin) y SOLO asegúrate de poner:
//	    // body.put("nombreRifaSeleccionada", rifa.getNombre());
//	    // body.put("imgUrl", imgUrl);
//	    // body.put("numPorBoleto", numPorBoleto);
//	    // ...
//	    Map<String,Object> body = /* tu misma construcción */ new HashMap<>();
//	    return ResponseEntity.ok(body);
//	  }
	  
		@GetMapping(value = "/detalle/{id}/auto-numeros", produces = MediaType.APPLICATION_JSON_VALUE)
		@ResponseBody
		public ResponseEntity<List<String>> autoPick(
		        @PathVariable("id") Long rifaId,
		        @RequestParam(name = "faltan", defaultValue = "1") Integer faltan) {

		    int req = (faltan == null || faltan < 0) ? 0 : faltan;

		    // Si tu servicio espera el conteo validado, usa 'req'
		    List<String> nums = serviceNumero.regresaNumerosRandomBaseDisponibles(rifaId, req);

		    return ResponseEntity.ok(nums);
		}

	  
	  
	  
		@Transactional()
		@GetMapping(value = "/detalle/{id}/numeros-simple", produces = MediaType.APPLICATION_JSON_VALUE)
		@ResponseBody
		public ResponseEntity<Map<String, Object>> numerosSimplePaged(
		        @PathVariable Integer id,
		        @RequestParam(defaultValue = "1") int page,
		        @RequestParam(defaultValue = "120") int size, Model model
		        ) {

			List<String> numeroRifaDisponibles = new ArrayList<String>();
			List<String> numeroRifaPagodos = new ArrayList<String>();
			
		    // 1) Trae listas (ajusta a tu servicio real)
			     List<String> disponibles = new ArrayList<>();
			    List<String> apartados   = new ArrayList<>();
			    List<String> pagados     = new ArrayList<>();
			    Rifa rifa = servicioRifa.obtenerRifaPorId(id);
			    
			    String imgUrl = null;
		        if (rifa.getPathImagen()!=null && !rifa.getPathImagen().isBlank()) {
		            imgUrl = rifa.getPathImagen();
		        } else if (rifa.getPathImagen()!=null && !rifa.getPathImagen().isBlank()) {
		            imgUrl = rifa.getPathImagen();
		        }
			    
			    rifa.getNumeros().forEach(numero -> {
			        // Evitar NPE y comparar Strings correctamente
			        String estado = null;
			        
			        
			        
			        
			        
			        if (numero.getBoleto() != null && numero.getBoleto().getEstadoBoleto() != null) {
			        	
			            estado = numero.getBoleto().getEstadoBoleto().getNombre();
			        }

			        // Regla de negocio típica:
			        // - Sin boleto => DISPONIBLE
			        // - Con boleto y estado = "DISPONIBLE" => DISPONIBLE (por si así lo modelaste)
			        // - "APARTADO" y "LIQUIDADO"/"PAGADO" según tus nombres reales
			        
			        if(!numero.getSeleccionado()) {
			        	  if (numero.getBoleto() == null || "DISPONIBLE".equalsIgnoreCase(estado)) {
			        		  numeroRifaDisponibles.add(numero.getValor());
					            disponibles.add(numero.getValor()); // String ya listo para pintar
			        	  }
			        }
			        
			        if("APARTADO".equalsIgnoreCase(estado)) {
			        	 apartados.add(numero.getValor());
			        }
			        
			        if ("VENDIDO".equalsIgnoreCase(estado) ) {
			        	numeroRifaPagodos.add(numero.getValor());
			            pagados.add(numero.getValor());
			        }
			        
			        
			      
			        
//			        if (numero.getBoleto() == null || "DISPONIBLE".equalsIgnoreCase(estado)) {
//			        	numeroRifaDisponibles.add(numero.getValor());
//			            disponibles.add(numero.getValor()); // String ya listo para pintar
//			        } else if ("APARTADO".equalsIgnoreCase(estado)) {
//			            apartados.add(numero.getValor());
//			        } else if ("VENDIDO".equalsIgnoreCase(estado) ) {
//			        	numeroRifaPagodos.add(numero.getValor());
//			            pagados.add(numero.getValor());
//			        } else {
//			            // Si hay más estados, decide a dónde van o ignóralos
//			        }
			    });

			    // Ordena numéricamente (para Strings “01”, “2”, “003”, etc.)
			    Comparator<String> numCmp = Comparator.comparingInt(s -> {
			        try { return Integer.parseInt(s); } catch (Exception e) { return Integer.MAX_VALUE; }
			    });
			    disponibles.sort(numCmp);
			    
			    
			 // Paginación sobre DISPONIBLES
			    int total = disponibles.size();
			    int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
			    page = Math.max(1, Math.min(page, totalPages));
			    int from = Math.max(0, (page - 1) * size);
			    int to   = Math.min(total, from + size);

			    List<String> sliceDisp = disponibles.subList(from, to); 

			    int digits = disponibles.stream().mapToInt(String::length).max().orElse(2);
			    // - numPorBoleto: si lo tienes en la rifa
			    Integer numPorBoleto = (rifa.getNumerosPorBoleto() != null) ? rifa.getNumerosPorBoleto() : 1;

			    Map<String, Object> body = new HashMap<>();
			    body.put("page", page);
			    body.put("size", size);
			    body.put("total", total);        // total DISPONIBLES (toda la rifa)
			    body.put("totalPages", totalPages);
			    body.put("digits", digits);
			    body.put("numPorBoleto", numPorBoleto);

			    // Datos para la grilla
			    body.put("disp", sliceDisp);     // disponibles de ESTA página

			    // Para la leyenda (elige una de estas dos estrategias):
//			    body.put("ap", ap);              // 1) listas completas (si no son muy grandes)
			    body.put("pag", pagados);

			    body.put("nombreRifaSeleccionada", rifa.getNombre());
			    body.put("imgUrl", imgUrl);

			    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
		}
	  
		@Transactional()
		@GetMapping(value = "/detalle/{rifaId}/limpiar-auto", produces = MediaType.APPLICATION_JSON_VALUE)
		@ResponseBody
		public ResponseEntity<Map<String, Object>> limpiarPick(
	            @PathVariable Integer rifaId,
	            @RequestParam(name = "numeroIds") List<String> numeroIds) {

			 int updated = serviceNumero.limpiarSeleccion(numeroIds, rifaId);
	//
			  return ResponseEntity.ok(Map.of("updated", updated, "ids", rifaId));
		}
//		@PostMapping("/detalle/{id}/limpiar-auto")
//		public ResponseEntity<Void> limpiarAuto(@PathVariable("id") Long rifaId,
//		                                        @RequestBody Map<String,Object> body) {
//		  String token = (String) body.get("token");
//		  @SuppressWarnings("unchecked")
//		  List<String> numeros = (List<String>) body.get("numeros");
//		  // ... liberar por numeros o token ...
//		  return ResponseEntity.ok().build();
//		}

		@Transactional
		@GetMapping(
		  value = "/detalle/{rifaId}/seleccion",
		  produces = MediaType.APPLICATION_JSON_VALUE
		)
		@ResponseBody
		public ResponseEntity<Map<String, Object>> setSeleccion(
		    @PathVariable Integer rifaId,
		    @RequestParam String numero,
		    @RequestParam(defaultValue = "false") boolean seleccionado,
		    @RequestParam(required = false) Integer rifaIdParam // por si lo mandas también en query
		) {

			System.out.print("");
			int updated = serviceNumero.limpiarSeleccionUnico(numero, rifaId);
			//
			return ResponseEntity.ok(Map.of("updated", updated, "ids", rifaId));
//			return ResponseEntity.ok(null);
		}
	  

}
