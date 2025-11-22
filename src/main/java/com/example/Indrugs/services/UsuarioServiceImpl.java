package com.example.Indrugs.services;

import com.example.Indrugs.DTO.MedicamentoDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioCreateDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioUpdateDTO;
import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.entities.Rol;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.MedicamentosMap;
import com.example.Indrugs.mapper.UsuarioMapper;
import com.example.Indrugs.repositorios.RolRepository;
import com.example.Indrugs.repositorios.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    RolRepository rolRepository;
    UsuarioRepository usuarioRepository;
    BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(RolRepository rolRepository, UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder ){
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<UsuarioDTO> read(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
        return usuarios.map(UsuarioMapper::mapToDto);
    }

    @Override
    public List<UsuarioDTO> readExport() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(UsuarioMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void crear(UsuarioCreateDTO userCreate) {

        if(usuarioRepository.existsByCorreo(userCreate.getCorreo())){
                throw new RuntimeException("Ya existe un usuario con ese correo");}
        if (usuarioRepository.existsByNumDoc(userCreate.getNumDoc())) {
            throw new RuntimeException("Ya existe un usuario con ese número de documento");
        }
        Usuario usuario = UsuarioMapper.mapNewToEntitie(userCreate);
        String passwordEncriptada = passwordEncoder.encode(userCreate.getPassword());
        usuario.setPassword(passwordEncriptada);
        Rol rol = rolRepository.findById(userCreate.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);
        usuario.setEstado("ACTIVO");
        usuarioRepository.save(usuario);
    }

    @Override
    public void actualizar(Long idUsuario, UsuarioUpdateDTO userUpdate) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UsuarioMapper.mapUpdateTo(usuario, userUpdate);
        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    @Override
    public List<UsuarioDTO> findByNombre(String nombre) {
        List<Usuario> usuario = usuarioRepository.findByNombreContainingIgnoreCase(nombre);
        return usuario.stream()
                .map(UsuarioMapper:: mapToDto)
                .collect(Collectors.toList());
    }


    @Override
    public Page<UsuarioDTO> findByStatus(String estado, Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findByEstado(estado, pageable);
        return usuarios.map(UsuarioMapper::mapToDto);
    }

    @Override
    public Usuario autenticar(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no registrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        } else if (usuario.getEstado().equals("INACTIVO")) {
            throw new RuntimeException("Usuario inactivo, contacte con el administrador");
        }


        return usuario;
    }

    public UsuarioDTO findById(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return UsuarioMapper.mapToDto(usuario);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existsByNumDoc(String numDoc) {
        return usuarioRepository.existsByNumDoc(numDoc);
    }

    @Override
    public Page<UsuarioDTO> findByRolNombre(String nombreRol, Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findByRol_nombreRol(nombreRol, pageable);
        return usuarios.map(UsuarioMapper::mapToDto);
    }

    @Override
    public Page<UsuarioDTO> findByRolNombreAndEstado(String nombreRol, String estado,Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findByRol_nombreRolAndEstado(nombreRol, estado,pageable);
        return usuarios.map(UsuarioMapper::mapToDto);
    }


}
