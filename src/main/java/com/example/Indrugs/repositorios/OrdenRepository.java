package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

        List<Orden> findByEstadoOrden(String estadoOrden);
        List<Orden> findTop4ByOrderByIdOrdenDesc();
        @Query("SELECT o FROM Orden o WHERE o.domicilio.vehiculo.idPropietario.idUsuario = :idUsuario ORDER BY o.idOrden DESC")
        List<Orden> findTop3ByOrderByIdOrdenDescUsuarioId(Long idUsuario);
        long countByEstadoOrden(String estadoOrden);
        @Query("SELECT o FROM Orden o WHERE o.domicilio.vehiculo.idPropietario.idUsuario = :idUsuario AND o.estadoOrden = :estadoOrden")
        List<Orden> findByOrdenByIdOrden(Long idUsuario, String estadoOrden);
        Optional<Orden> findByIdOrden(Long idOrden);
        @Query("SELECT COUNT(o) FROM Orden o WHERE o.domicilio.vehiculo.idPropietario.idUsuario = :idUsuario AND o.estadoOrden = :estadoOrden")
        long countOrdenesByUsuarioAndEstado(Long idUsuario, String estadoOrden);

}
