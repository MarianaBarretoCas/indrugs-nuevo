package com.example.Indrugs.mapper;

import com.example.Indrugs.DTO.ItemDTO;
import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.OrdenMedicamentoDTO;
import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.entities.Orden;
import com.example.Indrugs.entities.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenMapper {



    public static OrdenDTO toDTO(Orden orden) {
        if (orden == null) {
            return null;
        }

        OrdenDTO dto = new OrdenDTO();
        dto.setIdOrden(orden.getIdOrden());

        // Mapear paciente
        if (orden.getPaciente() != null) {
            dto.setPaciente(orden.getPaciente().getIdUsuario());
            dto.setPacienteNombre(orden.getPaciente().getNombre());
        }

        dto.setEpsOrden(orden.getEpsOrden());
        dto.setFechaEntrega(orden.getFechaEntrega());
        dto.setDireccionOrden(orden.getDireccionOrden());
        dto.setTelefonoOrden(orden.getTelefonoOrden());
        dto.setEstadoOrden(orden.getEstadoOrden());
//        dto.setFormulaMedica(orden.getFormulaMedica());
        if (orden.getOrdenMedicamentos() != null) {
            List<OrdenMedicamentoDTO> meds = orden.getOrdenMedicamentos().stream()
                    .map(om -> {
                        OrdenMedicamentoDTO medDTO = new OrdenMedicamentoDTO();
                        medDTO.setIdMedicamento(om.getMedicamento().getIdMedicamento());
                        medDTO.setNombreMedicamento(om.getMedicamento().getNombreMedicamento());
                        medDTO.setCantidad(om.getCantidad());
                        return medDTO;
                    })
                    .collect(Collectors.toList());
            dto.setMedicamentos(meds);
        }

        return dto;
//
    }


    public static List<OrdenDTO> toDTOList(List<Orden> ordenes) {
        if (ordenes == null) {
            return new ArrayList<>();
        }

        return ordenes.stream()
                .map(OrdenMapper::toDTO)
                .collect(Collectors.toList());
    }


    public static Orden toEntity(OrdenDTO ordenDTO) {
        if (ordenDTO == null) {
            return null;
        }

        Orden orden = new Orden();
        orden.setIdOrden(ordenDTO.getIdOrden());
        orden.setEpsOrden(ordenDTO.getEpsOrden());
        orden.setFechaEntrega(ordenDTO.getFechaEntrega());
        orden.setDireccionOrden(ordenDTO.getDireccionOrden());
        orden.setTelefonoOrden(ordenDTO.getTelefonoOrden());
        orden.setEstadoOrden(ordenDTO.getEstadoOrden());
//        orden.setFormulaMedica(ordenDTO.getFormulaMedica());


        return orden;
    }


    public static void updateEntityFromDTO(Orden orden, OrdenDTO ordenDTO) {
        if (orden == null || ordenDTO == null) {
            return;
        }

        if (ordenDTO.getEpsOrden() != null) {
            orden.setEpsOrden(ordenDTO.getEpsOrden());
        }
        if (ordenDTO.getFechaEntrega() != null) {
            orden.setFechaEntrega(ordenDTO.getFechaEntrega());
        }
        if (ordenDTO.getDireccionOrden() != null) {
            orden.setDireccionOrden(ordenDTO.getDireccionOrden());
        }
        if (ordenDTO.getTelefonoOrden() != null) {
            orden.setTelefonoOrden(ordenDTO.getTelefonoOrden());
        }
        if (ordenDTO.getEstadoOrden() != null) {
            orden.setEstadoOrden(ordenDTO.getEstadoOrden());
        }
    }
}