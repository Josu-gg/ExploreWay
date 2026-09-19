package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.destino.DestinoGuardar;
import org.esfe.DTOs.destino.DestinoModificar;
import org.esfe.DTOs.destino.DestinoSalida;

import java.util.List;
import java.util.Optional;

public interface IDestinoService {
    List<DestinoSalida> listar();
    List<DestinoSalida> listarPorEstado(Integer idEstado);
    Optional<DestinoSalida> buscarPorId(Integer id);
    DestinoSalida guardar(DestinoGuardar dto);
    Optional<DestinoSalida> modificar(Integer id, DestinoModificar dto);
    boolean eliminar(Integer id);
}