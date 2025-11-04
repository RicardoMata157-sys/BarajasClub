package com.mx.web.bajarasClub.controller;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mx.web.bajarasClub.model.Boleto;
import com.mx.web.bajarasClub.model.Rifa;
import com.mx.web.bajarasClub.service.RaffleService;
import com.mx.web.bajarasClub.service.ServiceCliente;
import com.mx.web.bajarasClub.service.ServicioBoletos;
import com.mx.web.bajarasClub.util.NameParser;
import com.mx.web.bajarasClub.util.NumeroGenerator;

@Controller
public class HomeController {
    @Autowired
    private final RaffleService raffleService;
    
    @Autowired
	private ServiceCliente serviceCliente;
    
    
    @Autowired
    private ServicioBoletos servicioBoletos;

    public HomeController(RaffleService raffleService) {
        this.raffleService = raffleService;
    }

//    @GetMapping({"/", "/index"})
//    public String index(Model model) {
//        model.addAttribute("raffles", raffleService.listAll());
//        return "index";
//    }
    
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        List<Rifa> rifas = raffleService.listAll();

        var vm = rifas.stream().map(r -> {
            // Total de números en la rifa
            int total = (r.getNumeros() == null) ? 0 : r.getNumeros().size();

            // Vendidos (o pagados)
            int vendidos = (r.getNumeros() == null) ? 0 :
                (int) r.getNumeros().stream()
                    .filter(n -> {
                        var b = n.getBoleto();
                        if (b == null) return false;
                        var e = b.getEstadoBoleto();
                        String nombre = (e != null && e.getNombre() != null) ? e.getNombre().trim() : "";
                        return "VENDIDO".equalsIgnoreCase(nombre) || "PAGADO".equalsIgnoreCase(nombre);
                    })
                    .count();

            // Apartados
            int apartados = (r.getNumeros() == null) ? 0 :
                (int) r.getNumeros().stream()
                    .filter(n -> {
                        var b = n.getBoleto();
                        if (b == null) return false;
                        var e = b.getEstadoBoleto();
                        String nombre = (e != null && e.getNombre() != null) ? e.getNombre().trim() : "";
                        return "APARTADO".equalsIgnoreCase(nombre);
                    })
                    .count();

            // Disponibles = sin boleto asociado
            int disponibles = (r.getNumeros() == null) ? 0 :
                (int) r.getNumeros().stream()
                    .filter(n -> n.getBoleto() == null)
                    .count();

            // Porcentaje de progreso (solo vendidos/pagados)
            int percent = (total > 0) ? (int) Math.round(vendidos * 100.0 / total) : 0;

            Map<String, Object> m = new HashMap<>();
            m.put("entity", r);
            m.put("progressPercent", percent);
            m.put("vendidos", vendidos);
            m.put("apartados", apartados);
            m.put("disponibles", disponibles);
            m.put("total", total);
            return m;
        }).toList();

