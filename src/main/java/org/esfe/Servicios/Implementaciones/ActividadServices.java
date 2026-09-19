package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.actividad.ActividadGuardar;
import org.esfe.DTOs.actividad.ActividadModificar;
import org.esfe.DTOs.actividad.ActividadSalida;
import org.esfe.Modelos.Actividad;
import org.esfe.Modelos.Estado;
import org.esfe.Repositorios.IActividadRepository;
import org.esfe.Repositorios.IEstadoRepository;
import org.esfe.Servicios.Interfaces.IActividadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActividadServices implements IActividadService {

    private final IActividadRepository actividadRepository;
    private final IEstadoRepository estadoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ActividadSalida> listar() {
        return actividadRepository.findAll()
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadSalida> listarPorEstado(Integer idEstado) {
        return actividadRepository.findByEstado_Id(idEstado)
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActividadSalida> buscarPorId(Integer id) {
        return actividadRepository.findById(id)
                .map(this::toSalida);
    }

    @Override
    @Transactional
    public ActividadSalida guardar(ActividadGuardar dto) {
        Estado estado = estadoRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new IllegalArgumentException("El estado indicado no existe"));

        Actividad actividad = new Actividad();
        actividad.setNombre(dto.getNombre());
        actividad.setDescripcion(dto.getDescripcion());
        actividad.setDificultad(dto.getDificultad());
        actividad.setEstado(estado);

        Actividad guardada = actividadRepository.save(actividad);
        return toSalida(guardada);
    }

    @Override
    @Transactional
    public Optional<ActividadSalida> modificar(Integer id, ActividadModificar dto) {
        return actividadRepository.findById(id).map(actividad -> {
            Estado estado = estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new IllegalArgumentException("El estado indicado no existe"));

            actividad.setNombre(dto.getNombre());
            actividad.setDescripcion(dto.getDescripcion());
            actividad.setDificultad(dto.getDificultad());
            actividad.setEstado(estado);

            return toSalida(actividadRepository.save(actividad));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Integer id) {
        if (actividadRepository.existsById(id)) {
            actividadRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ActividadSalida toSalida(Actividad actividad) {
        ActividadSalida dto = new ActividadSalida();
        dto.setIdActividad(actividad.getId());
        dto.setNombre(actividad.getNombre());
        dto.setDescripcion(actividad.getDescripcion());
        dto.setDificultad(actividad.getDificultad());
        dto.setIdEstado(actividad.getEstado().getId());
        dto.setNombreEstado(actividad.getEstado().getNombreEstado());
        return dto;
    }
}
