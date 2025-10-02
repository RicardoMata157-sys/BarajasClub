package com.mx.web.bajarasClub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mx.web.bajarasClub.dto.CompraRequest;
import com.mx.web.bajarasClub.model.Cliente;
import com.mx.web.bajarasClub.service.RaffleService;
import com.mx.web.bajarasClub.service.ServiceCliente;

@Controller
public class HomeController {
    @Autowired
    private final RaffleService raffleService;
    
    @Autowired
	private ServiceCliente serviceCliente;

    public HomeController(RaffleService raffleService) {
        this.raffleService = raffleService;
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("raffles", raffleService.listAll());
        return "index";
    }
    
    
    // Página de login personalizada (GET)
    @GetMapping("/login")
    public String login(Model model) {
        return "auth/login"; // templates/auth/login.html
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
