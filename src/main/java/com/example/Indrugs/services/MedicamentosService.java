package com.example.Indrugs.services;

import com.example.Indrugs.DTO.MedicamentoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface MedicamentosService {

    List<MedicamentoDTO> readAdmin();
    Page<MedicamentoDTO> readAd(Pageable pageable);
    Page<MedicamentoDTO> mostarEnPaciente(Pageable pageable);
    void crear(MedicamentoDTO MedDto);
    void actualizar(Long idMedicamento, MedicamentoDTO MedDto);
    List <MedicamentoDTO> findByNombre(String nombreMedicamento);
    MedicamentoDTO buscarPorIdMedicamento(Long idMedicamento);

}
