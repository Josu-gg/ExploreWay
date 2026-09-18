package org.esfe.Repositorios;
import org.esfe.Modelos.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
public interface IPersonaRepository  extends JpaRepository<Persona, Integer>{
}
