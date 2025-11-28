package com.example.Indrugs.controllers;

import com.example.Indrugs.services.EmailService;
import com.example.Indrugs.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class CorreosController {

    private final EmailService emailService;
    private final UsuarioService usuarioService;

    public CorreosController(EmailService emailService, UsuarioService usuarioService) {
        this.emailService = emailService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/enviar/masivoVista")
    public String mostrarFormularioEnvioMasivo(Model model) {
        model.addAttribute("correos", usuarioService.readExport());
        return "administrador/CorreosMasivos";
    }

    @PostMapping("/enviar/masivo")
    public String enviarMasivo(@RequestParam("asunto") String asunto,
                               @RequestParam("mensaje") String mensaje,
                               @RequestParam("correos") List<String> correos,
                               Model model) {

        emailService.enviarMasivoConBcc(correos, asunto, mensaje);
        model.addAttribute("resultado", "Correos enviados exitosamente");

        return "redirect:/admin/enviar/masivoVista"; // tu vista HTML
    }
}
