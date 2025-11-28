package com.example.Indrugs.services;

import com.example.Indrugs.DTO.DomicilioDTO;
import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.entities.Domicilio;
import com.example.Indrugs.entities.Orden;
import com.example.Indrugs.mapper.DomicilioMapper;
import com.example.Indrugs.mapper.OrdenMapper;
import com.example.Indrugs.repositorios.DomicilioRepository;
import com.example.Indrugs.repositorios.OrdenRepository;
import com.example.Indrugs.repositorios.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

    @Service
public class DomicilioServicelmpl implements DomicilioService{
    private final DomicilioRepository domicilioRepository;
    private final OrdenRepository ordenRepository;
    private  final VehiculoRepository vehiculoRepository;
    private final OrdenService ordenService;

    public DomicilioServicelmpl(DomicilioRepository domicilioRepository, OrdenRepository ordenRepository, VehiculoRepository vehiculoRepository, OrdenService ordenService) {
        this.ordenRepository = ordenRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.domicilioRepository=domicilioRepository;
        this.ordenService = ordenService;
    }
    @Override
    public List<DomicilioDTO> read(Long idUsuario) {
        List<Domicilio> domicilio = domicilioRepository.findByVehiculo_IdPropietario_IdUsuario(idUsuario);
        return domicilio.stream()
                .map(DomicilioMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void crear(DomicilioDTO domicilioDTO) {
        Domicilio domicilio = DomicilioMapper.toEntity(domicilioDTO, null, null);
        domicilioRepository.save(domicilio);

    }

    @Override
    public void actualizar(Long idDomicilio) {
        Domicilio domicilio = domicilioRepository.findByIdDomicilio(idDomicilio)
                .orElseThrow(() -> new RuntimeException("Domicilio no encontrado con id: " + idDomicilio));
            domicilio.setEstadoDomicilio("ENTREGADO");
            domicilioRepository.save(domicilio);
            ordenService.marcarComoEntregada(domicilio.getOrden().getIdOrden());
    }

    @Override
    public List<DomicilioDTO> findByEstadoDomicilio(String estadoDomicilio, long idUsuario) {
        List<Domicilio> domicilio = domicilioRepository.findByEstadoDomicilio(estadoDomicilio, idUsuario);
        return domicilio.stream()
                .map(DomicilioMapper::entityToDto)
                .collect(Collectors.toList());
    }

//    @Override
//    public long countDomicilioActivo() {
//        return domicilioRepository.countByEstadoDomicilio("EN ESPERA");
//    }

    @Override
    public List<DomicilioDTO> ObtenerDomiciliosRecientes() {
        List<Domicilio> domicilio = domicilioRepository.findTop3ByOrderByIdDomicilioDesc();
        return domicilio.stream()
                .map(DomicilioMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> ObtenerResumen() {
        Map<String, Object> dashboard = new HashMap<>();


//        long domiciliosActivos = domicilioRepository.countByEstadoDomicilio("EN ESPERA");
//        dashboard.put("totalDomiciliosActivos", domiciliosActivos);


        List<Domicilio> top3 = domicilioRepository.findTop3ByOrderByIdDomicilioDesc();
        dashboard.put("domiciliosRecientes", top3);

        return dashboard;
    }

        @Override
        public List<Object[]> countDomiciliosByEstadoDomicilio() {
            List<Object[]> resultados = domicilioRepository.countDomiciliosByEstadoDomicilio();
            return resultados.stream().toList();
        }

        @Override
        public long countByEstadoDomicilio(String estado, Long idUsuario) {
            return domicilioRepository.countByEstadoDomicilio("EN ESPERA", idUsuario);
        }

        @Override
        public List<DomicilioDTO> findTop3ByUsuario(Long idUsuario) {
            List<Domicilio> domiciliosTop3 = domicilioRepository.findTop3ByUsuario(idUsuario);

            if (domiciliosTop3 == null || domiciliosTop3.isEmpty()) {
                return Collections.emptyList();
            }

            return domiciliosTop3.stream()
                    .map(DomicilioMapper::entityToDto)
                    .limit(3)
                    .collect(Collectors.toList());
        }

        @Override
        public List<OrdenDTO> findTop3OrdenesByUsuarioId(Long idUsuario) {
            List<Orden> ordenesTop3 = ordenRepository.findTop3ByOrderByIdOrdenDescUsuarioId(idUsuario);

            if (ordenesTop3 == null || ordenesTop3.isEmpty()) {
                return Collections.emptyList();
            }
            return ordenesTop3.stream()
                    .map(OrdenMapper::toDTO)
                    .limit(3)
                    .collect(Collectors.toList());
        }

        @Override
        public Long countVehiculosByUsuario(Long idUsuario) {
            return vehiculoRepository.countVehiculosActivosByUsuario(idUsuario, "ACTIVO");
        }
    }
