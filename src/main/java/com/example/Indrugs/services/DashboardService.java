package com.example.Indrugs.services;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    //usuarios
    long contarUsuariosPorRol(String nombreRol);
    long contarUsuariosActivos();
    Map<String, Long> obtenerResumenUsuarios();
    List<UsuarioDTO> obtenerUsuariosRecientes();
    //ordenes
    long countOrdenActivo();
    List<OrdenDTO> ObtenerOrdenesRecientes();
    Map<String,Object> ObtenerResumenOrden();
    List<Object[]> top3MedicamentosMasEnviados();
    //inventario
    Long totalUnidadesEnStock();
}
