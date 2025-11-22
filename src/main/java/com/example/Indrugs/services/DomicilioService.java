package com.example.Indrugs.services;
import com.example.Indrugs.DTO.DomicilioDTO;
import com.example.Indrugs.DTO.OrdenDTO;

import java.util.List;
import java.util.Map;

public interface DomicilioService {
    List<DomicilioDTO> read(Long idUsuario);
    void crear(DomicilioDTO domicilioDTO);
    void actualizar(Long idDomicilio);
    List<DomicilioDTO> findByEstadoDomicilio(String estadoDomicilio, long idUsuario);
    List<DomicilioDTO> ObtenerDomiciliosRecientes();
    Map<String,Object> ObtenerResumen();
    List<Object[]> countDomiciliosByEstadoDomicilio();

    //nuevos
    long countByEstadoDomicilio(String estado, Long idUsuario);
    List<DomicilioDTO> findTop3ByUsuario(Long idUsuario);
    List<OrdenDTO> findTop3OrdenesByUsuarioId(Long idUsuario);
    Long countVehiculosByUsuario(Long idUsuario);
}


