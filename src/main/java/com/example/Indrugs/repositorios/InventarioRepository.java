package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Inventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Page<Inventario> findByEstadoMed(String estadoMed, Pageable pageable);
    @Query("SELECT SUM(i.stock) FROM Inventario i")
    Long contarUnidadesEnStock();
    boolean existsByIdMedicamento_IdMedicamento(Long idMedicamento);
    Optional<Inventario> findByidMedicamento_IdMedicamento(Long idMedicamento);

}
