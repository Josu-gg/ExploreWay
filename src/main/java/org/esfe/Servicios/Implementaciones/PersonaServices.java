package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.persona.PersonaGuardar;
import org.esfe.DTOs.persona.PersonaModificar;
import org.esfe.DTOs.persona.PersonaSalida;
import org.esfe.Modelos.Persona;
import org.esfe.Repositorios.IPersonaRepository;
import org.esfe.Servicios.Interfaces.IPersonaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaServices implements IPersonaService {

    private final IPersonaRepository personaRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaSalida> listar() {
        return personaRepository.findAll()
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonaSalida> buscarPorId(Integer id) {
        return personaRepository.findById(id)
                .map(this::toSalida);
    }

    @Override
    @Transactional
    public PersonaSalida guardar(PersonaGuardar dto) {
        Persona persona = modelMapper.map(dto, Persona.class);
        Persona guardada = personaRepository.save(persona);
        return toSalida(guardada);
    }

    @Override
    @Transactional
    public Optional<PersonaSalida> modificar(Integer id, PersonaModificar dto) {
        return personaRepository.findById(id).map(persona -> {
            persona.setNombre(dto.getNombre());
            persona.setApellido(dto.getApellido());
            persona.setTelefono(dto.getTelefono());
            persona.setDireccion(dto.getDireccion());
            persona.setFechaNacimiento(dto.getFechaNacimiento());
            persona.setFoto(dto.getFoto());
            return toSalida(personaRepository.save(persona));
        });
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

    private PersonaSalida toSalida(Persona persona) {
        PersonaSalida dto = modelMapper.map(persona, PersonaSalida.class);
        dto.setIdPersona(persona.getId());
        return dto;
    }
}