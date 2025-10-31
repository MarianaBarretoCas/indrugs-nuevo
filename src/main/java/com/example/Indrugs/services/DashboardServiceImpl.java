package com.example.Indrugs.services;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.entities.Orden;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.OrdenMapper;
import com.example.Indrugs.mapper.UsuarioMapper;
import com.example.Indrugs.repositorios.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrdenMedicamentoRepository ordenMedRepository;
    private final InventarioRepository inventarioRepository;

    public DashboardServiceImpl(OrdenRepository ordenRepository,
                            UsuarioRepository usuarioRepository,
                            OrdenMedicamentoRepository ordenMedRepository,
                            InventarioRepository inventarioRepository) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.ordenMedRepository = ordenMedRepository;
        this.inventarioRepository = inventarioRepository;
    }


    //usuarios
    public long contarUsuariosPorRol(String nombreRol) {
        return usuarioRepository.countByRol_nombreRol(nombreRol);
    }

    @Override
    public long contarUsuariosActivos() {
        return usuarioRepository.countByEstado("ACTIVO");
    }

    @Override
    public Map<String, Long> obtenerResumenUsuarios() {
        Map<String, Long> resumen = new HashMap<>();
        resumen.put("pacientes", contarUsuariosPorRol("Paciente"));
        resumen.put("domiciliarios", contarUsuariosPorRol("Domiciliario"));
        resumen.put("administradores", contarUsuariosPorRol("Administrador"));
        resumen.put("ACTIVOS", contarUsuariosActivos());
        return resumen;
    }
    //
    @Override
    public List<UsuarioDTO> obtenerUsuariosRecientes() {
        List<Usuario> usuarios = usuarioRepository.findTop5ByOrderByIdUsuarioDesc();
        return usuarios.stream()
                .map(UsuarioMapper::mapToDto)
                .collect(Collectors.toList());
    }

    //ordenes
    public long countOrdenActivo() {
        return ordenRepository.countByEstadoOrden("ACTIVO");
    }

    @Override
    public List<OrdenDTO> ObtenerOrdenesRecientes() {
        List<Orden> domicilio = ordenRepository.findTop4ByOrderByIdOrdenDesc();
        return domicilio.stream()
                .map(OrdenMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> ObtenerResumenOrden() {

        Map<String, Object> dashboard = new HashMap<>();


        long ordenesActivos = ordenRepository.countByEstadoOrden("ACTIVO");
        dashboard.put("totalOrdenesActivos", ordenesActivos);


        List<Orden> top4Orden = ordenRepository.findTop4ByOrderByIdOrdenDesc();
        dashboard.put("ordenesRecientes", top4Orden);

        return dashboard;
    }

    @Override
    public List<Object[]> top3MedicamentosMasEnviados() {
        List<Object[]> resultados = ordenMedRepository.top3MedicamentosMasEnviados();
        return resultados.stream().limit(3)
                .collect(Collectors.toList());
    }

    //inventario
    @Override
    public Long totalUnidadesEnStock() {
        Long total = inventarioRepository.contarUnidadesEnStock();
        return total != null ? total : 0L;
    }

}
