package org.esfe.Repositorios;

import org.esfe.Modelos.DestinoActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDestinoActividadRepository extends JpaRepository<DestinoActividad, Integer> {

    // Respaldo en código de UQ_DestinoActividad para dar un 409 con mensaje claro.
    boolean existsByDestino_IdAndActividad_Id(Integer idDestino, Integer idActividad);

    // Carga destino, actividad y estado en la misma consulta para evitar el problema N+1.
    @EntityGraph(attributePaths = {"destino", "actividad", "estado"})
    Optional<DestinoActividad> findById(Integer id);

    @EntityGraph(attributePaths = {"destino", "actividad", "estado"})
    Page<DestinoActividad> findAll(Pageable pageable);

    // Catálogo público: solo lo que se puede reservar hoy
    // (la oferta, el destino y la actividad deben estar en el estado indicado, normalmente "Activo").
    // Consulta parametrizada: no hay concatenación de texto, por lo que no hay riesgo de inyección SQL.
    @Query("""
            SELECT da FROM DestinoActividad da
            JOIN FETCH da.destino d
            JOIN FETCH da.actividad a
            JOIN FETCH da.estado e
            WHERE d.id = :idDestino
              AND e.nombreEstado = :nombreEstado
              AND d.estado.nombreEstado = :nombreEstado
              AND a.estado.nombreEstado = :nombreEstado
            ORDER BY a.nombre
            """)
    List<DestinoActividad> buscarPorDestinoYEstado(@Param("idDestino") Integer idDestino,
                                                   @Param("nombreEstado") String nombreEstado);
}