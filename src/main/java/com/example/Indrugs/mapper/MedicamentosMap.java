package com.example.Indrugs.mapper;

import com.example.Indrugs.DTO.InventarioDTO;
import com.example.Indrugs.DTO.MedicamentoDTO;
import com.example.Indrugs.entities.Inventario;
import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.services.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicamentosMap {

    private final InventarioService inventarioService;

    public MedicamentosMap(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    public static MedicamentoDTO mapToDtoAdmin(Medicamentos medicamentos){

        MedicamentoDTO dto = new MedicamentoDTO();
        dto.setIdMedicamento(medicamentos.getIdMedicamento());
        dto.setNombreMedicamento(medicamentos.getNombreMedicamento());
        dto.setDescripcionMedicamento(medicamentos.getDescripcionMedicamento());

        return dto;
    }

    public MedicamentoDTO mapToPaciente(Medicamentos medicamentos){

        InventarioDTO invDto = inventarioService.buscarPorId(medicamentos.getIdMedicamento());

        MedicamentoDTO dto = new MedicamentoDTO();
        dto.setIdMedicamento(medicamentos.getIdMedicamento());
        dto.setImagenMedicamento(medicamentos.getImagenMedicamento());
        dto.setNombreMedicamento(medicamentos.getNombreMedicamento());
        dto.setDescripcionMedicamento(medicamentos.getDescripcionMedicamento());
        dto.setStockMedicamento(invDto.getStock());
        return dto;
    }

    public static Medicamentos mapToEntitie(MedicamentoDTO medicamentoDTO){

        Medicamentos medic = new Medicamentos();
        medic.setNombreMedicamento(medicamentoDTO.getNombreMedicamento());
        medic.setDescripcionMedicamento(medicamentoDTO.getDescripcionMedicamento());
        medic.setImagenMedicamento(medicamentoDTO.getImagenMedicamento());

        return medic;
    }

    public static void mapUpdate(Medicamentos medic, MedicamentoDTO medicamentoDTO){

        medic.setNombreMedicamento(medicamentoDTO.getNombreMedicamento());
        medic.setDescripcionMedicamento(medicamentoDTO.getDescripcionMedicamento());
        medic.setImagenMedicamento(medicamentoDTO.getImagenMedicamento());
    }
}
