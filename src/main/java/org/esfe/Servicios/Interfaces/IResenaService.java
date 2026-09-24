package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.resena.ResenaGuardar;
import org.esfe.DTOs.resena.ResenaModificar;
import org.esfe.DTOs.resena.ResenaSalida;
import org.springframework.data.domain.Page;

public interface IResenaService {

    ResenaSalida crear(ResenaGuardar dto);

    ResenaSalida obtenerPorId(Integer id);

    // Público: reseñas del perfil de un guía.
    Page<ResenaSalida> listarPorGuia(Integer idGuia, int pagina, int tamano);

    Page<ResenaSalida> listarPorCliente(Integer idCliente, int pagina, int tamano);

    // Administración: todas las reseñas.
    Page<ResenaSalida> listar(int pagina, int tamano);

    ResenaSalida modificar(Integer id, ResenaModificar dto);

    void eliminar(Integer id);
}