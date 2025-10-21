package com.mx.web.bajarasClub.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mx.web.bajarasClub.dto.CompraRequest;
import com.mx.web.bajarasClub.model.Boleto;
import com.mx.web.bajarasClub.model.Cliente;
import com.mx.web.bajarasClub.model.EstadoBoleto;
import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;
import com.mx.web.bajarasClub.model.TipoPago;
import com.mx.web.bajarasClub.service.RaffleService;
import com.mx.web.bajarasClub.service.ServiceCliente;
import com.mx.web.bajarasClub.service.ServiceNumero;
import com.mx.web.bajarasClub.service.ServiceTipoPago;
import com.mx.web.bajarasClub.service.ServicioBoletos;
import com.mx.web.bajarasClub.serviceImpl.ServicioEstadoBoleto;
import com.mx.web.bajarasClub.serviceImpl.WhatsAppServiceImpl;
import com.mx.web.bajarasClub.util.NumeroGenerator;

@Controller
public class PublicParticiparController {

	@Autowired
	private ServicioBoletos servicioBoletos;

	@Autowired
	RaffleService servicioRifa;
	@Autowired
	ServiceNumero serviceNumero;
//	  @Autowired ServicioBoletos servicioBoletos;
	@Autowired
	ServiceCliente serviceCliente;
	@Autowired
	ServiceTipoPago serviceTipoPago;
	@Autowired
	ServicioEstadoBoleto serviceEstadoBoleto;
	@Autowired
	WhatsAppServiceImpl whatsAppService;

	// A) Entrar desde "Participar ahora" (carga SOLO esa edición)
	@GetMapping("/detalle/{id}")
	public String detallePublico(@PathVariable Integer id,
			@RequestParam(value = "compra", required = false) String compra, Model model) {

		Rifa rifa = servicioRifa.obtenerRifaPorId(id);
		if (rifa == null)
			return "redirect:/"; // o 404
		
		 if (compra != null) {
		        model.addAttribute("mostrarTicketFinal", true);
		        model.addAttribute("compraId", compra);
		        
		        model.addAttribute("modoPublico", true); // <- bandera clave
				model.addAttribute("rifaSeleccionada", rifa);
				model.addAttribute("nombreRifaSeleccionada", rifa.getNombre());
				model.addAttribute("numPorBoleto", rifa.getNumerosPorBoleto() != null ? rifa.getNumerosPorBoleto() : 1);

				// Tipos de pago SIN “EFECTIVO”
				List<TipoPago> tipos = serviceTipoPago.getAllTipoPagos().stream()
						.filter(tp -> !"EFECTIVO".equalsIgnoreCase(tp.getNombre())).toList();
				model.addAttribute("tiposPago", tipos);
		        
		        
		        
		        return "compra_public";
		 }

		model.addAttribute("modoPublico", true); // <- bandera clave
		model.addAttribute("rifaSeleccionada", rifa);
		model.addAttribute("nombreRifaSeleccionada", rifa.getNombre());
		model.addAttribute("numPorBoleto", rifa.getNumerosPorBoleto() != null ? rifa.getNumerosPorBoleto() : 1);

		// Tipos de pago SIN “EFECTIVO”
		List<TipoPago> tipos = serviceTipoPago.getAllTipoPagos().stream()
				.filter(tp -> !"EFECTIVO".equalsIgnoreCase(tp.getNombre())).toList();
		model.addAttribute("tiposPago", tipos);

		// Reusa el MISMO template admin
		return "compra_public";
	}

	@GetMapping("/detalle/clientes/check-telefono")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> checkTelefono(@RequestParam String telefono) {
		Map<String, Object> out = new HashMap<>();
		Optional<Cliente> cli = serviceCliente.findByTelefono(normalizaTel(telefono));
		if (cli.isPresent()) {
			Cliente c = cli.get();
			out.put("exists", true);
			out.put("id", c.getIdCliente());
			out.put("nombre", c.getNombre() + "" + c.getApellido_patrno() + " " + c.getApellido_materno());
			out.put("telefono", c.getTelefono());
			out.put("email", c.getEmail());
		} else {
			out.put("exists", false);
		}
		return ResponseEntity.ok(out);
	}

	private String normalizaTel(String t) {
		return t == null ? null : t.replaceAll("[^\\d+]", "");
	}

