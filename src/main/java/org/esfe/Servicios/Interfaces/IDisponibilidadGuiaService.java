package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaGuardar;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaModificar;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaSalida;
import org.springframework.data.domain.Page;

public interface IDisponibilidadGuiaService {

    // Público: franjas activas de un guía paginado.
    Page<DisponibilidadGuiaSalida> listarVigentesPorGuia(Integer idGuia, int pagina, int tamano);

    DisponibilidadGuiaSalida obtenerPorId(Integer id);

    // Administración: todas las franjas, en cualquier estado y fecha.
    Page<DisponibilidadGuiaSalida> listar(int pagina, int tamano);

    DisponibilidadGuiaSalida crear(DisponibilidadGuiaGuardar dto);

    DisponibilidadGuiaSalida modificar(Integer id, DisponibilidadGuiaModificar dto);

    void eliminar(Integer id);
}