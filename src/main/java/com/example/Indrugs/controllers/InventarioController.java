package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.InventarioDTO;
import com.example.Indrugs.DTO.InventarioUpdateDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.Font;

import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioUpdateDTO;
import com.example.Indrugs.entities.Inventario;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.InventarioMapper;
import com.example.Indrugs.mapper.UsuarioMapper;
import com.example.Indrugs.services.InventarioService;
import com.example.Indrugs.services.MedicamentosService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping
public class InventarioController {

    private InventarioService inventarioService;
    private MedicamentosService medicamentosService;

    public InventarioController(InventarioService inventarioService, MedicamentosService medicamentosService){
        this.inventarioService = inventarioService;
        this.medicamentosService = medicamentosService;
    }

    @GetMapping("/admin/export/inventario")
    public void exportUsersToPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=inventario.pdf");

        List<InventarioDTO> inventarios = inventarioService.read();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(22,17,153));
        Paragraph title = new Paragraph("Lista de Inventario",titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        Paragraph fecha = new Paragraph("Fecha de generación: " + new Date().toString());
        fecha.setAlignment(Element.ALIGN_RIGHT);
        document.add(fecha);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);

        float[] columnWidths = {0.5f, 1f, 1f, 0.5f, 1f, 1f, 0.5f};
        table.setWidths(columnWidths);
        int rowIndex = 0;
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        addCellToTable(table, "Medicamento", headerFont, true, rowIndex);
        addCellToTable(table, "Entrada", headerFont, true, rowIndex);
        addCellToTable(table, "Stock", headerFont, true, rowIndex);
        addCellToTable(table, "Vencimiento", headerFont, true, rowIndex);
        addCellToTable(table, "Estado", headerFont, true, rowIndex);

        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 7);

        for (InventarioDTO inventario : inventarios) {
            addCellToTable(table, inventario.getNombreMedicamento() != null ? inventario.getNombreMedicamento() : "", dataFont, false, rowIndex);
            addCellToTable(table, inventario.getFechaEntrada() != null ? String.valueOf(inventario.getFechaEntrada())  : "", dataFont, false, rowIndex);
            addCellToTable(table, inventario.getStock() != null ? String.valueOf(inventario.getStock())  : "", dataFont, false, rowIndex);
            addCellToTable(table, inventario.getVencimiento() != null ? String.valueOf(inventario.getVencimiento()) : "", dataFont, false, rowIndex);
            addCellToTable(table, inventario.getEstadoMed() != null ? inventario.getEstadoMed() : "", dataFont, false, rowIndex);
            rowIndex++;
        }

        document.add(table);

        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Total inventario: " + inventarios.size());
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
            cell.setBorderColor(new BaseColor(180, 210, 245)); // bordes azul pastel
        }
        cell.setPadding(5);
        table.addCell(cell);
    }

    @GetMapping("/admin/17.pagina_inventario")
    public String mostrarInventario(@RequestParam(required = false) String estadoMed,
                                    HttpSession session,
                                    Model model,
                                    @RequestParam(defaultValue = "0") int page){
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login"; // si no está logueado
        }
        Page<InventarioDTO> inventarios;
        Pageable pageable = PageRequest.of(page, 8);
        //filtros
        if (estadoMed != null && !estadoMed.isEmpty()){
            inventarios = inventarioService.findByEstado(estadoMed, pageable);
        }else {
            inventarios = inventarioService.readA(pageable);
        }

        model.addAttribute("inventarios", inventarios);
        model.addAttribute("estadoSelecionado", estadoMed);
        //formulario
        model.addAttribute("invetarioDTO", new InventarioDTO());
        model.addAttribute("medicamentos", medicamentosService.readAdmin());
        return "administrador/17.pagina_inventario";
    }

    @GetMapping("/paciente/api/inventario/stock/{idMedicamento}")
    @ResponseBody
    public ResponseEntity<Integer> obtenerStock(@PathVariable Long idMedicamento) {
        try {
            InventarioDTO inventario = inventarioService.buscarPorId(idMedicamento);
            return ResponseEntity.ok(inventario.getStock());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(0); // 0 si no existe
        }
    }


    @GetMapping("/admin/19.pagina_agregar_med")
    public String mostrarFormulario(HttpSession session, Model model){
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login"; // si no está logueado
        }

        model.addAttribute("invetarioDTO", new InventarioDTO());
        model.addAttribute("medicamentos", medicamentosService.readAdmin());
        return "administrador/19.pagina_agregar_med";
    }

    @PostMapping("/admin/procesar_inventario")
    public String agregarInventario(@ModelAttribute InventarioDTO inventarioDTO,
                                    RedirectAttributes redirectAttribute){
        try{
            inventarioDTO.setFechaEntrada(LocalDateTime.now());
            inventarioService.crear(inventarioDTO);
            redirectAttribute.addFlashAttribute("mensaje", "Medicamento agregado exitosamente");
        } catch (Exception e) {
            redirectAttribute.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/17.pagina_inventario";
    }

    @GetMapping("/admin/actualizarInventario")
    public String mostrarFormularioEditar(@RequestParam("idInventario") Long idInventario, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login"; // si no está logueado
        }

        try {
            InventarioDTO inventarioDTO = inventarioService.buscarPorIdInventario(idInventario);

            InventarioUpdateDTO inventarioUpDto = InventarioMapper.toUpdateDTO(inventarioDTO);

            model.addAttribute("inventario", inventarioUpDto);

            model.addAttribute("estados", List.of("ACTIVO", "INACTIVO"));

            return "Layouts/modal_edicion :: modalEditarInventario";

        } catch (Exception e) {
            return "redirect:/admin/17.pagina_inventario?error=Inventario no encontrado";
        }
    }

    @PostMapping("/admin/actualizarInventario")
    public String actualizarInventario(@RequestParam Long idInventario,
                                       InventarioUpdateDTO inventarioUpDto,
                                       RedirectAttributes redirectAttributes) {
        try{
            inventarioService.actualizar(idInventario, inventarioUpDto);
            redirectAttributes.addFlashAttribute("mensaje", "Inventario actualizado correctamente");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/17.pagina_inventario";
    }

    @GetMapping("/admin/export/inventario/excel")
    public void exportInventarioToExcel(HttpServletResponse response) throws IOException {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=inventario.xlsx"
        );

        List<InventarioDTO> inventarios = inventarioService.read();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Inventario");


        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeight(18);
        titleFont.setBold(true);
        titleFont.setColor(new XSSFColor(new java.awt.Color(22,17,153), null));
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(200,225,255), null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        CellStyle evenStyle = workbook.createCellStyle();
        evenStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(225,240,255), null));
        evenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        evenStyle.setBorderBottom(BorderStyle.THIN);
        evenStyle.setBorderTop(BorderStyle.THIN);
        evenStyle.setBorderLeft(BorderStyle.THIN);
        evenStyle.setBorderRight(BorderStyle.THIN);

        CellStyle oddStyle = workbook.createCellStyle();
        oddStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240,248,255), null));
        oddStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        oddStyle.setBorderBottom(BorderStyle.THIN);
        oddStyle.setBorderTop(BorderStyle.THIN);
        oddStyle.setBorderLeft(BorderStyle.THIN);
        oddStyle.setBorderRight(BorderStyle.THIN);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Lista de Inventario");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        Row dateRow = sheet.createRow(1);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Fecha de generación: " + new Date());
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        Row headerRow = sheet.createRow(3);
        String[] headers = {"Medicamento", "Entrada", "Stock", "Vencimiento", "Estado"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 4;

        for (InventarioDTO inventario : inventarios) {

            Row row = sheet.createRow(rowIndex);
            CellStyle rowStyle = (rowIndex % 2 == 0) ? evenStyle : oddStyle;

            Cell c0 = row.createCell(0);
            c0.setCellValue(inventario.getNombreMedicamento() != null ? inventario.getNombreMedicamento() : "");
            c0.setCellStyle(rowStyle);

            Cell c1 = row.createCell(1);
            c1.setCellValue(inventario.getFechaEntrada() != null ? inventario.getFechaEntrada().toString() : "");
            c1.setCellStyle(rowStyle);

            Cell c2 = row.createCell(2);
            c2.setCellValue(inventario.getStock() != null ? inventario.getStock() : 0);
            c2.setCellStyle(rowStyle);

            Cell c3 = row.createCell(3);
            c3.setCellValue(inventario.getVencimiento() != null ? inventario.getVencimiento().toString() : "");
            c3.setCellStyle(rowStyle);

            Cell c4 = row.createCell(4);
            c4.setCellValue(inventario.getEstadoMed() != null ? inventario.getEstadoMed() : "");
            c4.setCellStyle(rowStyle);

            rowIndex++;
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
