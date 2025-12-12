package com.example.Indrugs.services;

import com.example.Indrugs.DTO.PQRSDTO;

import java.util.List;

public interface PQRSservice {
    void crear (PQRSDTO pqrsdto,Long idUsuario);
    List<PQRSDTO> listarTodo();

    PQRSDTO obtenerPorId(Long id);

    void eliminar(Long id);
}
