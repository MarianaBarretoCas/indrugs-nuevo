package com.example.Indrugs.services;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.entities.Orden;

import java.util.List;
import java.util.Map;

public interface OrdenService {
    List<OrdenDTO> listarOrdenes();
    OrdenDTO listarDetalle(Long idOrden);
    List<OrdenDTO> listarOrdenesP(Long idUsuario);
    List<OrdenDTO> listarOrdenesPa(Long idUsuario);
    void marcarComoEntregada(Long idOrden);
    void crear (OrdenDTO ordenDTO,Long idUsuario);
    void eliminar (Long idOrden);
    void crearDomicilioConOrden(Orden orden);
    List<OrdenDTO> findByEstadoOrden(String estadoOrden);
}
