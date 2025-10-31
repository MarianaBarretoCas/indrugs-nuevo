package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.services.DashboardService;
import com.example.Indrugs.services.DomicilioService;
import com.example.Indrugs.services.InventarioService;
import com.example.Indrugs.services.OrdenService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private DomicilioService domicilioService;
    private DashboardService dashboardService;

    public DashboardController(DomicilioService domicilioService, DashboardService dashboardService) {
        this.domicilioService = domicilioService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/medicamentosTop3")
    public ResponseEntity<List<Map<String, Object>>> getMedicamentosTop3() {
        List<Object[]> resultados = dashboardService.top3MedicamentosMasEnviados();
        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Object[] fila : resultados) {
            Map<String, Object> medicamentoData = new HashMap<>();
            String nombreMedicamento = (String) fila[0];
            medicamentoData.put("medicamento", nombreMedicamento);
            Long totalEnviados = (Long) fila[1];
            medicamentoData.put("totalEnviados", totalEnviados);
            respuesta.add(medicamentoData);
        }
        if (respuesta.isEmpty()) {
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(respuesta);
        }

    }

    @GetMapping("/enviosDelMes")
    public ResponseEntity<List<Map<String, Object>>> getEstadoDomicilios() {
        List<Object[]> resultadosDomiS = domicilioService.countDomiciliosByEstadoDomicilio();
        List<Map<String, Object>> respuestaDomi = new ArrayList<>();
        for (Object[] fila : resultadosDomiS) {
            Map<String, Object> estadoData = new HashMap<>();
            String estado = (String) fila[0];
            estadoData.put("estado", estado);
            Long totalActivos = (Long) fila[1];
            estadoData.put("totalActivos", totalActivos);
            respuestaDomi.add(estadoData);
        }
        if (respuestaDomi.isEmpty()) {
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(respuestaDomi);
        }

    }

    //generar reporte

    @PostMapping("/export/reporte_dashboard")
    public void generarReporteDashboard(
            @RequestParam ("chart1") String chart1DataUrl,
            @RequestParam ("chart2") String chart2DataUrl,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_dashboard_Indrugs.pdf");

        try {
            //datos
            double totalInventario = dashboardService.totalUnidadesEnStock();
            double totalOrdenes = dashboardService.countOrdenActivo();
            double totalUsuarios = dashboardService.contarUsuariosActivos();
            List<UsuarioDTO> usuariosRecientes = dashboardService.obtenerUsuariosRecientes();
            List<OrdenDTO> ordenesRecientes = dashboardService.ObtenerOrdenesRecientes();

            //pdf
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            //cabeza
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK );
            Paragraph title = new Paragraph("Reporte Dashboard Indrugs", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph fecha = new Paragraph("Fecha de generación: " + LocalDate.now());
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            //CUERPO
            Font seccionFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.DARK_GRAY);
            document.add(new Paragraph("Resumen General", seccionFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Unidades de totales en stock del inventario: " + totalInventario));
            document.add(new Paragraph("Cantidad de ordenes activas: " + totalOrdenes));
            document.add(new Paragraph("Usuarios activos registrados: " + totalUsuarios));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            //tabla usuarios recientes
            document.add(new Paragraph("Usuarios Recientes", seccionFont));
            document.add(new Paragraph(" "));

            PdfPTable tablaUsuarios = new PdfPTable(3);
            tablaUsuarios.setWidthPercentage(100);
            tablaUsuarios.setWidths(new float[]{2.0f, 4.0f, 2.0f});

            tablaUsuarios.addCell("Rol");
            tablaUsuarios.addCell("Nombre Completo");
            tablaUsuarios.addCell("Estado");

            for (UsuarioDTO usuario : usuariosRecientes) {
                tablaUsuarios.addCell(usuario.getNombreRol() != null ? usuario.getNombreRol() : "");
                tablaUsuarios.addCell(usuario.getNombre() != null ? usuario.getNombre() : "");
                tablaUsuarios.addCell(usuario.getEstado() != null ? usuario.getEstado() : "");
            }

            document.add(tablaUsuarios);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            //tabla ordenes recientes
            document.add(new Paragraph("Órdenes Recientes", seccionFont));
            document.add(new Paragraph(" "));

            PdfPTable tablaOrdenes = new PdfPTable(4);
            tablaOrdenes.setWidthPercentage(100);
            tablaOrdenes.setWidths(new float[]{2.0f, 3.0f, 2.0f, 2.0f});

            tablaOrdenes.addCell("ID Orden");
            tablaOrdenes.addCell("Usuario");
            tablaOrdenes.addCell("Fecha Orden");
            tablaOrdenes.addCell("Estado Orden");

            for (OrdenDTO orden : ordenesRecientes) {
                tablaOrdenes.addCell(String.valueOf(orden.getIdOrden()));
                tablaOrdenes.addCell(orden.getPacienteNombre() != null ? orden.getPacienteNombre() : "");
                tablaOrdenes.addCell(orden.getFechaEntrega() != null ? String.valueOf(orden.getFechaEntrega()) : "");
                tablaOrdenes.addCell(orden.getEstadoOrden() != null ? orden.getEstadoOrden() : "");
            }

            document.add(tablaOrdenes);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            //graficas

            document.add(new Paragraph("Gráficas", seccionFont));
            document.add(new Paragraph(" "));
            Image chart1 = Image.getInstance(Base64.getDecoder().decode(chart1DataUrl.split(",")[1]));
            chart1.scaleToFit(480, 260);
            chart1.setAlignment(Element.ALIGN_CENTER);
            document.add(chart1);

            document.add(new Paragraph(" "));
            Image chart2 = Image.getInstance(Base64.getDecoder().decode(chart2DataUrl.split(",")[1]));
            chart2.scaleToFit(480, 260);
            chart2.setAlignment(Element.ALIGN_CENTER);
            document.add(chart2);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Documento generado por Indrugs © " +
                    LocalDate.now().getYear(),
                    new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY)));
            document.close();


        } catch (DocumentException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte PDF. " + e.getMessage());
        }
    }




}
