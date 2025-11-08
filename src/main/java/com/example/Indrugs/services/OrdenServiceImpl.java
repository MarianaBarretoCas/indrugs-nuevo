package com.example.Indrugs.services;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.OrdenMedicamentoDTO;
import com.example.Indrugs.entities.*;
import com.example.Indrugs.mapper.OrdenMapper;
import com.example.Indrugs.repositorios.*;
import org.springframework.stereotype.Service;

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
    public void crear(OrdenDTO ordenDTO,Long idUsuario)    {

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


}