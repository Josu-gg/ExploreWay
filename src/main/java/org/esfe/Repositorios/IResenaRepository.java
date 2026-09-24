package org.esfe.Repositorios;

import org.esfe.Modelos.Resena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IResenaRepository extends JpaRepository<Resena, Integer> {

    @EntityGraph(attributePaths = {"reserva", "cliente", "cliente.persona", "guia", "guia.persona"})
    Optional<Resena> findById(Integer id);

    @EntityGraph(attributePaths = {"reserva", "cliente", "cliente.persona", "guia", "guia.persona"})
    Page<Resena> findAll(Pageable pageable);

    // Perfil público del guía: las más recientes primero.
    @EntityGraph(attributePaths = {"reserva", "cliente", "cliente.persona", "guia", "guia.persona"})
    Page<Resena> findByGuia_IdOrderByFechaDesc(Integer idGuia, Pageable pageable);

    @EntityGraph(attributePaths = {"reserva", "cliente", "cliente.persona", "guia", "guia.persona"})
    Page<Resena> findByCliente_IdClienteOrderByFechaDesc(Integer idCliente, Pageable pageable);

    boolean existsByReserva_IdReserva(Integer idReserva);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.guia.id = :idGuia")
    Double calcularPromedioPorGuia(@Param("idGuia") Integer idGuia);
}