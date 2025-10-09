package com.example.Indrugs.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class OrdenDTO {

    private Long idOrden;
    private String pacienteNombre;
    private String epsOrden;
    private Long paciente;
    private LocalDateTime fechaEntrega;
    private String direccionOrden;
    private String telefonoOrden;
    private String estadoOrden;

    private List<OrdenMedicamentoDTO> medicamentos;
//    private String formulaMedica;

}