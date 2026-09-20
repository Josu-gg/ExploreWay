package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.persona.PersonaDatos;
import org.esfe.DTOs.persona.PersonaGuardar;
import org.esfe.DTOs.persona.PersonaModificar;
import org.esfe.DTOs.persona.PersonaSalida;
import org.esfe.Modelos.Persona;

import java.util.List;
import java.util.Optional;

public interface IPersonaService {

    // ── CRUD expuesto por PersonaController ───────────────────
    List<PersonaSalida> listar();

    Optional<PersonaSalida> buscarPorId(Integer id);

    PersonaSalida guardar(PersonaGuardar dto);

    Optional<PersonaSalida> modificar(Integer id, PersonaModificar dto);

    boolean eliminar(Integer id);

    // ── Uso interno entre servicios (Usuario, Cliente, Guia) ──
    // Crea y guarda una Persona a partir de los datos personales de cualquier DTO de entrada.
    Persona crear(PersonaDatos datos);

    // Actualiza una Persona ya cargada (debe estar dentro de la transacción del llamador).
    Persona actualizar(Persona persona, PersonaDatos datos);
}