	@GetMapping(value = "/detalle/{id}/auto-numeros", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<String>> autoPick(@PathVariable("id") Long rifaId,
			@RequestParam(name = "faltan", defaultValue = "1") Integer faltan) {

		int req = (faltan == null || faltan < 0) ? 0 : faltan;

		// Si tu servicio espera el conteo validado, usa 'req'
		List<String> nums = serviceNumero.regresaNumerosRandomBaseDisponibles(rifaId, req);

		return ResponseEntity.ok(nums);
	}

	@PostMapping("/detalle/confirmar")
	public String confirmarCompra(@RequestParam Integer rifaId, @RequestParam(required = false) Integer clienteId,
			@RequestParam Long tipoPagoId, @RequestParam String numeroTicket, // “01, 02, 15…”
			@RequestParam Integer cantidadBoletos, Model model, RedirectAttributes ra, CompraRequest request) {

		List<Map<String, Object>> tickets = new ArrayList<>();
		Rifa rifaSeleccionada = servicioRifa.obtenerRifaPorId(rifaId);
		Map<String, Object> payload = new HashMap<>();

		if (rifaSeleccionada == null) {
			return "redirect:/";
		}

		int porBoleto = rifaSeleccionada.getNumerosPorBoleto() != null ? rifaSeleccionada.getNumerosPorBoleto() : 1;
		if (porBoleto <= 0)
			porBoleto = 1;
		if (cantidadBoletos == null || cantidadBoletos < 1)
			cantidadBoletos = 1;

		EstadoBoleto estadoBoleto = serviceEstadoBoleto.regresaEstadoApartado();
		TipoPago tipoPago = serviceTipoPago.regresaTipoPagoId(request.getTipoPagoId());

		List<String> numeros = NumeroGenerator.parseNumerosCsv(numeroTicket);
		int totalEsperado = porBoleto * cantidadBoletos;
		List<Numero> numeroSeleccionados = serviceNumero.regresaNumerosSeleccionados(rifaSeleccionada, numeros);

		if (numeroSeleccionados.size() != totalEsperado) {
//		        ra.addFlashAttribute("error", "Algunos números seleccionados ya no están disponibles.");
			return "redirect:/";
		}

		Cliente cliente = (clienteId != null) ? serviceCliente.getById(clienteId)
				: serviceCliente.createOrUpdateByTelefono(request.getTelefono(), request);

		
		
		
		List<List<Numero>> grupos = new ArrayList<>();
		for (int i = 0; i < totalEsperado; i += porBoleto) {
			grupos.add(numeroSeleccionados.subList(i, i + porBoleto));
		}

		for (int i = 0; i < grupos.size(); i++) {
			List<Numero> grupo = new ArrayList<>(grupos.get(i)); // copia por seguridad

			// Para folio por boleto, usa la misma ventana en la lista de strings
			List<String> numerosStrDelBoleto = numeros.subList(i * porBoleto, (i + 1) * porBoleto);

			Boleto boleto = new Boleto();
			boleto.setRifa(rifaSeleccionada);
			boleto.setCliente(cliente);
			boleto.setEstadoBoleto(estadoBoleto);
			boleto.setTipoPago(tipoPago);
			boleto.setNumeros(grupo);
			boleto.setFechaCompra(LocalDateTime.now());
			boleto.setFolio(
					NumeroGenerator.generarFolio(boleto.getFechaCompra(), numerosStrDelBoleto, cliente.getIdCliente()));

			// back-reference
			for (Numero n : grupo) {
				n.setBoleto(boleto);
			}

			// Persistir
			servicioBoletos.guardaBoletoClienteAsignado(boleto);

			// Armar objeto de ticket para la vista
			Map<String, Object> t = new HashMap<>();
			t.put("folio", boleto.getFolio());
			t.put("numeros", new ArrayList<>(numerosStrDelBoleto));
			t.put("fechaCompra", boleto.getFechaCompra());
			tickets.add(t);
		}
		BigDecimal total = NumeroGenerator.calcularTotal(BigDecimal.valueOf(rifaSeleccionada.getPrecioBoleto()),
				cantidadBoletos);
		rifaSeleccionada.setIdCompra(rifaId+"rifaId");
		serviceNumero.actualizaNumeros(numeroSeleccionados);

		Map<String, Object> ticketGroup = new HashMap<>();
		ticketGroup.put("rifaId", rifaId);
		ticketGroup.put("nombreRifa", rifaSeleccionada.getNombre());
		ticketGroup.put("cantidadBoletos", cantidadBoletos);
		ticketGroup.put("numerosPorBoleto", porBoleto);
		ticketGroup.put("total", total); // BigDecimal
		ticketGroup.put("comprador",
				Map.of("nombre", cliente.getNombre(), "apellido_patrno", cliente.getApellido_patrno(),
						"apellido_materno", cliente.getApellido_materno(),

						"telefono", cliente.getTelefono()));
		// tickets: [{folio: "ABC123", numeros: ["0003","0123",…]}, …]
		ticketGroup.put("tickets", tickets);

		ticketGroup.put("leyendaPago", """
				Tienes 24 horas para realizar el depósito.
				Mi cuenta BBVA:
				Cuenta CLABE: 012 306 02932601117 4
				Titular: Andrés Molina

				En el asunto/nota del depósito escribe tu nombre.
				Si no recibimos el depósito en 24 horas, tus números se liberarán.
				""");

//			        model.addAttribute("mostrarTicket", true);
//			        model.addAttribute("ticketGroup", ticketGroup);
//			        model.addAttribute("compraId", rifaId); // lo usaremos para enviar SMS por fetch

		ra.addFlashAttribute("mostrarTicketFinal", true);
		ra.addFlashAttribute("ticketGroup", ticketGroup);
		ra.addFlashAttribute("compraId", rifaSeleccionada.getIdCompra());

		return "redirect:/detalle/" + rifaId ;
	}
	
	@PostMapping("/detalle/enviar-whatsapp")
	@ResponseBody
	public ResponseEntity<?> enviarWhatsapp(
	        @RequestParam String telefono,
	        @RequestParam(value = "compra", required = false)String compra) {

	    // TODO: tu lógica para armar/enviar el mensaje (o solo guardar/loggear)
	    // Ejemplo rápido:
	    System.out.println("Enviar WhatsApp a: " + telefono + " (compraId=" + compra + ")");
	    
	    String resumen = "¡Gracias! Tu compra fue registrada. Rifa X, boletos: 3, total $150.00.";
		whatsAppService.enviarConfirmacionCompra(telefono, resumen);
	    
	    
	    return ResponseEntity.ok().build();
	}

//		 @PostMapping("/detalle/enviar-whatsapp")
//			public String confirmarCompra(@RequestParam(name = "telefono") String telefono) {
//			  // ... lógica de persistencia de la compra/boletos ...
//
//			  String resumen = "¡Gracias! Tu compra fue registrada. Rifa X, boletos: 3, total $150.00.";
//			  whatsAppService.enviarConfirmacionCompra(telefono, resumen);
//
//			  return "redirect:/compra/ok";
//			}

	@Transactional()
	@GetMapping(value = "/detalle/{id}/numeros-simple", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> numerosSimplePaged(@PathVariable Integer id,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "120") int size, Model model) {

		List<String> numeroRifaDisponibles = new ArrayList<String>();
		List<String> numeroRifaPagodos = new ArrayList<String>();

		// 1) Trae listas (ajusta a tu servicio real)
		List<String> disponibles = new ArrayList<>();
		List<String> apartados = new ArrayList<>();
		List<String> pagados = new ArrayList<>();
		Rifa rifa = servicioRifa.obtenerRifaPorId(id);

		String imgUrl = null;
		if (rifa.getPathImagen() != null && !rifa.getPathImagen().isBlank()) {
			imgUrl = rifa.getPathImagen();
		} else if (rifa.getPathImagen() != null && !rifa.getPathImagen().isBlank()) {
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

			if (!numero.getSeleccionado()) {
				if (numero.getBoleto() == null || "DISPONIBLE".equalsIgnoreCase(estado)) {
					numeroRifaDisponibles.add(numero.getValor());
					disponibles.add(numero.getValor()); // String ya listo para pintar
				}
			}

			if ("APARTADO".equalsIgnoreCase(estado)) {
				apartados.add(numero.getValor());
			}

			if ("VENDIDO".equalsIgnoreCase(estado)) {
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
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {
				return Integer.MAX_VALUE;
			}
		});
		disponibles.sort(numCmp);

		// Paginación sobre DISPONIBLES
		int total = disponibles.size();
		int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
		page = Math.max(1, Math.min(page, totalPages));
		int from = Math.max(0, (page - 1) * size);
		int to = Math.min(total, from + size);

		List<String> sliceDisp = disponibles.subList(from, to);

		int digits = disponibles.stream().mapToInt(String::length).max().orElse(2);
		// - numPorBoleto: si lo tienes en la rifa
		Integer numPorBoleto = (rifa.getNumerosPorBoleto() != null) ? rifa.getNumerosPorBoleto() : 1;

		Map<String, Object> body = new HashMap<>();
		body.put("page", page);
		body.put("size", size);
		body.put("total", total); // total DISPONIBLES (toda la rifa)
		body.put("totalPages", totalPages);
		body.put("digits", digits);
		body.put("numPorBoleto", numPorBoleto);

		// Datos para la grilla
		body.put("disp", sliceDisp); // disponibles de ESTA página

		// Para la leyenda (elige una de estas dos estrategias):
//			    body.put("ap", ap);              // 1) listas completas (si no son muy grandes)
		body.put("pag", pagados);
		body.put("ap", apartados);

		body.put("nombreRifaSeleccionada", rifa.getNombre());
		body.put("imgUrl", imgUrl);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
	}

	@Transactional()
	@GetMapping(value = "/detalle/{rifaId}/limpiar-auto", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> limpiarPick(@PathVariable Integer rifaId,
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
	@GetMapping(value = "/detalle/{rifaId}/seleccion", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> setSeleccion(@PathVariable Integer rifaId, @RequestParam String numero,
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
