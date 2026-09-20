package org.esfe.Repositorios;

import org.esfe.Modelos.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IEstadoRepository extends JpaRepository<Estado, Integer> {

    List<Estado> findByTipoEstado(String tipoEstado);

    boolean existsByNombreEstadoAndTipoEstado(String nombreEstado, String tipoEstado);

    Optional<Estado> findByNombreEstadoAndTipoEstado(String nombreEstado, String tipoEstado);
}
