package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.MedicamentoDTO;
import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioCreateDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioUpdateDTO;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.UsuarioMapper;
import com.example.Indrugs.services.DashboardService;
import com.example.Indrugs.services.EmailService;
import com.example.Indrugs.services.UsuarioService;
import com.itextpdf.text.Font;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminisradorController {

    private final UsuarioService usuarioService;
    private final DashboardService dashboardService;
    private EmailService emailService;

    public AdminisradorController(UsuarioService usuarioService, DashboardService dashboardService, EmailService emailService){
        this.usuarioService = usuarioService;
        this.dashboardService = dashboardService;
        this.emailService = emailService;
    }

    @GetMapping("/export/usuarios_registrados")
    public void exportUsersToPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");

        List<UsuarioDTO> usuarios = usuarioService.readExport();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(34,139,34));
        Paragraph title = new Paragraph("Lista de Usuarios Registrados ",titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        Paragraph fecha = new Paragraph("Fecha de generación: " + new Date().toString());
        fecha.setAlignment(Element.ALIGN_RIGHT);
        document.add(fecha);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        float[] columnWidths = {1f, 2f, 1.5f, 2f, 2.5f, 1.5f, 2.5f, 1f};
        table.setWidths(columnWidths);
        int rowIndex = 0;
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        addCellToTable(table, "Rol", headerFont, true, rowIndex);
        addCellToTable(table, "Nombre", headerFont, true, rowIndex);
        addCellToTable(table, "Tipo documento", headerFont, true, rowIndex);
        addCellToTable(table, "Documento", headerFont, true, rowIndex);
        addCellToTable(table, "Dirección", headerFont, true, rowIndex);
        addCellToTable(table, "Teléfono", headerFont, true, rowIndex);
        addCellToTable(table, "Correo", headerFont, true, rowIndex);
        addCellToTable(table, "Estado", headerFont, true, rowIndex);

        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        for (UsuarioDTO usuario : usuarios) {
            addCellToTable(table, usuario.getNombreRol() != null ? usuario.getNombreRol() : "", dataFont, false, rowIndex);
            addCellToTable(table, usuario.getNombre() != null ? usuario.getNombre() : "", dataFont, false, rowIndex);
            addCellToTable(table, usuario.getTipoDoc() != null ? usuario.getTipoDoc() : "", dataFont, false, rowIndex);
            addCellToTable(table, usuario.getNumDoc() != null ? usuario.getNumDoc() : "", dataFont, false, rowIndex);
            addCellToTable(table, usuario.getDireccion() != null ? usuario.getDireccion() : "", dataFont, false, rowIndex);
            addCellToTable(table, usuario.getTelefono() != null ? usuario.getTelefono() : "", dataFont, false, rowIndex);
            addCellToTable(table, usuario.getCorreo() != null ? usuario.getCorreo() : "", dataFont, false, rowIndex);
            addCellToTable(table, usuario.getEstado() != null ? usuario.getEstado() : "", dataFont, false, rowIndex);
            rowIndex++;
        }

        document.add(table);

        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Total de usuarios: " + usuarios.size());
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    private void addCellToTable(PdfPTable table, String content, Font font, boolean isHeader, int rowIndex) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        if (isHeader) {
            cell.setBackgroundColor(new BaseColor(200, 225, 255)); // azul pastel muy claro para encabezados
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(new BaseColor(180, 210, 245)); // azul grisáceo claro para bordes
        } else {
            if (rowIndex % 2 == 0) {
                cell.setBackgroundColor(new BaseColor(225, 240, 255)); // azul muy claro para filas pares
            } else {
                cell.setBackgroundColor(new BaseColor(240, 248, 255)); // azul casi blanco (tono aireado)
            }
            cell.setBorderColor(new BaseColor(180, 210, 245)); // bordes más claros para datos
        }
        cell.setPadding(5);
        table.addCell(cell);
    }

    @GetMapping("/20.pagina_principal_administrador")
    public String mostrarPaginaAdmin(HttpSession session, Model model){
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login"; // si no está logueado
        }

        //estadistica
        Map<String, Long> estadisticasUsuarios = dashboardService.obtenerResumenUsuarios();
        model.addAttribute("estadisticas", estadisticasUsuarios);
        // resumen
        List<UsuarioDTO> usuariosRecientes = dashboardService.obtenerUsuariosRecientes();
        model.addAttribute("usuariosRecientes", usuariosRecientes);

        model.addAttribute("cantidadInventario", dashboardService.totalUnidadesEnStock());
        
        List<OrdenDTO> ordenesRecientes = dashboardService.ObtenerOrdenesRecientes();
        model.addAttribute("ordenesRecientes", ordenesRecientes);

        Map<String,Object> dashboard = dashboardService.ObtenerResumenOrden();
