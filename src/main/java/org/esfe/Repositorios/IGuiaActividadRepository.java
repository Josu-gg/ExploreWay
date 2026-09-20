package org.esfe.Repositorios;

import org.esfe.Modelos.GuiaActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IGuiaActividadRepository extends JpaRepository<GuiaActividad, Integer> {

    // Respaldo en código de UQ_GuiaActividad para dar un 409 con mensaje claro.
    boolean existsByGuia_IdAndActividad_Id(Integer idGuia, Integer idActividad);

    // Se usará al crear reservas y al reasignar: ¿el guía realiza (activamente) esta actividad?
    boolean existsByGuia_IdAndActividad_IdAndEstado_NombreEstado(Integer idGuia,
                                                                 Integer idActividad,
                                                                 String nombreEstado);

    // Guia -> Persona también es LAZY: se incluye "guia.persona" para armar el nombre sin consultas extra.
    @EntityGraph(attributePaths = {"guia", "guia.persona", "actividad", "estado"})
    Optional<GuiaActividad> findById(Integer id);

    @EntityGraph(attributePaths = {"guia", "guia.persona", "actividad", "estado"})
    Page<GuiaActividad> findAll(Pageable pageable);

    // Perfil público del guía: solo asignaciones activas de actividades activas.
    @Query("""
            SELECT ga FROM GuiaActividad ga
            JOIN FETCH ga.guia g
            JOIN FETCH g.persona
            JOIN FETCH ga.actividad a
            JOIN FETCH ga.estado e
            WHERE g.id = :idGuia
              AND e.nombreEstado = :nombreEstado
              AND a.estado.nombreEstado = :nombreEstado
            ORDER BY a.nombre
            """)
    List<GuiaActividad> buscarPorGuiaYEstado(@Param("idGuia") Integer idGuia,
                                             @Param("nombreEstado") String nombreEstado);
}