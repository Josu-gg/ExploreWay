package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.guiaactividad.GuiaActividadGuardar;
import org.esfe.DTOs.guiaactividad.GuiaActividadModificar;
import org.esfe.DTOs.guiaactividad.GuiaActividadSalida;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IGuiaActividadService {

    // Público: actividades activas que realiza un guía (para su perfil).
    List<GuiaActividadSalida> listarActivasPorGuia(Integer idGuia);

    GuiaActividadSalida obtenerPorId(Integer id);

    // Administración: todas las asignaciones, en cualquier estado.
    Page<GuiaActividadSalida> listar(int pagina, int tamano);

    GuiaActividadSalida crear(GuiaActividadGuardar dto);

    GuiaActividadSalida modificar(Integer id, GuiaActividadModificar dto);

    void eliminar(Integer id);
}