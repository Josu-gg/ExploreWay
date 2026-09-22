package org.esfe.Repositorios;

import org.esfe.Modelos.GuiaDestino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IGuiaDestinoRepository extends JpaRepository<GuiaDestino, Integer> {

    boolean existsByGuia_IdAndDestino_Id(Integer idGuia, Integer idDestino);

    boolean existsByGuia_IdAndDestino_IdAndEstado_NombreEstado(Integer idGuia,
                                                               Integer idDestino,
                                                               String nombreEstado);

    @EntityGraph(attributePaths = {"guia", "guia.persona", "destino", "estado"})
    Optional<GuiaDestino> findById(Integer id);

    @EntityGraph(attributePaths = {"guia", "guia.persona", "destino", "estado"})
    Page<GuiaDestino> findAll(Pageable pageable);

    // Perfil público del guía
    @Query("""
        SELECT gd FROM GuiaDestino gd
        JOIN FETCH gd.guia g
        JOIN FETCH g.persona
        JOIN FETCH gd.destino d
        JOIN FETCH gd.estado e
        WHERE g.id = :idGuia
        AND e.nombreEstado = :nombreEstado
        AND d.estado.nombreEstado = :nombreEstado
        ORDER BY d.nombre
        """)
    List<GuiaDestino> buscarPorGuiaYEstado(@Param("idGuia") Integer idGuia,
                                           @Param("nombreEstado") String nombreEstado);
}
