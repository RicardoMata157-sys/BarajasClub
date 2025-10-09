package com.mx.web.bajarasClub.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mx.web.bajarasClub.dto.LimpiarSeleccionRequest;
import com.mx.web.bajarasClub.dto.RaffleForm;
import com.mx.web.bajarasClub.model.EstadoEdicion;
import com.mx.web.bajarasClub.model.Numero;
import com.mx.web.bajarasClub.model.Rifa;
import com.mx.web.bajarasClub.service.RaffleService;
import com.mx.web.bajarasClub.service.ServiceEstadoRifa;
import com.mx.web.bajarasClub.service.ServiceNumero;
import com.mx.web.bajarasClub.service.ServicioBoletos;
import com.mx.web.bajarasClub.util.NumeroGenerator;

@Controller
@RequestMapping("/admin/rifas")
public class AdminRaffleController {

//    private final RaffleRepository raffleRepo;
//    private final TicketRepository ticketRepo;

//	private final Path root;

//	public AdminRaffleController(@Value("${app.upload-dir}") String uploadDir) {
//		this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
//	}

	@Value("${app.upload-dir}")
	private String uploadDir;

	@Autowired
	private ServiceEstadoRifa serviceEstadoRifa;
	private Rifa nuevaRifa;
	@Autowired
	private RaffleService servicioRifa;
	@Autowired
	private ServicioBoletos servicioBoletos;

	@Autowired
	private ServiceNumero serviceNumero;
	
	

//    public AdminRaffleController(RaffleRepository raffleRepo, TicketRepository ticketRepo) {
//        this.raffleRepo = raffleRepo;
////        this.ticketRepo = ticketRepo;
//    }

	@GetMapping("/nueva")
	public String nueva(Model model) {
		model.addAttribute("form", new RaffleForm());
		return "admin/rifas/form";
	}

	@ModelAttribute("estadosRifa")
	public List<EstadoEdicion> estadosRifa() {
		return serviceEstadoRifa.getConsultaEstados();
	}

	// Cambiar estado de un ticket (reusa tu endpoint existente)
	@PostMapping("/tickets/{ticketId}/estado")
	public String cambiarEstado(@PathVariable Long ticketId) {// @RequestParam("estado") Ticket.Status estado) {
//        Ticket t = ticketRepo.findById(ticketId).orElseThrow();
//        t.setStatus(estado);
//        ticketRepo.save(t);
		return "redirect:/admin/rifas/";// + t.getRaffle().getId() + "/editar";
	}

	@PostMapping
	public String crear(@RequestParam String nombre, @RequestParam Integer digitos,
			@RequestParam("maxValor") Integer maxValor, @RequestParam("numerosPorBoleto") Integer numerosPorBoleto,
			@RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
			@RequestParam("precioBoleto") Double precioBoleto,
			@RequestParam(value = "ganadorRifa", required = false) String ganadorRifa,
			@RequestParam("estadoRifa") String estadoRifa,
			@RequestParam(value = "totalVendido", defaultValue = "0") Integer totalVendido,
			@RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

		// Raffle saved = raffleService.crearRifa(form, estadoRifa);

		Rifa pathRifa = new Rifa();
		nuevaRifa = new Rifa();

		// Guardar imagen (si viene)

		if (image != null && !image.isEmpty()) {
			try {
				String original = image.getOriginalFilename();

				String cleaned = java.text.Normalizer.normalize(original, java.text.Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "").replaceAll("[\\s]+", "-").replaceAll("[^-_.A-Za-z0-9]", "")
						.toLowerCase();

				Path target = Paths.get(uploadDir).resolve(cleaned).normalize();
				Files.createDirectories(target.getParent());
				image.transferTo(target);
				
				 String dbKey = cleaned;
				 
				 
				 String publicUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder
				            .fromCurrentContextPath()
				            .path("/uploads/")
				            .path(java.net.URLEncoder.encode(dbKey, java.nio.charset.StandardCharsets.UTF_8))
				            .toUriString();
				 
				 pathRifa.setPathImagen(publicUrl);


			} catch (Exception e) {
				throw new RuntimeException("No se pudo guardar el archivo", e);
			}

		}

		serviceEstadoRifa.getConsultaEstado(estadoRifa).stream().forEach(estado -> {
			nuevaRifa = new Rifa(nombre, digitos, maxValor, numerosPorBoleto, fechaInicio, fechaFin, precioBoleto,
					ganadorRifa, totalVendido, pathRifa.getPathImagen(), null, null, estado);
		});

		servicioRifa.guardaRifaService(nuevaRifa);

		Rifa rifaNueva = servicioRifa.regresaRifaEntidad(nuevaRifa.getNombre(), nuevaRifa.getFechaInicio(),
				nuevaRifa.getEstadoEdicion());

		return "redirect:/admin/rifas/" + rifaNueva.getId() + "/editar";

	}

