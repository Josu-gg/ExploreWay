package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.persona.PersonaDatos;
import org.esfe.DTOs.persona.PersonaGuardar;
import org.esfe.DTOs.persona.PersonaModificar;
import org.esfe.DTOs.persona.PersonaSalida;
import org.esfe.Modelos.Persona;
import org.esfe.Repositorios.IPersonaRepository;
import org.esfe.Servicios.Interfaces.IPersonaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaServices implements IPersonaService {

    private final IPersonaRepository personaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaSalida> listar() {
        return personaRepository.findAll().stream().map(PersonaSalida::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonaSalida> buscarPorId(Integer id) {
        return personaRepository.findById(id).map(PersonaSalida::desde);
    }

    @Override
    @Transactional
    public PersonaSalida guardar(PersonaGuardar dto) {
        return PersonaSalida.desde(crear(dto));
    }

    @Override
    @Transactional
    public Optional<PersonaSalida> modificar(Integer id, PersonaModificar dto) {
        return personaRepository.findById(id)
                .map(persona -> PersonaSalida.desde(actualizar(persona, dto)));
    }

    @Override
    @Transactional
    public boolean eliminar(Integer id) {
        if (personaRepository.existsById(id)) {
            personaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Persona crear(PersonaDatos datos) {
        return personaRepository.save(aplicarDatos(new Persona(), datos));
    }

    @Override
    @Transactional
    public Persona actualizar(Persona persona, PersonaDatos datos) {
        return personaRepository.save(aplicarDatos(persona, datos));
    }

    // Único lugar donde se copian los datos personales del DTO a la entidad.
    private static Persona aplicarDatos(Persona persona, PersonaDatos datos) {
        persona.setNombre(datos.getNombre().trim());
        persona.setApellido(datos.getApellido().trim());
        persona.setTelefono(datos.getTelefono().trim());
        persona.setDireccion(limpiarOpcional(datos.getDireccion()));
        persona.setFechaNacimiento(datos.getFechaNacimiento());
        persona.setFoto(limpiarOpcional(datos.getFoto()));
        return persona;
    }

    private static String limpiarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
