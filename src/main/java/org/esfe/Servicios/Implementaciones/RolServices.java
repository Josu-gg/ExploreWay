package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.rol.RolGuardar;
import org.esfe.DTOs.rol.RolModificar;
import org.esfe.DTOs.rol.RolSalida;
import org.esfe.Modelos.Rol;
import org.esfe.Repositorios.IRolRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IRolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolServices implements IRolService {

    private final IRolRepository rolRepository;
    private final IEstadoService estadoService;

    @Override
    @Transactional(readOnly = true)
    public List<RolSalida> listar() {
        return rolRepository.findAll()
                .stream()
                .map(this::toSalida)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolSalida> buscarPorId(Integer id) {
        return rolRepository.findById(id)
                .map(this::toSalida);
    }

    @Override
    @Transactional
    public RolSalida guardar(RolGuardar dto) {
        Rol rol = new Rol();
        rol.setNombreRol(dto.getNombreRol());
        rol.setEstado(estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_GENERAL));
        Rol guardado = rolRepository.save(rol);
        return toSalida(guardado);
    }

    @Override
    @Transactional
    public Optional<RolSalida> modificar(Integer id, RolModificar dto) {
        return rolRepository.findById(id).map(rol -> {
            rol.setNombreRol(dto.getNombreRol());
            rol.setEstado(estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_GENERAL));
            return toSalida(rolRepository.save(rol));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Integer id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private RolSalida toSalida(Rol rol) {
        return new RolSalida(
                rol.getId(),
                rol.getNombreRol(),
                rol.getEstado().getId(),
                rol.getEstado().getNombreEstado());
    }
}
