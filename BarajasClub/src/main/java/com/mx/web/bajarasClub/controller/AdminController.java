package com.mx.web.bajarasClub.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.mx.web.bajarasClub.service.ServiceEstadoBoleto;
import com.mx.web.bajarasClub.service.ServiceEstadoRifa;
import com.mx.web.bajarasClub.service.ServiceNumero;
import com.mx.web.bajarasClub.service.ServiceTipoPago;
import com.mx.web.bajarasClub.service.ServicioBoletos;
import com.mx.web.bajarasClub.util.NumeroGenerator;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ServiceTipoPago serviceTipoPago;

	@Autowired
	private ServiceEstadoRifa serviceEstadoRifa;

	@Autowired
	private RaffleService servicioRifa;

	@Autowired
	private ServiceNumero serviceNumero;

	@Autowired
	private ServiceEstadoBoleto serviceEstadoBoleto;

	@Autowired
	private ServiceCliente serviceCliente;
	@Autowired
	private ServicioBoletos servicioBoletos;

//	private final RaffleRepository rRepo;
	// private final TicketRepository tRepo;
//	private final CompraService service;

//	public AdminController(RaffleRepository rRepo, TicketRepository tRepo, CompraService service) {
//		this.rRepo = rRepo;
//		this.tRepo = tRepo;
//		this.service = service;
//	}
	
	
	
	@GetMapping("/clientes/check")
	@ResponseBody
	public Map<String, Object> checkCliente(
	    @RequestParam(required = false) String email,
	    @RequestParam(required = false) String telefono) {

	    Map<String, Object> out = new HashMap<>();
	    List<Cliente> encontrados  = serviceCliente.findByEmailOrTelefono(
	        (email != null && !email.isBlank()) ? email.trim() : null,
	        (telefono != null && !telefono.isBlank()) ? telefono.trim() : null
	    );

	    
	    Cliente c = (encontrados != null && !encontrados.isEmpty()) ? encontrados.get(0) : null;
	    out.put("exists", c != null);
	    
	    if (c != null) {
	        out.put("id", c.getIdCliente());
	        out.put("nombre", String.format("%s %s %s",
	            nullToEmpty(c.getNombre()),
	            nullToEmpty(c.getApellido_patrno()),
	            nullToEmpty(c.getApellido_materno())
	        ).replaceAll("\\s+", " ").trim());
	        out.put("email", nullToEmpty(c.getEmail()));
	        out.put("telefono", nullToEmpty(c.getTelefono()));
	    }

	    return out;
	}

	private static String nullToEmpty(String s){ return s == null ? "" : s; }
	
	
	

	@GetMapping(value = "/admin/comprar/{rifaId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> comprarAjax(@PathVariable Long rifaId, @RequestParam String numeroTicket,
			@RequestParam String nombre, @RequestParam String apellidoP,
			@RequestParam(required = false) String apellidoM, @RequestParam String email,
			@RequestParam(required = false) String telefono, @RequestParam(required = false) String municipio,
			@RequestParam(required = false) String estado, @RequestParam(required = false) String codigoPostal,
			@RequestParam Long tipoPagoId) {

		// 1) Validaciones básicas
		if (numeroTicket == null || numeroTicket.isBlank()) {
			return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Sin números seleccionados."));
		}

		// 2) Obtén labels tal cual (con ceros) y, si necesitas, su versión entera
		List<String> labels = Arrays.stream(numeroTicket.split(",")).map(String::trim).filter(s -> !s.isEmpty())
				.toList();

		// Si tu lógica interna requiere números:
		// List<Integer> numeros = labels.stream().map(s ->
		// Integer.valueOf(s)).toList();

		// 3) (Re)valida disponibilidad y realiza el “apartado”
		// rifaService.validarDisponibilidad(rifaId, labels);
		// String folio = compraService.apartar(rifaId, labels, nombre, apellidoP,
		// apellidoM, email, telefono, municipio, estado, codigoPostal, tipoPagoId);

		// 4) Construye la redirección a una confirmación (si la tienes)
		String redirectUrl = "/admin/confirmacion"; // agrega aquí ?folio=...
		return ResponseEntity.ok(Map.of("ok", true, "redirect", redirectUrl));
	}

	private List<String> parseNumerosCsv(String csv) {
		if (csv == null || csv.isBlank())
			return Collections.emptyList();
		return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(String::valueOf).distinct()
				.sorted().toList();
	}

	@PostMapping("/tickets/{ticketId}/estado")
	public String cambiarEstado(@PathVariable UUID ticketId) {// , @RequestParam Ticket.Status estado) {
//		var t = tRepo.findById(ticketId).orElseThrow();
//		t.setStatus(estado);
//		tRepo.save(t);
		return "redirect:/admin/tickets/";// + t.getRaffle().getId();
	}

	private Cliente generaCliente(CompraRequest request) {
		Cliente cliente = new Cliente(request.getNombre(), request.getApellidoP(), request.getApellidoM(),
				request.getMunicipio(), request.getEstado(), request.getCodigoPostal(), request.getEmail(),
				request.getTelefono());
		return cliente;
	}

	@GetMapping("/comprar")
	public String comprar(@RequestParam(name = "rifaId", required = false) Integer id, CompraRequest request,
			Model model, @RequestParam String numeroTicket,
			@RequestParam(name = "cantidadBoletos", required = false, defaultValue = "1") Integer cantidadBoletos,
			RedirectAttributes ra) {
		// Aquí puedes guardar el ticket y comprador en la BD
		List<Map<String, Object>> tickets = new ArrayList<>();
		Rifa rifaSeleccionada = servicioRifa.obtenerRifaPorId(id);
		 Map<String, Object> payload = new HashMap<>();
		
		 if (rifaSeleccionada == null) {
		        ra.addFlashAttribute("error", "La rifa seleccionada no existe.");
		        return "redirect:/admin/rifa";
		    }
		
		
		 int porBoleto = rifaSeleccionada.getNumerosPorBoleto() != null
			        ? rifaSeleccionada.getNumerosPorBoleto() : 1;
			    if (porBoleto <= 0) porBoleto = 1;
			    if (cantidadBoletos == null || cantidadBoletos < 1) cantidadBoletos = 1;

		EstadoBoleto estadoBoleto = serviceEstadoBoleto.regresaEstadoVendido();
		TipoPago tipoPago = serviceTipoPago.regresaTipoPagoId(request.getTipoPagoId());
		
        List<String> numeros = parseNumerosCsv(numeroTicket);
        int totalEsperado = porBoleto * cantidadBoletos;
        
        if (numeros.size() != totalEsperado) {
            ra.addFlashAttribute("error",
                "Debes seleccionar exactamente " + totalEsperado +
                " números (" + porBoleto + " por boleto × " + cantidadBoletos + ").");
            return "redirect:/admin/rifa";
        }
        
        
        Cliente comprador = null;
		List<Numero> numeroSeleccionados = serviceNumero.regresaNumerosSeleccionados(rifaSeleccionada, numeros);
		
		if (numeroSeleccionados.size() != totalEsperado) {
	        ra.addFlashAttribute("error", "Algunos números seleccionados ya no están disponibles.");
	        return "redirect:/admin/rifa";
	    }
		
		 List<List<Numero>> grupos = new ArrayList<>();
		    for (int i = 0; i < totalEsperado; i += porBoleto) {
		        grupos.add(numeroSeleccionados.subList(i, i + porBoleto));
		    }


		Cliente cliente = generaCliente(request);
		Cliente clienteGuardado = serviceCliente.guardaClienteEdicion(cliente);
		
		for (int i = 0; i < grupos.size(); i++) {
	        List<Numero> grupo = new ArrayList<>(grupos.get(i)); // copia por seguridad

	        // Para folio por boleto, usa la misma ventana en la lista de strings
	        List<String> numerosStrDelBoleto = numeros.subList(i * porBoleto, (i + 1) * porBoleto);

	        Boleto boleto = new Boleto();
	        boleto.setRifa(rifaSeleccionada);
	        boleto.setCliente(clienteGuardado);
	        boleto.setEstadoBoleto(estadoBoleto);
	        boleto.setTipoPago(tipoPago);
	        boleto.setNumeros(grupo);
	        boleto.setFechaCompra(LocalDateTime.now());
	        boleto.setFolio(NumeroGenerator.generarFolio(boleto.getFechaCompra(), numerosStrDelBoleto, clienteGuardado.getIdCliente()));

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
		
		BigDecimal total = NumeroGenerator.calcularTotal(BigDecimal.valueOf(rifaSeleccionada.getPrecioBoleto()), cantidadBoletos);

	    // 7) Marcar números como vendidos (todos a la vez)
	    serviceNumero.actualizaNumeros(numeroSeleccionados);

	    // 8) Datos para la vista
	    model.addAttribute("rifaSeleccionada", rifaSeleccionada);
	    payload.put("numeros", numeros);
	    payload.put("comprador", clienteGuardado);
	    payload.put("precioUnitario", rifaSeleccionada.getPrecioBoleto());
	    payload.put("tickets", tickets);
	    payload.put("cantidadBoletos", cantidadBoletos);
	    payload.put("numerosPorBoleto", porBoleto);
	    payload.put("total", total);
	    ra.addFlashAttribute("ticketGroup", payload);
	    ra.addFlashAttribute("mostrarTicket", true);

		return "redirect:/admin/rifa";
	}

	@GetMapping(value = "/rifa/{rifaId}/auto-numeros", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> autoNumeros(@PathVariable("rifaId") Long rifaId,
			@RequestParam(name = "faltan", defaultValue = "1") Integer faltan) {

		int req = (faltan == null || faltan < 0) ? 0 : faltan;
		// Usa una de las dos estrategias:
		List<String> nums = serviceNumero.regresaNumerosRandomBaseDisponibles(rifaId, faltan);
		
//		    ra.addFlashAttribute("ticket", ticket);
//
		return ResponseEntity.ok(nums);
	}

	@ModelAttribute("tiposPago")
	public List<TipoPago> cargarTiposPago() {
		return serviceTipoPago.getAllTipoPagos();
	}

	private List<Rifa> edicionesActivas = new ArrayList();

	@GetMapping("/rifa")
	public String detalleRifa(@RequestParam(name = "id", required = false) Integer id, Model model) {
		List<String> numeroRifaDisponibles = new ArrayList<String>();
		List<String> numeroRifaPagodos = new ArrayList<String>();
		edicionesActivas = new ArrayList();
		if (id != null) {
			Rifa rifaSeleccionada = servicioRifa.obtenerRifaPorId(id);
			rifaSeleccionada.getNumeros().stream().forEach(numero -> {
				if (numero.getBoleto() == null || numero.getBoleto().getEstadoBoleto().getNombre() == "DISPONIBLE") {
					numeroRifaDisponibles.add(numero.getValor());
				}
				
				if(numero.getBoleto() != null) {
					if(numero.getBoleto().getEstadoBoleto().getNombre().equals("VENDIDO")) {
						numeroRifaPagodos.add(numero.getValor());
					}
				}
				
				

			});
			serviceEstadoRifa.getEstadosActivos().stream().forEach(estado -> {
				edicionesActivas = servicioRifa.regresaRifaActivas(estado);

			});

			model.addAttribute("rifasActivas", edicionesActivas);
			rifaSeleccionada.getNumeros();
			model.addAttribute("numPorBoleto", rifaSeleccionada.getNumerosPorBoleto());

			model.addAttribute("digitosNumeros", rifaSeleccionada.getDigitos());
			model.addAttribute("pag", numeroRifaPagodos);
			model.addAttribute("dispNumeros", numeroRifaDisponibles);
			model.addAttribute("rifaSeleccionada", rifaSeleccionada);
			model.addAttribute("r", rifaSeleccionada);
			return "admin/raffle_detail";
		}

		serviceEstadoRifa.getEstadosActivos().stream().forEach(estado -> {
			edicionesActivas = servicioRifa.regresaRifaActivas(estado);
			model.addAttribute("rifasActivas", edicionesActivas);
			edicionesActivas.stream().findFirst().ifPresent(edicion -> {
				edicion.getNumeros().stream().forEach(numero -> {
					if(numero.getBoleto() == null) {
						numeroRifaDisponibles.add(numero.getValor());
					}
					if(numero.getBoleto() != null) {
						if(numero.getBoleto().getEstadoBoleto().getNombre().equals("VENDIDO")) {
							numeroRifaPagodos.add(numero.getValor());
						}
					}
					
					
				});
				model.addAttribute("rifaSeleccionada", edicion);
				model.addAttribute("disp", edicion);
				model.addAttribute("numPorBoleto", edicion.getNumerosPorBoleto());
				model.addAttribute("digitosNumeros", edicion.getDigitos());
				
			
			});
			model.addAttribute("dispNumeros", numeroRifaDisponibles);
			model.addAttribute("pag", numeroRifaPagodos);

		});

//		 

//	var raffle = rRepo.findById(id).orElseThrow();
//	var disponibles = tRepo.findByRaffleIdAndStatusOrderByNumero(id, Ticket.Status.DISPONIBLE);
//	var apartados = tRepo.findByRaffleIdAndStatusOrderByNumero(id, Ticket.Status.APARTADO);
//	var pagados = tRepo.findByRaffleIdAndStatusOrderByNumero(id, Ticket.Status.LIQUIDADO);
//	model.addAttribute("r", raffle); model.addAttribute("disp", disponibles);
//	model.addAttribute("ap", apartados); model.addAttribute("pag", pagados);
		return "admin/raffle_detail";
	}

}
