package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.actividad.ActividadGuardar;
import org.esfe.DTOs.actividad.ActividadModificar;
import org.esfe.DTOs.actividad.ActividadSalida;

import java.util.List;
import java.util.Optional;

public interface IActividadService {
        List<ActividadSalida> listar();
        List<ActividadSalida> listarPorEstado(Integer idEstado);
        Optional<ActividadSalida> buscarPorId(Integer id);
        ActividadSalida guardar(ActividadGuardar dto);
        Optional<ActividadSalida> modificar(Integer id, ActividadModificar dto);
        boolean eliminar(Integer id);
}
