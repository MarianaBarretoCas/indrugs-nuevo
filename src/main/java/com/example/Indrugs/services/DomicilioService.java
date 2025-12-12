package com.example.Indrugs.services;
import com.example.Indrugs.DTO.DomicilioDTO;
import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.VehiculoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DomicilioService {
    Page<DomicilioDTO> read(Long idUsuario, Pageable pageable);
    void crear(DomicilioDTO domicilioDTO);
    void actualizar(Long idDomicilio);
    Page<DomicilioDTO> findByEstadoDomicilio(String estadoDomicilio, long idUsuario, Pageable pageable);
    List<DomicilioDTO> ObtenerDomiciliosRecientes();
    Map<String,Object> ObtenerResumen();
    List<Object[]> countDomiciliosByEstadoDomicilio();

    //nuevos
    long countByEstadoDomicilio(String estado, Long idUsuario);
    List<DomicilioDTO> findTop3ByUsuario(Long idUsuario);

    List<OrdenDTO> findTop3OrdenesByUsuarioId(Long idUsuario);
    Long countVehiculosByUsuario(Long idUsuario);
    VehiculoDTO obtenerDomiciliarioPorId(Long idOrden);
}


