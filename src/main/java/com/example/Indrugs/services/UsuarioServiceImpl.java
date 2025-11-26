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
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    private final View error;
    RolRepository rolRepository;
    UsuarioRepository usuarioRepository;
    BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(RolRepository rolRepository, UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder, View error){
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.error = error;
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
        Rol rol = rolRepository.findByNombreRol(userUpdate.getRol()).orElseThrow(()-> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    @Override
    public List<UsuarioDTO> buscarPorNombreRolEstado(String termino) {
        List<Usuario> usuario = usuarioRepository.buscarPorNombreRolEstado(termino.toLowerCase());
        return usuario.stream()
                .map(UsuarioMapper:: mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional
    @Override
    public void importarExcel(MultipartFile file) throws IOException {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet hoja = workbook.getSheetAt(0);

        DataFormatter formatter = new DataFormatter();
        List<Usuario> usuarios = new ArrayList<>();

        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;

            try{

                String nombre = formatter.formatCellValue(fila.getCell(0)) ;
                String tipo = formatter.formatCellValue(fila.getCell(1));
                String numdoc = formatter.formatCellValue(fila.getCell(2));
                String direccion = formatter.formatCellValue(fila.getCell(3));
                String estado = formatter.formatCellValue(fila.getCell(4));
                String telefono = formatter.formatCellValue(fila.getCell(5));
                String correo = formatter.formatCellValue(fila.getCell(6));
                if (usuarioRepository.existsByNumDoc(numdoc)) {
                    System.out.println("Saltando fila " + i + ": número de documento ya existe");
                    continue;
                }
                if (usuarioRepository.existsByCorreo(correo)) {
                    System.out.println("Saltando fila " + i + ": correo ya existe");
                    continue;
                }

                Usuario u = new Usuario();
                u.setNombre(nombre);
                u.setTipoDoc(tipo);
                u.setNumDoc(numdoc);
                u.setDireccion(direccion);
                u.setEstado(estado);
                u.setTelefono(telefono);
                u.setCorreo(correo);

                String passLimpia = formatter.formatCellValue(fila.getCell(7));
                String passwordEncriptada = passwordEncoder.encode(passLimpia);
                u.setPassword(passwordEncriptada);

                String rolNombre = formatter.formatCellValue(fila.getCell(8));
                Rol rol = rolRepository.findByNombreRol(rolNombre).orElseThrow(() -> new RuntimeException("El rol '" + rolNombre + "' no existe en la base de datos"));;
                u.setRol(rol);

                usuarios.add(u);
            }catch (Exception e){
                System.out.println("Error en la fila " + (i + 1) + ": " + e.getMessage());
            }

        }

        usuarioRepository.saveAll(usuarios);
        workbook.close();
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
