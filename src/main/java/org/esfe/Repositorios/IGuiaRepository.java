package org.esfe.Repositorios;
import org.esfe.Modelos.Guia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IGuiaRepository extends JpaRepository<Guia, Integer> {
    Optional<Guia> findByPersona_Id(Integer idPersona);
    boolean existsByPersona_Id(Integer idPersona);
    List<Guia> findByEstado_Id(Integer idEstado);
    List<Guia> findByEstadoDisponibilidad(Boolean estadoDisponibilidad);

    // Guía del usuario autenticado (Usuario y Guia comparten persona).
    @org.springframework.data.jpa.repository.Query("SELECT g FROM Guia g, Usuario u WHERE u.persona = g.persona AND u.correo = :correo")
    Optional<Guia> findByCorreoUsuario(@org.springframework.data.repository.query.Param("correo") String correo);
}
