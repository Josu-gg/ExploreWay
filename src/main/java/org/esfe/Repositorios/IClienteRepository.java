package org.esfe.Repositorios;

import org.esfe.Modelos.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IClienteRepository extends JpaRepository<Cliente, Integer> {

    // Carga persona y estado en la misma consulta para evitar el problema N+1.
    @EntityGraph(attributePaths = {"persona", "estado"})
    Optional<Cliente> findById(Integer id);

    @EntityGraph(attributePaths = {"persona", "estado"})
    Page<Cliente> findAll(Pageable pageable);
}