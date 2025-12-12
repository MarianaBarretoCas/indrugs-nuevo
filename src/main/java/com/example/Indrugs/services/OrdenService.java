package com.example.Indrugs.services;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.entities.Orden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface OrdenService {
    List<OrdenDTO> listarOrdenes();
    Page<OrdenDTO> listarOrdenesPage(Pageable pageable);
    OrdenDTO listarDetalle(Long idOrden);
    Page<OrdenDTO> listarOrdenesP(Long idUsuario,Pageable pageable);
    List<OrdenDTO> listarOrdenesPa(Long idUsuario);
    void marcarComoEntregada(Long idOrden);
    void crear (OrdenDTO ordenDTO,Long idUsuario);
    void eliminar (Long idOrden);
    void crearDomicilioConOrden(Orden orden);
    List<OrdenDTO> findByEstadoOrden(String estadoOrden);
    Page<OrdenDTO> findByEstaOrden(String estadoOrden, Pageable pageable);

    // excel metodo
    byte[] generarReporteExcel(List<OrdenDTO> ordenes) throws IOException;
}