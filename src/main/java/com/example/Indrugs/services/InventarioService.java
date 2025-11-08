package com.example.Indrugs.services;

import com.example.Indrugs.DTO.InventarioDTO;
import com.example.Indrugs.DTO.InventarioUpdateDTO;

import java.util.List;

public interface InventarioService {
    List<InventarioDTO> read();
    void crear(InventarioDTO inventarioDTO);
    void actualizar(Long idInventario, InventarioUpdateDTO updateDTO);
    InventarioDTO buscarPorId(Long idMedicamento);
    InventarioDTO buscarPorIdInventario(Long idInventario);
    List<InventarioDTO> findByEstado(String estadoMed);
}
