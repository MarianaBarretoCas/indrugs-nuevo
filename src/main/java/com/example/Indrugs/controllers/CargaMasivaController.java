package com.example.Indrugs.controllers;

import ch.qos.logback.core.model.Model;
import com.example.Indrugs.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/api/importar")
public class CargaMasivaController {

    private final UsuarioService usuarioService;

    public CargaMasivaController (UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/usuarios")
    public String importarUsuarios(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.importarExcel(file);
            redirectAttributes.addFlashAttribute("mensaje", "Importación realizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/21.pagina_usuarios";
    }


}
