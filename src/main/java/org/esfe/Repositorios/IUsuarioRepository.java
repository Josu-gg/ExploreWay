package org.esfe.Repositorios;

import org.esfe.Modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByCorreo(String correo);

    // Al modificar: el correo puede coincidir con el del propio usuario.
    boolean existsByCorreoAndIdUsuarioNot(String correo, Integer idUsuario);

    // Se usará en el login (Spring Security).
    @EntityGraph(attributePaths = {"rol", "estado", "persona"})
    Optional<Usuario> findByCorreo(String correo);

    // Carga rol, estado y persona en la misma consulta para evitar el problema N+1.
    @EntityGraph(attributePaths = {"rol", "estado", "persona"})
    Optional<Usuario> findById(Integer id);

    @EntityGraph(attributePaths = {"rol", "estado", "persona"})
    Page<Usuario> findAll(Pageable pageable);
}