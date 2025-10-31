package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.OrdenMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdenMedicamentoRepository extends JpaRepository<OrdenMedicamento , Long> {

    @Query("""
        SELECT m.nombreMedicamento, COUNT(o)
        FROM OrdenMedicamento o
        JOIN o.medicamento m
        GROUP BY m.nombreMedicamento
        ORDER BY COUNT(o) DESC
    """)
    List<Object[]> top3MedicamentosMasEnviados();

}
