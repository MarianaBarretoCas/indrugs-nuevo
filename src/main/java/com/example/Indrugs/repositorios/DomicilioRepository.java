package com.example.Indrugs.repositorios;


import com.example.Indrugs.entities.Domicilio;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.entities.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DomicilioRepository extends JpaRepository<Domicilio, Long> {
    List<Domicilio> findByEstadoDomicilio(String estadoDomicilio);
    List<Domicilio> findTop3ByOrderByIdDomicilioDesc();
    long countByEstadoDomicilio(String estadoDomicilio);
    Optional<Domicilio> findByIdDomicilio(Long idDomicilio);
    List<Domicilio> findByVehiculo_IdPropietario_IdUsuario(Long idUsuario);
    @Query("SELECT d.estadoDomicilio, COUNT(d) FROM Domicilio d GROUP BY d.estadoDomicilio")
    List<Object[]> countDomiciliosByEstadoDomicilio();
}