//        model.addAttribute("ordenesRecientes",dashboard.get("ordenesRecientes"));
        model.addAttribute("cantidadOrdenes",dashboard.get("totalOrdenesActivos"));


        return "administrador/20.pagina_principal_administrador";
    }

    @GetMapping("/21.pagina_usuarios")
    public String gestionUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado,
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login"; // si no está logueado
        }

        Page<UsuarioDTO> usuarios;
        Pageable pageable = PageRequest.of(page, 8);

        // aplicar filtros
        if (rol != null && !rol.isEmpty() && estado != null && !estado.isEmpty()) {
            usuarios = usuarioService.findByRolNombreAndEstado(rol, estado, pageable);
        } else if (rol != null && !rol.isEmpty()) {
            usuarios = usuarioService.findByRolNombre(rol, pageable);
        } else if (estado != null && !estado.isEmpty()) {
            usuarios = usuarioService.findByStatus(estado, pageable);
        } else {
            usuarios = usuarioService.read(pageable); // Todos los usuarios
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("rolSeleccionado", rol);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("usuarioNuevo", new UsuarioCreateDTO());


        return "administrador/21.pagina_usuarios";
    }

    @PostMapping({"/registrarAdmin"})
    public String crearUsuarioAdmin(@Valid @ModelAttribute("usuarioNuevo")
                                    UsuarioCreateDTO userCreate,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()){
            System.out.println("❌ Errores de validación detectados:");
            bindingResult.getAllErrors().forEach(e -> System.out.println(" - " + e.getDefaultMessage()));
            return "administrador/21.pagina_usuarios";
        }

        try {
            if (usuarioService.existsByCorreo(userCreate.getCorreo())) {
                redirectAttributes.addFlashAttribute("error", "El correo ya está registrado");
                return "redirect:/admin/21.pagina_usuarios";
            }
            userCreate.setRol(1L);
            usuarioService.crear(userCreate);
            System.out.println("📌 Después de crear usuario");
            emailService.enviarCorreoRegistro(userCreate.getCorreo(), userCreate.getNombre());
            System.out.println("📌 Después de crear usuario");
            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado exitosamente");
            return "redirect:/admin/21.pagina_usuarios";

        } catch (Exception e) {
            System.out.println("📌 Error al registrar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "redirect:/admin/21.pagina_usuarios";
        }

    }


    @GetMapping("/api/usuarios/buscar")
    @ResponseBody
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios(
            @RequestParam(value = "termino") String termino) {

        try {
            if (termino == null || termino.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<UsuarioDTO> usuarios = usuarioService.buscarPorNombreRolEstado(termino);
            return ResponseEntity.ok(usuarios);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/actualizar")
    public String mostrarFormularioEdicion(@RequestParam("idUsuario") Long idUsuario, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login"; // si no está logueado
        }

        try {
            // Obtener el usuario por ID
            UsuarioDTO usuariodto = usuarioService.findById(idUsuario);

            // Convertir a UsuarioUpdateDTO para el formulario
            UsuarioUpdateDTO usuarioUpdate = UsuarioMapper.toUpdateDTO(usuariodto);

            model.addAttribute("usuario", usuarioUpdate);

            model.addAttribute("estados", List.of("ACTIVO", "INACTIVO"));

            return "Layouts/modal_edicion :: modalEditarUsuario";

        } catch (Exception e) {
            return "redirect:/admin/21.pagina_usuarios?error=Usuario no encontrado";
        }
    }

    @PostMapping("/actualizar")
    public String actualizarUsuario(@RequestParam Long idUsuario,
                                    UsuarioUpdateDTO userUpdate,
                                    RedirectAttributes redirectAttributes) {
        try{
            usuarioService.actualizar(idUsuario, userUpdate);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/21.pagina_usuarios";
    }

    @GetMapping("/export/usuarios_registrados_excel")
    public void exportUsersToExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.xlsx");

        List<UsuarioDTO> usuarios = usuarioService.readExport();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Usuarios");


        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeight(16);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);

        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(200, 225, 255), null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle evenRowStyle = workbook.createCellStyle();
        evenRowStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(225, 240, 255), null));
        evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        evenRowStyle.setBorderBottom(BorderStyle.THIN);
        evenRowStyle.setBorderTop(BorderStyle.THIN);
        evenRowStyle.setBorderLeft(BorderStyle.THIN);
        evenRowStyle.setBorderRight(BorderStyle.THIN);

        CellStyle oddRowStyle = workbook.createCellStyle();
        oddRowStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 248, 255), null));
        oddRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        oddRowStyle.setBorderBottom(BorderStyle.THIN);
        oddRowStyle.setBorderTop(BorderStyle.THIN);
        oddRowStyle.setBorderLeft(BorderStyle.THIN);
        oddRowStyle.setBorderRight(BorderStyle.THIN);


        int rowIndex = 0;

        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Lista de Usuarios Registrados");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7)); // unir columnas A:H

        rowIndex++;

        Row fechaRow = sheet.createRow(rowIndex++);
        Cell fechaCell = fechaRow.createCell(0);
        fechaCell.setCellValue("Fecha de generación: " + new Date());
        sheet.addMergedRegion(new CellRangeAddress(rowIndex-1, rowIndex-1, 0, 7));

        Row header = sheet.createRow(rowIndex++);
        String[] columnas = {"Rol", "Nombre", "Tipo documento", "Documento", "Dirección", "Teléfono", "Correo", "Estado"};

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        int dataIndex = 0;
        for (UsuarioDTO usuario : usuarios) {
            Row row = sheet.createRow(rowIndex++);
            CellStyle style = (dataIndex % 2 == 0) ? evenRowStyle : oddRowStyle;

            row.createCell(0).setCellValue(usuario.getNombreRol());
            row.createCell(1).setCellValue(usuario.getNombre());
            row.createCell(2).setCellValue(usuario.getTipoDoc());
            row.createCell(3).setCellValue(usuario.getNumDoc());
            row.createCell(4).setCellValue(usuario.getDireccion());
            row.createCell(5).setCellValue(usuario.getTelefono());
            row.createCell(6).setCellValue(usuario.getCorreo());
            row.createCell(7).setCellValue(usuario.getEstado());

            for (int i = 0; i < 8; i++) {
                row.getCell(i).setCellStyle(style);
            }

            dataIndex++;
        }

        Row footer = sheet.createRow(rowIndex++);
        Cell footerCell = footer.createCell(0);
        footerCell.setCellValue("Total de usuarios: " + usuarios.size());
        sheet.addMergedRegion(new CellRangeAddress(rowIndex-1, rowIndex-1, 0, 7));

        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}

