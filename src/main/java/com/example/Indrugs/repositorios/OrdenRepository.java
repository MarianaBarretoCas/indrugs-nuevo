package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Orden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

        List<Orden> findByEstadoOrden(String estadoOrden);
        Page<Orden> findByEstadoOrden(String estadoOrden, Pageable pageable);
        List<Orden> findTop4ByOrderByIdOrdenDesc();
        @Query("SELECT o FROM Orden o WHERE o.domicilio.vehiculo.idPropietario.idUsuario = :idUsuario AND o.estadoOrden = 'ACTIVO' ORDER BY o.idOrden DESC")
        List<Orden> findTop3ByOrderByIdOrdenDescUsuarioId(Long idUsuario);
        long countByEstadoOrden(String estadoOrden);
        @Query("SELECT o FROM Orden o WHERE o.domicilio.vehiculo.idPropietario.idUsuario = :idUsuario AND o.estadoOrden = :estadoOrden")
        Page<Orden> findByOrdenByIdOrden(Long idUsuario, String estadoOrden, Pageable pageable);
        @Query("SELECT o FROM Orden o WHERE o.paciente.idUsuario = :idUsuario AND o.estadoOrden = :estadoOrden")
        List<Orden> findByOrdenByPaciente(Long idUsuario, String estadoOrden);
        Optional<Orden> findByIdOrden(Long idOrden);
        @Query("SELECT COUNT(o) FROM Orden o WHERE o.domicilio.vehiculo.idPropietario.idUsuario = :idUsuario AND o.estadoOrden = :estadoOrden")
        long countOrdenesByUsuarioAndEstado(Long idUsuario, String estadoOrden);

}
