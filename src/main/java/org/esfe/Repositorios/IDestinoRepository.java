package org.esfe.Repositorios;

import org.esfe.Modelos.Destino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IDestinoRepository extends JpaRepository<Destino, Integer> {
    Optional<Destino> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Destino> findByEstado(Integer IdEstado);
}
