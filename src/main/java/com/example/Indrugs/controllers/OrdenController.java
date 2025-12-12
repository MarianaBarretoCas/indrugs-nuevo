package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.*;
import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.entities.Orden;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.ArchivosService;
import com.example.Indrugs.services.DomicilioService;
import com.example.Indrugs.services.MedicamentosService;
import com.example.Indrugs.services.OrdenService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private DomicilioService domicilioService;

    @GetMapping("/domi/14.pagina_ordenes")
    public String verOrdenesDirecto(Model model, HttpSession session, @RequestParam(defaultValue = "0") int page) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, 8);
        model.addAttribute("ordenes", ordenService.listarOrdenesP(usuario.getIdUsuario(), pageable));
        return "domiciliario/14.pagina_ordenes";
    }

    @GetMapping("/paciente/3.pagina_de_ordenes")
    public String verOrdenesPaciente(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("ordenes", ordenService.listarOrdenesPa(usuario.getIdUsuario()));
        return "pacientes/3.pagina_de_ordenes";
    }

    @GetMapping("/VerDetalle")
    public String verDetalle(@RequestParam Long idOrden, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        OrdenDTO orden = ordenService.listarDetalle(idOrden);
        model.addAttribute("orden", orden);
        String rol = usuario.getRol().getNombreRol();
        return switch (rol) {
            case "Administrador" -> "administrador/VerDetalle";
            case "Paciente" -> "pacientes/VerDetalleP";
            case "Domiciliario" -> "domiciliario/VerDetalleD";
            default -> "redirect:/login";
        };
    }

    @GetMapping("/paciente/orden/{idOrden}/domiciliario")
    @ResponseBody
    public ResponseEntity<VehiculoDTO> obtenerDomiciliario(@PathVariable Long idOrden) {
        try {
            VehiculoDTO dto = domicilioService.obtenerDomiciliarioPorId(idOrden);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/paciente/16.pagina_carrito_med")
    public String mostrarCarrito(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        return "pacientes/16.pagina_carrito_med";
    }

    @GetMapping("/admin/18.pagina_orden_admin")
    public String verOrdenesAdmin(@RequestParam(required = false) String estadoOrden,
                                  Model model,
                                  HttpSession session,
                                  @RequestParam(defaultValue = "0") int page) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        Page<OrdenDTO> ordenes;
        Pageable pageable = PageRequest.of(page, 8);

        if (estadoOrden != null && !estadoOrden.isEmpty()){
            ordenes = ordenService.findByEstaOrden(estadoOrden, pageable);
        }else {
            ordenes = ordenService.listarOrdenesPage(pageable);
        }

        model.addAttribute("ordenes", ordenes);
        model.addAttribute("estadoSeleccionado", estadoOrden);

        return "administrador/18.pagina_orden_admin";
    }

    @GetMapping("/paciente/4.pagina_domicilio")
    public String mostrarformulario(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuarioLogueado", usuario);
        model.addAttribute("orden", new OrdenDTO());
        return "pacientes/4.pagina_domicilio";
    }

    @PostMapping("/paciente/guardar")
    public String guardarOrden(@ModelAttribute OrdenDTO ordenDTO,
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
            return "redirect:/paciente/16.pagina_carrito_med";

        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar la orden: " + e.getMessage());
            model.addAttribute("orden", ordenDTO);
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            return "pacientes/4.pagina_domicilio";
        }
    }

    @GetMapping("/admin/ordenes/eliminar/{idOrden}")
    public String entregarOrdenDesdeAdmin(@PathVariable Long idOrden) {
        ordenService.eliminar(idOrden);
        return "redirect:/admin/18.pagina_orden_admin";
    }

    @GetMapping("/admin/ordenes/reporte/excel")
    public void descargarReporteExcel(
            @RequestParam(required = false) String estadoOrden,
            HttpServletResponse response,
            HttpSession session) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuario == null) {
                response.sendRedirect("/login");
                return;
            }

            List<OrdenDTO> ordenes;

            if (estadoOrden != null && !estadoOrden.isEmpty() && !estadoOrden.equalsIgnoreCase("Todos")) {
                ordenes = ordenService.findByEstadoOrden(estadoOrden);
            } else {
                ordenes = ordenService.listarOrdenes();
            }

            if (ordenes == null || ordenes.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NO_CONTENT, "No hay órdenes para exportar");
                return;
            }

            byte[] excelBytes = ordenService.generarReporteExcel(ordenes);

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "Reporte_Ordenes_" + timestamp + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentLength(excelBytes.length);

            OutputStream out = response.getOutputStream();
            out.write(excelBytes);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}