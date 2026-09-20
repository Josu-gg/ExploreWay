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
}
