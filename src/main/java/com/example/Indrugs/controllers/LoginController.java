package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController (UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @GetMapping({"/login"})
    public String mostrarLogin(){
        return "2.pagina_inicio_sesion";
    }

    @PostMapping({"/login"})
    public String procesoLogin(@RequestParam String correo,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes,
                               HttpSession session){

        try{
            Usuario usuario = usuarioService.autenticar(correo, password);
            session.setAttribute("usuarioLogueado", usuario);

            SimpleGrantedAuthority autoridad =
                    new SimpleGrantedAuthority(usuario.getRol().getNombreRol());
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(usuario, null, List.of(autoridad));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            //aceptacion
            if (usuario.getRol().getNombreRol().equals("Administrador")){
                return "redirect:/admin/20.pagina_principal_administrador";
            } else if (usuario.getRol().getNombreRol().equals("Paciente")) {
                return "redirect:/paciente/1.pagina_principal_paciente";
            } else if (usuario.getRol().getNombreRol().equals("Domiciliario")) {
                return "redirect:/domi/11.pagina_principal_domiciliario";
            }
            redirectAttributes.addFlashAttribute("error", "Rol no encontrado");
            return "redirect:/login";
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }
    @GetMapping("/cerrarSesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

}
