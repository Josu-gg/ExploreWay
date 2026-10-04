package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.guia.GuiaGuardar;
import org.esfe.DTOs.guia.GuiaModificar;
import org.esfe.DTOs.guia.GuiaPerfilModificar;
import org.esfe.DTOs.guia.GuiaSalida;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.Persona;
import org.esfe.Repositorios.IEstadoRepository;
import org.esfe.Repositorios.IGuiaRepository;
import org.esfe.Repositorios.IPersonaRepository;
import org.esfe.Servicios.Interfaces.IGuiaService;
import org.esfe.Utilidades.UsuarioActual;
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
    private final org.esfe.Repositorios.IRolRepository rolRepository;
    private final org.esfe.Servicios.Interfaces.IUsuarioService usuarioService;
    private final org.esfe.Servicios.Interfaces.IEstadoService estadoService;
    private final UsuarioActual usuarioActual;

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

    // Persona y Usuario se crean primero; si falla el Guia se revierte todo.
    @Override
    @Transactional
    public GuiaSalida registrar(org.esfe.DTOs.guia.GuiaRegistroGuardar dto) {
        org.esfe.Modelos.Rol rolGuia = rolRepository.findByNombreRol("Guia")
                .orElseThrow(() -> new IllegalStateException("Falta el rol Guia en la tabla Rol."));

        org.esfe.Modelos.Usuario usuario = usuarioService.crearConRol(dto, rolGuia);

        Guia guia = new Guia();
        guia.setPersona(usuario.getPersona());
        guia.setBiografia(dto.getBiografia());
        guia.setExperiencia(dto.getExperiencia());
        guia.setEstudios(dto.getEstudios());
        guia.setPrimerosAuxilios(dto.getPrimerosAuxilios());
        guia.setEstadoDisponibilidad(dto.getEstadoDisponibilidad());
        guia.setCalificacionPromedio(BigDecimal.ZERO);
        guia.setEstado(estadoService.obtenerActivo(org.esfe.Servicios.Interfaces.IEstadoService.TIPO_GENERAL));

        return toSalida(guiaRepository.save(guia));
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
    public GuiaSalida modificarMiPerfil(GuiaPerfilModificar dto) {
        Guia guia = guiaRepository.findById(usuarioActual.idGuia())
                .orElseThrow(() -> new IllegalArgumentException("El guía indicado no existe"));

        guia.setBiografia(dto.getBiografia());
        guia.setExperiencia(dto.getExperiencia());
        guia.setEstudios(dto.getEstudios());
        guia.setPrimerosAuxilios(dto.getPrimerosAuxilios());
        guia.setEstadoDisponibilidad(dto.getEstadoDisponibilidad());

        return toSalida(guiaRepository.save(guia));
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
