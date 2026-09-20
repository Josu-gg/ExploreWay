package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.destinoactividad.DestinoActividadGuardar;
import org.esfe.DTOs.destinoactividad.DestinoActividadModificar;
import org.esfe.DTOs.destinoactividad.DestinoActividadSalida;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IDestinoActividadService {

    // Público: actividades reservables de un destino (con duración, precio y dificultad).
    List<DestinoActividadSalida> listarDisponiblesPorDestino(Integer idDestino);

    DestinoActividadSalida obtenerPorId(Integer id);

    // Administración: todas las ofertas, en cualquier estado.
    Page<DestinoActividadSalida> listar(int pagina, int tamano);

    DestinoActividadSalida crear(DestinoActividadGuardar dto);

    DestinoActividadSalida modificar(Integer id, DestinoActividadModificar dto);
}