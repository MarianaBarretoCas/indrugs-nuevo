package com.example.Indrugs.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class InventarioUpdateDTO {

    private Long idInventario;

    private String nombreMedicamento;

    private LocalDateTime fechaSalida;

    private Integer stock;

    private LocalDate vencimiento;

    private String estadoMed;

}
