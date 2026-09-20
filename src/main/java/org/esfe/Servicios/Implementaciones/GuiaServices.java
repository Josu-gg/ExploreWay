package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.guia.GuiaGuardar;
import org.esfe.DTOs.guia.GuiaModificar;
import org.esfe.DTOs.guia.GuiaSalida;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.Persona;
import org.esfe.Repositorios.IEstadoRepository;
import org.esfe.Repositorios.IGuiaRepository;
import org.esfe.Repositorios.IPersonaRepository;
import org.esfe.Servicios.Interfaces.IGuiaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuiaServices implements IGuiaService {
    private final IGuiaRepository guiaRepository;
    private final IPersonaRepository personaRepository;
    private final IEstadoRepository estadoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GuiaSalida> listar() {
        return guiaRepository.findAll()
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuiaSalida> listarDisponibles() {
        return guiaRepository.findByEstadoDisponibilidad(true)
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GuiaSalida> buscarPorId(Integer id) {
        return guiaRepository.findById(id)
                .map(this::toSalida);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GuiaSalida> buscarPorPersona(Integer idPersona) {
        return guiaRepository.findByPersona_Id(idPersona)
                .map(this::toSalida);
    }

    @Override
    @Transactional
    public GuiaSalida guardar(GuiaGuardar dto) {
        if (guiaRepository.existsByPersona_Id(dto.getIdPersona())) {
            throw new IllegalArgumentException("Esta persona ya está registrada como guía");
        }

        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException("La persona indicada no existe"));

        Estado estado = estadoRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new IllegalArgumentException("El estado indicado no existe"));

        Guia guia = new Guia();
        guia.setPersona(persona);
        guia.setBiografia(dto.getBiografia());
        guia.setExperiencia(dto.getExperiencia());
        guia.setEstudios(dto.getEstudios());
        guia.setPrimerosAuxilios(dto.getPrimerosAuxilios());
        guia.setEstadoDisponibilidad(dto.getEstadoDisponibilidad());
        guia.setCalificacionPromedio(BigDecimal.ZERO);
        guia.setEstado(estado);

        Guia guardado = guiaRepository.save(guia);
        return toSalida(guardado);
    }

    @Override
    @Transactional
    public Optional<GuiaSalida> modificar(Integer id, GuiaModificar dto) {
        return guiaRepository.findById(id).map(guia -> {
            Estado estado = estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new IllegalArgumentException("El estado indicado no existe"));

            guia.setBiografia(dto.getBiografia());
            guia.setExperiencia(dto.getExperiencia());
            guia.setEstudios(dto.getEstudios());
            guia.setPrimerosAuxilios(dto.getPrimerosAuxilios());
            guia.setEstadoDisponibilidad(dto.getEstadoDisponibilidad());
            guia.setEstado(estado);

            return toSalida(guiaRepository.save(guia));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Integer id) {
        if (guiaRepository.existsById(id)) {
            guiaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private GuiaSalida toSalida(Guia guia) {
        GuiaSalida dto = new GuiaSalida();
        dto.setIdGuia(guia.getId());
        dto.setIdPersona(guia.getPersona().getId());
        dto.setNombrePersona(guia.getPersona().getNombre());
        dto.setApellidoPersona(guia.getPersona().getApellido());
        dto.setBiografia(guia.getBiografia());
        dto.setExperiencia(guia.getExperiencia());
        dto.setEstudios(guia.getEstudios());
        dto.setPrimerosAuxilios(guia.getPrimerosAuxilios());
        dto.setEstadoDisponibilidad(guia.getEstadoDisponibilidad());
        dto.setCalificacionPromedio(guia.getCalificacionPromedio());
        dto.setIdEstado(guia.getEstado().getId());
        dto.setNombreEstado(guia.getEstado().getNombreEstado());
        return dto;
    }
}
