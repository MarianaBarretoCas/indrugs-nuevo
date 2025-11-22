package com.example.Indrugs.services;


import com.example.Indrugs.DTO.MedicamentoDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioCreateDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioUpdateDTO;
import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.MedicamentosMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface UsuarioService  {

    Page<UsuarioDTO> read(Pageable pageable);
    List<UsuarioDTO> readExport();
    void crear(UsuarioCreateDTO userCreate);
    void actualizar(Long idUsuario, UsuarioUpdateDTO userUpdate);
    void eliminar(Long idUsuario);
    Page<UsuarioDTO> findByStatus(String estado, Pageable pageable);
    Usuario autenticar(String correo, String password);
    boolean existsByCorreo(String correo);
    boolean existsByNumDoc(String numDoc);
    Page<UsuarioDTO> findByRolNombreAndEstado(String nombreRol, String estado, Pageable pageable);
    Page<UsuarioDTO> findByRolNombre(String nombreRol, Pageable pageable);
    UsuarioDTO findById(Long idUsuario);
    List<UsuarioDTO> findByNombre(String nombre);

}
