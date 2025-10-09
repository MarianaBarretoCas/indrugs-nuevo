package com.example.Indrugs.entities;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@Table(name = "ordenes_has_medicamentos")
public class OrdenMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICAMENTO_POR_ORDEN")
    private Long idOrdenMedicamento;

    @ManyToOne
    @JoinColumn(name = "ID_ORDENES")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "ID_MEDICAMENTOS")
    private Medicamentos medicamento;

    @Column(name = "CANTIDAD")
    private Integer cantidad;


}
