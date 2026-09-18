package org.esfe.Servicios.Interfaces;
import org.esfe.DTOs.persona.PersonaGuardar;
import org.esfe.DTOs.persona.PersonaModificar;
import org.esfe.DTOs.persona.PersonaSalida;

import java.util.List;
import java.util.Optional;
public interface IPersonaService {
    List<PersonaSalida> listar();

    Optional <PersonaSalida> buscarPorId(Integer id);

    PersonaSalida guardar(PersonaGuardar dto);

    Optional<PersonaSalida> modificar(Integer id, PersonaModificar dto);

    boolean eliminar(Integer id);
}