	@GetMapping(value = "/rifa/{id}/auto-numeros", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<String>> autoPick(
	        @PathVariable Long rifaId,
	        @RequestParam(defaultValue = "1") Integer faltan) {

		int req = (faltan == null || faltan < 0) ? 0 : faltan;
		// Usa una de las dos estrategias:
		List<String> nums = serviceNumero.regresaNumerosRandomBaseDisponibles(rifaId, faltan);
		
//		    ra.addFlashAttribute("ticket", ticket);
//
		return ResponseEntity.ok(nums);
	}
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/editar")
	public String editar(@PathVariable(name = "id", required = false) Integer id, Model model,RedirectAttributes ra) {
		
		
		List<Rifa> edicionesCreadas = servicioRifa.listAll();
		List<Numero> numDisp = new ArrayList<Numero>();
		List<Numero> numVendidos = new ArrayList<Numero>();
		
		if (edicionesCreadas == null || edicionesCreadas.isEmpty()) {
			    ra.addFlashAttribute("info", "No hay rifas. Crea la primera.");
			    return "redirect:/admin/rifas/nueva";
		}
		
		model.addAttribute("rifas", edicionesCreadas);
		edicionesCreadas.stream().findFirst().ifPresent(edicion -> {

			model.addAttribute("r", edicion);
			model.addAttribute("estadoSeleccionado", edicion.getEstadoEdicion());

			edicion.getNumeros().stream().forEach(numerosDisponibles -> {
				if (numerosDisponibles.getBoleto() == null) {
					numDisp.add(numerosDisponibles);
				}
				
				if(numerosDisponibles.getBoleto() != null) {
					if(numerosDisponibles.getBoleto().getEstadoBoleto().getNombre().equals("VENDIDO")) {
						numVendidos.add(numerosDisponibles);
						
					}
				}
				

			
			});
			BigDecimal	montoRecaudado = NumeroGenerator.montoPorNumero(edicion.getPrecioBoleto(), numVendidos);
			model.addAttribute("montoRecaudado", montoRecaudado);
			
			
			
			model.addAttribute("numDisp", numDisp);
			model.addAttribute("numVendidos", numVendidos);
			BigDecimal montoEsperado = NumeroGenerator.calcularMontoEsperado(edicion.getMaxValor(),
					edicion.getNumerosPorBoleto(), BigDecimal.valueOf(edicion.getPrecioBoleto()), false);
			model.addAttribute("montoEsperado", montoEsperado);
			
		});

		return "admin/rifas/editar";
	}

	@GetMapping("/{id}/editar")
	public String editarSeleccionado(@PathVariable(name = "id", required = false) Integer id, Model model) {
		Rifa r = servicioRifa.obtenerRifaPorId(id); // o findById(id).orElseThrow(
		model.addAttribute("r", r);
		model.addAttribute("rifas", servicioRifa.listAll());
		model.addAttribute("estadoSeleccionado", r.getEstadoEdicion());
		model.addAttribute("tickets", r.getBoletos()); // ordena si lo necesitas
		model.addAttribute("estadoBoletos", servicioBoletos.regreseEstadosBoletos());
		// CREAMOS LOS NUMEROS DE LA RIFA

		int sizeNumero = r.getMaxValor();
		int digitosNumero = r.getDigitos();
		int numBoletos = r.getNumerosPorBoleto();
		List<Numero> numVendidos = new ArrayList<Numero>();
		List<Numero> numDisp = new ArrayList<Numero>();
		if (r.getNumeros().isEmpty()) {
			List<String> valorNumeros = NumeroGenerator.generarPorCantidad(digitosNumero, sizeNumero);

			valorNumeros.stream().forEach(valor -> {
				Numero numeros = new Numero(r, null, valor);
				r.addNumero(numeros);
			});

			servicioRifa.actualizaRifaService(r);

		}

		r.getNumeros().stream().forEach(numerosDisponibles -> {
			if (numerosDisponibles.getBoleto() == null) {
				numDisp.add(numerosDisponibles);
			}
			
			if(numerosDisponibles.getBoleto() != null) {
				if(numerosDisponibles.getBoleto().getEstadoBoleto().getNombre().equals("VENDIDO")) {
					numVendidos.add(numerosDisponibles);
					
				}
			}

		});

		model.addAttribute("tickets", r);

		BigDecimal	montoRecaudado = NumeroGenerator.montoPorNumero(r.getPrecioBoleto(), numVendidos);
		model.addAttribute("montoRecaudado", montoRecaudado);
		
		
		
		model.addAttribute("numDisp", numDisp);
		model.addAttribute("numVendidos", numVendidos);
		BigDecimal montoEsperado = NumeroGenerator.calcularMontoEsperado(r.getMaxValor(), r.getNumerosPorBoleto(),
				BigDecimal.valueOf(r.getPrecioBoleto()), false);
		model.addAttribute("montoEsperado", montoEsperado);

		return "admin/rifas/editar";
	}

	@GetMapping("/tickets")
	public String adminTickets() {
//		var r = rRepo.findById(raffleId).orElseThrow();
//		model.addAttribute("r", r);
//		model.addAttribute("tickets", tRepo.findAll());
		return "admin/tickets";
	}

	@PostMapping("/{id}")
	public String actualizarRifa(@PathVariable Integer id, @ModelAttribute RaffleForm form, // campos del form
			@RequestParam(value = "image", required = false) MultipartFile image, Model model) throws IOException {
//        servicioRifa.actualizarRifa(id, form, image);
		Rifa rifaActualizada = servicioRifa.obtenerRifaPorId(id);
		rifaActualizada.setNombre(form.getNombre());
		List<Numero> numDisp = new ArrayList<Numero>();
		rifaActualizada.setEstadoEdicion(cosultaEdicionActualizada(form.estadoEdicion));

		rifaActualizada.setDigitos(form.getDigitos());

		rifaActualizada.setNumerosPorBoleto(form.getNumerosPorBoleto());
		rifaActualizada.setPrecioBoleto(form.precioBoleto);
		rifaActualizada.setFechaFin(form.getFechaFin());
		rifaActualizada.setFechaInicio(form.getFechaInicio());
		rifaActualizada.setGanadorRifa(form.getGanadorRifa());

		if (rifaActualizada.getMaxValor() != form.getMaxValor()) {
			rifaActualizada.setMaxValor(form.getMaxValor());
			syncNumerosParaRifa(rifaActualizada);

		}

		if (image != null && !image.isEmpty()) {
			try {
				String original = image.getOriginalFilename();

				String cleaned = java.text.Normalizer.normalize(original, java.text.Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "").replaceAll("[\\s]+", "-").replaceAll("[^-_.A-Za-z0-9]", "")
						.toLowerCase();

				Path target = Paths.get(uploadDir).resolve(cleaned).normalize();
				Files.createDirectories(target.getParent());
				image.transferTo(target);
				
				 String dbKey = cleaned;
				 
				 
				 String publicUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder
				            .fromCurrentContextPath()
				            .path("/uploads/")
				            .path(java.net.URLEncoder.encode(dbKey, java.nio.charset.StandardCharsets.UTF_8))
				            .toUriString();
				 
				 rifaActualizada.setPathImagen(publicUrl);


			} catch (Exception e) {
				throw new RuntimeException("No se pudo guardar el archivo", e);
			}

		}

		servicioRifa.actualizaRifaService(rifaActualizada);

		rifaActualizada.getNumeros().stream().forEach(numerosDisponibles -> {
			if (numerosDisponibles.getBoleto() == null) {
				numDisp.add(numerosDisponibles);
			}

		});

		model.addAttribute("numDisp", numDisp);
		BigDecimal montoEsperado = NumeroGenerator.calcularMontoEsperado(rifaActualizada.getMaxValor(),
				rifaActualizada.getNumerosPorBoleto(), BigDecimal.valueOf(rifaActualizada.getPrecioBoleto()), false);
		model.addAttribute("montoEsperado", montoEsperado);

		model.addAttribute("r", rifaActualizada);
		model.addAttribute("rifas", servicioRifa.listAll());
		model.addAttribute("estadoSeleccionado", rifaActualizada.getEstadoEdicion());
		model.addAttribute("tickets", rifaActualizada.getBoletos()); // ordena si lo necesitas
		model.addAttribute("estadoBoletos", servicioBoletos.regreseEstadosBoletos());

		return "redirect:/admin/rifas/" + id + "/editar";
	}

	public void syncNumerosParaRifa(Rifa rifa) {
		final int digitos = rifa.getDigitos();
		final int max = rifa.getMaxValor();

		// Ajusta estas dos líneas según tu regla de negocio:
		final boolean inclusive = true; // true => 0..max ; false => 0..max-1
		final int start = 1; // usa 1 si quieres 1..max

		// 1) Conjunto "deseado"
		final int hasta = inclusive ? max : (max - 1);
		if (hasta < start)
			return; // nada que hacer si rango inválido

		Set<String> deseados = new LinkedHashSet<>();
		String fmt = "%0" + digitos + "d";
		for (int i = start; i <= hasta; i++) {
			deseados.add(String.format(fmt, i));
		}

		// 2) Conjunto "actual" desde BD
		List<Numero> actuales = consultaNumerosActualizaLista(rifa);
		Set<String> actualesSet = actuales.stream().map(Numero::getValor)
				.collect(Collectors.toCollection(LinkedHashSet::new));

		// 3) Diferencias
		Set<String> porCrear = new LinkedHashSet<>(deseados);
		porCrear.removeAll(actualesSet);

		Set<String> porEliminarCandidatos = new LinkedHashSet<>(actualesSet);
		porEliminarCandidatos.removeAll(deseados);

		// 4) Crear faltantes
		if (!porCrear.isEmpty()) {
			List<Numero> nuevos = porCrear.stream().map(code -> {
				Numero n = new Numero();
				n.setRifa(rifa);
				n.setValor(code);
//                n.setEstadoEdicion(EstadoBoleto.DISPONIBLE);
				return n;
			}).toList();

			serviceNumero.actualizaNumeros(nuevos);
		}

		// 5) Eliminar sobrantes SOLO si están DISPONIBLE
		if (!porEliminarCandidatos.isEmpty()) {
			// Mantén APARTADO/VENDIDO
			List<String> borrables = actuales.stream().filter(n -> porEliminarCandidatos.contains(n.getValor()))
					.map(Numero::getValor).toList();

			if (!borrables.isEmpty()) {
				serviceNumero.deleteByRifaIdAndNumero(rifa, borrables);
			}
			// Si te interesa saber cuáles NO se pudieron borrar porque no estaban
			// DISPONIBLE,
			// puedes registrarlos en logs o devolver un resultado.
		}
	}

	private List<Numero> consultaNumerosActualizaLista(Rifa data) {

		List<Numero> numerosActuales = serviceNumero.regresaNumerosRifaActializada(data);

		return numerosActuales;
	}

	private EstadoEdicion cosultaEdicionActualizada(String idEstado) {
		return serviceEstadoRifa.consultaEdicionEstado(idEstado);
	}

	

}