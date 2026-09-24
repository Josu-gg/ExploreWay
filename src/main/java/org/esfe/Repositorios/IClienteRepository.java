package org.esfe.Repositorios;

import org.esfe.Modelos.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IClienteRepository extends JpaRepository<Cliente, Integer> {

    // Carga persona y estado en la misma consulta para evitar el problema N+1.
    @EntityGraph(attributePaths = {"persona", "estado"})
    Optional<Cliente> findById(Integer id);
    //Permite obtener el cliente del usuario autenticado(Usuario y cliente comparten persona)
    @EntityGraph(attributePaths = {"persona", "estado"})
    Optional<Cliente> findByPersona_Id(Integer idPersona);

    @EntityGraph(attributePaths = {"persona", "estado"})
    Page<Cliente> findAll(Pageable pageable);
    @Query("SELECT c FROM Cliente c, Usuario u WHERE u.persona = c.persona AND u.correo = :correo")
    Optional<Cliente> findByCorreoUsuario(@Param("correo") String correo);
}