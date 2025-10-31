package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.*;
import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.entities.Orden;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.ArchivosService;
import com.example.Indrugs.services.MedicamentosService;
import com.example.Indrugs.services.OrdenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrdenController {

    @Autowired
    private OrdenService ordenService;
    @Autowired
    private MedicamentosService medicamentosService;
    @Autowired
    private ArchivosService archivosService;

    @GetMapping("/14.pagina_ordenes")
    public String verOrdenesDirecto(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login"; // Si no está logueado
        }

        model.addAttribute("ordenes", ordenService.listarOrdenes());
        return "domiciliario/14.pagina_ordenes";
    }
    @GetMapping("/VerDetalle")
    public String verDetalle(@RequestParam Long idOrden, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login"; // Si no está logueado
        }
        OrdenDTO orden = ordenService.listarDetalle(idOrden);
        model.addAttribute("orden", orden);
        return "administrador/VerDetalle";
    }
    @GetMapping("/VerDetalleD")
    public String verDetalleD(@RequestParam Long idOrden, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login"; // Si no está logueado
        }
        OrdenDTO orden = ordenService.listarDetalle(idOrden);
        model.addAttribute("orden", orden);
        return "domiciliario/VerDetalleD";
    }
    @GetMapping("/16.pagina_carrito_med")
    public String mostrarCarrito(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login"; // Si no está logueado
        }

        model.addAttribute("ordenes", ordenService.listarOrdenesP(usuario.getIdUsuario()));
        return "pacientes/16.pagina_carrito_med";
    }

    // Vista del administrador
    @GetMapping("/18.pagina_orden_admin")
    public String verOrdenesAdmin(@RequestParam(required = false) String estadoOrden,Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login"; // Si no está logueado
        }

        //filtro
        List<OrdenDTO> ordenes;

        //filtros
        if (estadoOrden != null && !estadoOrden.isEmpty()){
            ordenes = ordenService.findByEstadoOrden(estadoOrden);
        }else {
            ordenes = ordenService.listarOrdenes();
        }

        model.addAttribute("ordenes", ordenes);
        model.addAttribute("estadoSeleccionado", estadoOrden);

        return "administrador/18.pagina_orden_admin";
    }

    @GetMapping("/4.pagina_domicilio")
    public String mostrarformulario(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuarioLogueado", usuario);
        model.addAttribute("orden", new OrdenDTO());
        return "pacientes/4.pagina_domicilio";
    }


    @PostMapping("/guardar")
    public String guardarOrden(@ModelAttribute OrdenDTO ordenDTO,
//                               @RequestParam("formulaFile") MultipartFile formulaFile,
                  //             @RequestParam("idMedicamento") Long idMedicamento,
                               HttpSession session, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
                if (usuario == null) {
                    return "redirect:/login";
                }
            if (ordenDTO.getMedicamentos() == null || ordenDTO.getMedicamentos().isEmpty()) {
                model.addAttribute("error", "No hay medicamentos en la orden");
                model.addAttribute("orden", new OrdenDTO());
                model.addAttribute("usuarioLogueado", usuario);
                return "pacientes/4.pagina_domicilio";
            }
            ordenDTO.setPaciente(usuario.getIdUsuario());
            ordenDTO.setPacienteNombre(usuario.getNombre());
            ordenDTO.setEstadoOrden("ACTIVO");
                if (ordenDTO.getFechaEntrega() == null) {
                    ordenDTO.setFechaEntrega(LocalDateTime.now().plusDays(1));
                }

            List<OrdenMedicamentoDTO> meds = new ArrayList<>();
            for (OrdenMedicamentoDTO item : ordenDTO.getMedicamentos()) {
                MedicamentoDTO medicamento = medicamentosService.buscarPorIdMedicamento(item.getIdMedicamento());
                if (medicamento != null) {
                    OrdenMedicamentoDTO dtoMed = new OrdenMedicamentoDTO();
                    dtoMed.setIdMedicamento(item.getIdMedicamento());
                    dtoMed.setCantidad(item.getCantidad());
                    dtoMed.setNombreMedicamento(medicamento.getNombreMedicamento());
                    meds.add(dtoMed);
                }
            }
            ordenDTO.setMedicamentos(meds);
            ordenService.crear(ordenDTO, usuario.getIdUsuario());
            redirectAttributes.addFlashAttribute("mensaje", "Orden creada exitosamente");
            return "redirect:/16.pagina_carrito_med";

        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar la orden: " + e.getMessage());
            model.addAttribute("orden", ordenDTO);
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            return "pacientes/4.pagina_domicilio";
        }
    }

    // Marcar orden como entregada desde domiciliario
//    @GetMapping("/domiciliario/ordenes/entregar/{id}")
//    public String entregarOrdenDesdeDomiciliario(@PathVariable Long id) {
//        ordenService.marcarComoEntregada(id);
//        return "redirect:/domiciliario/14.pagina_ordenes";
//    }

    // Marcar orden como entregada desde administrador
    @GetMapping("/ordenes/eliminar/{idOrden}")
    public String entregarOrdenDesdeAdmin(@PathVariable Long idOrden) {
        ordenService.eliminar(idOrden);
        return "redirect:/18.pagina_orden_admin";
    }
}