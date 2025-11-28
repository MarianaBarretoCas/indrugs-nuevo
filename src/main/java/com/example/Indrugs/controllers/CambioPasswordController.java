package com.example.Indrugs.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.Indrugs.DTO.Usuario.RequestChangePassword;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioUpdateDTO;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.UsuarioMapper;
import com.example.Indrugs.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller
@RequestMapping
public class CambioPasswordController {

    private UsuarioService usuarioService;

    public CambioPasswordController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cambiar-password")
    public String actualizarUsuario(@ModelAttribute RequestChangePassword requestChangePassword,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        try{
            usuarioService.cambiarPassword(usuario.getIdUsuario(), requestChangePassword);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada correctamente");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/actualizar_perfil";
    }

}
