package com.example.Indrugs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORDENES")
    private Long idOrden;

    @ManyToOne
    @JoinColumn(name = "USUARIOS_PACIENTE")
    private Usuario paciente;

    @Column(name = "DIRECCION_ORDEN")
    private String direccionOrden;

    @Column(name = "EPS_ORDEN")
    private String epsOrden;

    @Column(name = "TELEFONO_ORDEN")
    private String telefonoOrden;

    @Column(name = "FECHA_ENTREGA")
    private LocalDateTime fechaEntrega;

    @Column(name = "ESTADO_ORDEN")
    private String estadoOrden;


    @OneToOne(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private Domicilio domicilio;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenMedicamento> ordenMedicamentos;
}
