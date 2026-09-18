package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.estado.EstadoGuardar;
import org.esfe.DTOs.estado.EstadoModificar;
import org.esfe.DTOs.estado.EstadoSalida;

import java.util.List;
import java.util.Optional;
public interface IEstadoService {
    List<EstadoSalida> listar();

    List<EstadoSalida> listarPorTipos(String tipoEstado);
    Optional<EstadoSalida> buscarPorId(Integer id);

    EstadoSalida guardar(EstadoGuardar dto);

    Optional<EstadoSalida> modificar(Integer id, EstadoModificar dto);
    boolean eliminar(Integer id);
}
