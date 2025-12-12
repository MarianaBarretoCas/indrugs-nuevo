package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByRol_idRol(Long idRol);
    Page<Usuario> findByEstado(String estado, Pageable pageable);
    boolean existsByCorreo(String correo);
    boolean existsByNumDoc(String numDoc);
    Optional<Usuario> findByCorreo(String correo);
    long countByRol_nombreRol(String nombreRol);
    long countByEstado(String estado);
    List<Usuario> findTop5ByOrderByIdUsuarioDesc();
    Page<Usuario> findByRol_nombreRolAndEstado(String nombreRol, String estado, Pageable pageable);
    Page<Usuario> findByRol_nombreRol(String nombreRol, Pageable pageable);
    Optional<Usuario> findByIdUsuario(Long idUsuario);
    @Query("SELECT u FROM Usuario u " +
            "JOIN u.rol r " +
            "WHERE LOWER(u.nombre) LIKE %:termino% " +
            "   OR LOWER(r.nombreRol) LIKE %:termino% " +
            "   OR LOWER(u.estado) LIKE %:termino%")
    List<Usuario> buscarPorNombreRolEstado(@Param("termino") String termino);

}
