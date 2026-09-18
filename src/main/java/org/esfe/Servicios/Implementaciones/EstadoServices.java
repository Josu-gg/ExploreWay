package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.estado.EstadoGuardar;
import org.esfe.DTOs.estado.EstadoModificar;
import org.esfe.DTOs.estado.EstadoSalida;
import org.esfe.Modelos.Estado;
import org.esfe.Repositorios.IEstadoRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadoServices implements IEstadoService {

    private final IEstadoRepository estadoRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EstadoSalida> listar() {
        return estadoRepository.findAll()
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoSalida> listarPorTipos(String tipoEstado) {
        return estadoRepository.findByTipoEstado(tipoEstado)
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EstadoSalida> buscarPorId(Integer id) {
        return estadoRepository.findById(id)
                .map(this::toSalida);
    }

    @Override
    @Transactional
    public EstadoSalida guardar(EstadoGuardar dto) {
        Estado estado = modelMapper.map(dto, Estado.class);
        Estado guardado = estadoRepository.save(estado);
        return toSalida(guardado);
    }

    @Override
    @Transactional
    public Optional<EstadoSalida> modificar(Integer id, EstadoModificar dto) {
        return estadoRepository.findById(id).map(estado -> {
            estado.setNombreEstado(dto.getNombreEstado());
            estado.setTipoEstado(dto.getTipoEstado());
            return toSalida(estadoRepository.save(estado));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Integer id) {
        if (estadoRepository.existsById(id)) {
            estadoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private EstadoSalida toSalida(Estado estado) {
        EstadoSalida dto = modelMapper.map(estado, EstadoSalida.class);
        dto.setIdEstado(estado.getId());
        return dto;
    }
}
