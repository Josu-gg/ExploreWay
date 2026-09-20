package org.esfe.Repositorios;

import org.esfe.Modelos.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRolRepository extends JpaRepository<Rol, Integer> {
 Optional<Rol> findByNombreRol(String nombreRol);
}