        model.addAttribute("raffles", vm);
        return "index";
    }


    
    
    // Página de login personalizada (GET)
    @GetMapping("/login")
    public String login(Model model) {
        return "auth/login"; // templates/auth/login.html
    }
    
    
    
    
    @GetMapping("/consulta_boleto")
    public String consultaBoleto(Model model) {
        return "consulta_tickets"; // templates/auth/login.html
    }
    
  
    
    @GetMapping("/consulta")
    public String consulta(@RequestParam(value = "q", required = false) String q,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "20") int size,
                           Model model) {
    	
    	Pageable pageable = PageRequest.of(Math.max(0, page),
                Math.min(Math.max(size,1), 100),
                Sort.by(Sort.Direction.DESC, "fechaCompra", "id"));
//    	 NameParser.NameParts p = null;
    	List<Boleto> resultados = Collections.emptyList();
    	
    	 QueryType type = detectType(q);
    	 
    	 NameParser.NameParts	 p = NameParser.parse(q, true); // true = quita acentos
    		 
    	 if (type != QueryType.VACIA) {
    		 switch (type) {
    		 case TELEFONO -> resultados = servicioBoletos.matchTelefono(NumeroGenerator.normalizaTelefono(q));
    		 case NOMBRE -> resultados = servicioBoletos.matchNombre(p.nombres(),p.apPat(), p.apMat());
    		 case FOLIO -> resultados = servicioBoletos.matchFolio(q);
    		 }
    		 
    	 }
    	   model.addAttribute("tickets", resultados);
           model.addAttribute("fmtFecha",
                   DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withLocale(new Locale("es","MX")));

    	
    	 return "consulta_tickets";
    }
    
    
    enum QueryType { TELEFONO, FOLIO, NOMBRE, NUMERO_PARTICIPANTE, VACIA, DESCONOCIDA }
    private QueryType detectType(String q) {
        if (q == null || q.isBlank()) return QueryType.VACIA;
        String s = q.trim();
        String digits = s.replaceAll("\\D", "");

        boolean hasLetters = s.matches(".*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ].*");
        boolean onlyDigits = s.matches("\\d+");
        boolean hasHyphenOrUnderscore = s.contains("-") || s.contains("_");

        // Teléfono: >=10 dígitos y no contiene letras
        if (!hasLetters && digits.length() >= 10) return QueryType.TELEFONO;

        // Folio: mezcla letras y números, o patrón típico con guion (FOL-000123, TKT_202501, etc.)
        if (hasLetters && (s.matches(".*\\d.*") || hasHyphenOrUnderscore)) return QueryType.FOLIO;

        // Nombre: 2+ palabras con letras (Ana Pérez, Juan P García)
        if (hasLetters && s.trim().split("\\s+").length >= 2) return QueryType.NOMBRE;

        // Número participante: solo dígitos y menos de 10 (para no chocar con teléfono)
        if (onlyDigits && digits.length() > 0 && digits.length() < 10) return QueryType.NUMERO_PARTICIPANTE;

        // Desconocida: fallback (hará OR en todo)
        return QueryType.DESCONOCIDA;
    }
    
    
//    @PostMapping("/comprar")
//    public String comprar(CompraRequest request, Model model) {
//        // Aquí puedes guardar el ticket y comprador en la BD
//        System.out.println("Número de ticket: " + request.getNumeroTicket());
//        System.out.println("Nombre: " + request.getNombre() + " " + request.getApellidoP());
//
//        // TODO: Lógica para marcar el boleto como apartado
//
//        // Devolver un mensaje de confirmación a la vista
//        model.addAttribute("mensaje", "Tu boleto ha sido apartado con éxito.");
//        return "confirmacion"; // plantilla confirmacion.html
//    }
    
//    @GetMapping("/admin/clientes/check")
//	@ResponseBody
//	public Map<String, Object> checkCliente(
//	    @RequestParam(required = false) String email,
//	    @RequestParam(required = false) String telefono) {
//
//	    Map<String, Object> out = new HashMap<>();
//	    List<Cliente> encontrados  = serviceCliente.findByEmailOrTelefono(
//	        (email != null && !email.isBlank()) ? email.trim() : null,
//	        (telefono != null && !telefono.isBlank()) ? telefono.trim() : null
//	    );
//
//	    
//	    Cliente c = (encontrados != null && !encontrados.isEmpty()) ? encontrados.get(0) : null;
//	    out.put("exists", c != null);
//	    
//	    if (c != null) {
//	        out.put("id", c.getIdCliente());
//	        out.put("nombre", String.format("%s %s %s",
//	            nullToEmpty(c.getNombre()),
//	            nullToEmpty(c.getApellido_patrno()),
//	            nullToEmpty(c.getApellido_materno())
//	        ).replaceAll("\\s+", " ").trim());
//	        out.put("email", nullToEmpty(c.getEmail()));
//	        out.put("telefono", nullToEmpty(c.getTelefono()));
//	    }
//
//	    return out;
//	}
//
//	private static String nullToEmpty(String s){ return s == null ? "" : s; }
//    
    
}
