package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    List<Vehiculo> findByIdPropietario_IdUsuario(Long idUsuario);
    @Query("SELECT v FROM Vehiculo v WHERE v.estadoVehiculo = 'ACTIVO'")
    List<Vehiculo> findVehiculosDisponibles();
}
