package com.example.Indrugs.DTO.Usuario;

import lombok.Data;

@Data
public class RequestChangePassword {

    private String passwordActual;
    private String passwordNueva;
    private String passwordConfirmacion;
}
