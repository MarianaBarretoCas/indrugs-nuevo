package com.example.Indrugs.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CorreoMasivoDTO {

    private List<String> correos;
    private String asunto;
    private String mensaje;
}
