package com.example.Indrugs.services;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.OrdenMedicamentoDTO;
import com.example.Indrugs.entities.*;
import com.example.Indrugs.mapper.OrdenMapper;
import com.example.Indrugs.repositorios.*;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DomicilioRepository domicilioRepository;
    private final OrdenMedicamentoRepository ordenMedRepository;
    private final VehiculoRepository vehiculoRepository;
    private final InventarioRepository inventarioRepository;

    public OrdenServiceImpl(OrdenRepository ordenRepository,
                            MedicamentoRepository medicamentoRepository,
                            UsuarioRepository usuarioRepository,
                            DomicilioRepository domicilioRepository,
                            OrdenMedicamentoRepository ordenMedRepository,
                            VehiculoRepository vehiculoRepository,
                            InventarioRepository inventarioRepository) {
        this.ordenRepository = ordenRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.domicilioRepository = domicilioRepository;
        this.ordenMedRepository = ordenMedRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public List<OrdenDTO> listarOrdenes() {
        List<Orden> ordenes = ordenRepository.findAll();
        return OrdenMapper.toDTOList(ordenes);
    }

    @Override
    public OrdenDTO listarDetalle(Long idOrden) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("El ID de la orden no puede ser nulo o menor o igual a 0");
        }
        Orden orden = ordenRepository.findByIdOrden(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        return OrdenMapper.toDTO(orden);
    }

    @Override
    public List<OrdenDTO> findByEstadoOrden(String estadoOrden) {
        List<Orden> orden = ordenRepository.findByEstadoOrden(estadoOrden);
        return orden.stream()
                .map(OrdenMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdenDTO> listarOrdenesP(Long idUsuario) {
        List<Orden> ordenes = ordenRepository.findByOrdenByIdOrden(idUsuario, "ACTIVO");
        return ordenes.stream()
                .map(OrdenMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdenDTO> listarOrdenesPa(Long idUsuario) {
        List<Orden> ordenes = ordenRepository.findByOrdenByPaciente(idUsuario, "ACTIVO");
        return ordenes.stream()
                .map(OrdenMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void marcarComoEntregada(Long idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + idOrden));
        orden.setEstadoOrden("INACTIVA");
        ordenRepository.save(orden);
    }

    @Override
    public void crear(OrdenDTO ordenDTO, Long idUsuario) {
        Orden orden = OrdenMapper.toEntity(ordenDTO);
        Usuario usuario = usuarioRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        orden.setPaciente(usuario);
        orden.setEstadoOrden("ACTIVO");
        ordenRepository.save(orden);

        for (OrdenMedicamentoDTO medDTO : ordenDTO.getMedicamentos()) {
            Medicamentos medicamento = medicamentoRepository.findById(medDTO.getIdMedicamento())
                    .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

            Inventario inventario = inventarioRepository.findByidMedicamento_IdMedicamento(medicamento.getIdMedicamento())
                    .orElseThrow(() -> new RuntimeException(
                            "No se encontró inventario para el medicamento: " + medicamento.getNombreMedicamento()
                    ));
            if (inventario.getStock() < medDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el medicamento: " + medicamento.getNombreMedicamento());
            }
            inventario.setStock(inventario.getStock() - medDTO.getCantidad());
            inventarioRepository.save(inventario);

            OrdenMedicamento ordenMedicamento = new OrdenMedicamento();
            ordenMedicamento.setOrden(orden);
            ordenMedicamento.setMedicamento(medicamento);
            ordenMedicamento.setCantidad(medDTO.getCantidad());

            ordenMedRepository.save(ordenMedicamento);
        }
        crearDomicilioConOrden(orden);
    }

    @Override
    public void crearDomicilioConOrden(Orden orden) {
        Domicilio domicilio = new Domicilio();
        domicilio.setOrden(orden);
        domicilio.setUbicacionDomicilio(orden.getDireccionOrden());
        domicilio.setEstadoDomicilio("EN ESPERA");
        domicilio.setFechaEntregaDomicilio(orden.getFechaEntrega());
        List<Vehiculo> vehiculosDisponibles = vehiculoRepository.findVehiculosDisponibles();
        if (!vehiculosDisponibles.isEmpty()) {
            Random random = new Random();
            Vehiculo vehiculoAsignado = vehiculosDisponibles.get(random.nextInt(vehiculosDisponibles.size()));
            domicilio.setVehiculo(vehiculoAsignado);
        } else {
            throw new RuntimeException("No hay vehículos disponibles para asignar el domicilio");
        }
        domicilioRepository.save(domicilio);
    }

    @Override
    public void eliminar(Long idOrden) {
        ordenRepository.deleteById(idOrden);
    }

    public OrdenDTO obtenerOrdenPorId(Long id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
        return OrdenMapper.toDTO(orden);
    }

//    NUEVO MÉTODOGenera reporte de órdenes en formato Excel

    @Override
    public byte[] generarReporteExcel(List<OrdenDTO> ordenes) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte de Órdenes");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] columnas = {"ID", "PACIENTE", "EPS", "FECHA DE ENTREGA",
                "DIRECCIÓN", "TELÉFONO", "ESTADO"};

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (OrdenDTO orden : ordenes) {
            Row row = sheet.createRow(rowNum++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(orden.getIdOrden() != null ? orden.getIdOrden() : 0);
            cell0.setCellStyle(dataStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(orden.getPacienteNombre() != null ? orden.getPacienteNombre() : "");
            cell1.setCellStyle(dataStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(orden.getEpsOrden() != null ? orden.getEpsOrden() : "");
            cell2.setCellStyle(dataStyle);

            Cell cell3 = row.createCell(3);
            if (orden.getFechaEntrega() != null) {
                cell3.setCellValue(orden.getFechaEntrega().toString());
            } else {
                cell3.setCellValue("");
            }
            cell3.setCellStyle(dateStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(orden.getDireccionOrden() != null ? orden.getDireccionOrden() : "");
            cell4.setCellStyle(dataStyle);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(orden.getTelefonoOrden() != null ? orden.getTelefonoOrden() : "");
            cell5.setCellStyle(dataStyle);

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(orden.getEstadoOrden() != null ? orden.getEstadoOrden() : "");
            cell6.setCellStyle(dataStyle);
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    // MÉTODOS PRIVADOS: Crean estilos para el Excel
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm"));
        return style;
    }
}