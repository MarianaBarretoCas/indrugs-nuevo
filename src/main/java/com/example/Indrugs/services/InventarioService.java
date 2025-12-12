package com.example.Indrugs.services;

import com.example.Indrugs.DTO.InventarioDTO;
import com.example.Indrugs.DTO.InventarioUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventarioService {
    List<InventarioDTO> read();
    Page<InventarioDTO> readA(Pageable pageable);
    void crear(InventarioDTO inventarioDTO);
    void actualizar(Long idInventario, InventarioUpdateDTO updateDTO);
    InventarioDTO buscarPorId(Long idMedicamento);
    InventarioDTO buscarPorIdInventario(Long idInventario);
    Page<InventarioDTO> findByEstado(String estadoMed, Pageable pageable);
}
