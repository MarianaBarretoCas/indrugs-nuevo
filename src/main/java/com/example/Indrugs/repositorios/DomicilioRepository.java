package com.example.Indrugs.repositorios;


import com.example.Indrugs.entities.Domicilio;
import com.example.Indrugs.entities.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DomicilioRepository extends JpaRepository<Domicilio, Long> {

    @Query("SELECT d FROM Domicilio d WHERE d.vehiculo.idPropietario.idUsuario = :idUsuario AND d.estadoDomicilio = :estadoDomicilio")
    List<Domicilio> findByEstadoDomicilio(String estadoDomicilio, Long idUsuario);
    List<Domicilio> findTop3ByOrderByIdDomicilioDesc();
    @Query("SELECT COUNT(d) FROM Domicilio d WHERE d.estadoDomicilio = :estado AND d.vehiculo.idPropietario.idUsuario = :idUsuario")
    long countByEstadoDomicilio(String estado, Long idUsuario);
    Optional<Domicilio> findByIdDomicilio(Long idDomicilio);
    List<Domicilio> findByVehiculo_IdPropietario_IdUsuario(Long idUsuario);
    @Query("SELECT d.estadoDomicilio, COUNT(d) FROM Domicilio d GROUP BY d.estadoDomicilio")
    List<Object[]> countDomiciliosByEstadoDomicilio();
    @Query("SELECT d FROM Domicilio d WHERE d.vehiculo.idPropietario.idUsuario = :idUsuario ORDER BY d.idDomicilio DESC")
    List<Domicilio> findTop3ByUsuario(Long idUsuario);
}
