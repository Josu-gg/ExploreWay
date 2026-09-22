package org.esfe.Repositorios;

import org.esfe.Modelos.DisponibilidadGuia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface IDisponibilidadGuiaRepository extends JpaRepository<DisponibilidadGuia, Integer> {

    boolean existsByGuia_IdAndFechaAndHoraInicioAndHoraFin(Integer idGuia,
                                                           LocalDate fecha,
                                                           LocalTime horaInicio,
                                                           LocalTime horaFin);

    @EntityGraph(attributePaths = {"guia", "guia.persona", "estado"})
    Optional<DisponibilidadGuia> findById(Integer id);

    @EntityGraph(attributePaths = {"guia", "guia.persona", "estado"})
    Page<DisponibilidadGuia> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"guia", "guia.persona", "estado"})
    Page<DisponibilidadGuia> findByGuia_IdAndEstado_NombreEstadoAndFechaGreaterThanEqual(Integer idGuia,
                                                                                         String nombreEstado,
                                                                                         LocalDate desde,
                                                                                         Pageable pageable);

    @Query("""
            SELECT COUNT(d) > 0 FROM DisponibilidadGuia d
            WHERE d.guia.id = :idGuia
              AND d.fecha = :fecha
              AND d.estado.nombreEstado = :nombreEstado
              AND d.horaInicio < :horaFin
              AND d.horaFin > :horaInicio
              AND (:idExcluir IS NULL OR d.idDisponibilidad <> :idExcluir)
            """)
    boolean existeSolapamiento(@Param("idGuia") Integer idGuia,
                               @Param("fecha") LocalDate fecha,
                               @Param("horaInicio") LocalTime horaInicio,
                               @Param("horaFin") LocalTime horaFin,
                               @Param("nombreEstado") String nombreEstado,
                               @Param("idExcluir") Integer idExcluir);

    // Se usará al crear reservas y al reasignar: ¿alguna franja activa cubre completo el horario pedido?
    @Query("""
            SELECT COUNT(d) > 0 FROM DisponibilidadGuia d
            WHERE d.guia.id = :idGuia
              AND d.fecha = :fecha
              AND d.estado.nombreEstado = :nombreEstado
              AND d.horaInicio <= :horaInicio
              AND d.horaFin >= :horaFin
            """)
    boolean cubreHorario(@Param("idGuia") Integer idGuia,
                         @Param("fecha") LocalDate fecha,
                         @Param("horaInicio") LocalTime horaInicio,
                         @Param("horaFin") LocalTime horaFin,
                         @Param("nombreEstado") String nombreEstado);
}