package com.mx.web.bajarasClub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.mx.web.bajarasClub.dto.CompraRequest;
import com.mx.web.bajarasClub.service.RaffleService;

@Controller
public class HomeController {
    @Autowired
    private final RaffleService raffleService;

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
    
    
    @PostMapping("/comprar")
    public String comprar(CompraRequest request, Model model) {
        // Aquí puedes guardar el ticket y comprador en la BD
        System.out.println("Número de ticket: " + request.getNumeroTicket());
        System.out.println("Nombre: " + request.getNombre() + " " + request.getApellidoP());

        // TODO: Lógica para marcar el boleto como apartado

        // Devolver un mensaje de confirmación a la vista
        model.addAttribute("mensaje", "Tu boleto ha sido apartado con éxito.");
        return "confirmacion"; // plantilla confirmacion.html
    }
    
    
    
    
}
