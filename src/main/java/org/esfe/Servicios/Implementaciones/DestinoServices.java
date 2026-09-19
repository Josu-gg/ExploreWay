package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.destino.DestinoGuardar;
import org.esfe.DTOs.destino.DestinoModificar;
import org.esfe.DTOs.destino.DestinoSalida;
import org.esfe.Modelos.Destino;
import org.esfe.Modelos.Estado;
import org.esfe.Repositorios.IDestinoRepository;
import org.esfe.Repositorios.IEstadoRepository;
import org.esfe.Servicios.Interfaces.IDestinoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DestinoServices implements IDestinoService {

    private final IDestinoRepository destinoRepository;
    private final IEstadoRepository estadoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DestinoSalida> listar() {
        return destinoRepository.findAll()
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinoSalida> listarPorEstado(Integer idEstado) {
        return destinoRepository.findByEstado_Id(idEstado)
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DestinoSalida> buscarPorId(Integer id) {
        return destinoRepository.findById(id)
                .map(this::toSalida);
    }

    @Override
    @Transactional
    public DestinoSalida guardar(DestinoGuardar dto) {
        Estado estado = estadoRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new IllegalArgumentException("El estado indicado no existe"));

        Destino destino = new Destino();
        destino.setNombre(dto.getNombre());
        destino.setDescripcion(dto.getDescripcion());
        destino.setDepartamento(dto.getDepartamento());
        destino.setMunicipio(dto.getMunicipio());
        destino.setEstado(estado);

        Destino guardado = destinoRepository.save(destino);
        return toSalida(guardado);
    }

    @Override
    @Transactional
    public Optional<DestinoSalida> modificar(Integer id, DestinoModificar dto) {
        return destinoRepository.findById(id).map(destino -> {
            Estado estado = estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new IllegalArgumentException("El estado indicado no existe"));

            destino.setNombre(dto.getNombre());
            destino.setDescripcion(dto.getDescripcion());
            destino.setDepartamento(dto.getDepartamento());
            destino.setMunicipio(dto.getMunicipio());
            destino.setEstado(estado);

            return toSalida(destinoRepository.save(destino));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Integer id) {
        if (destinoRepository.existsById(id)) {
            destinoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private DestinoSalida toSalida(Destino destino) {
        DestinoSalida dto = new DestinoSalida();
        dto.setIdDestino(destino.getId());
        dto.setNombre(destino.getNombre());
        dto.setDescripcion(destino.getDescripcion());
        dto.setDepartamento(destino.getDepartamento());
        dto.setMunicipio(destino.getMunicipio());
        dto.setIdEstado(destino.getEstado().getId());
        dto.setNombreEstado(destino.getEstado().getNombreEstado());
        return dto;
    }
}
