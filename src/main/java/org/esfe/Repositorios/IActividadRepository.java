package org.esfe.Repositorios;

import org.esfe.Modelos.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IActividadRepository extends JpaRepository<Actividad, Integer> {
    Optional<Actividad> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Actividad> findByEstado_Id(Integer idEstado);
}
