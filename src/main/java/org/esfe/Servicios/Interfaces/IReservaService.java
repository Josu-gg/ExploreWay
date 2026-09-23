package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.comun.CambioEstadoModificar;
import org.esfe.DTOs.reserva.ReservaGuardar;
import org.esfe.DTOs.reserva.ReservaSalida;
import org.springframework.data.domain.Page;

public interface IReservaService {

    ReservaSalida crear(ReservaGuardar dto);

    ReservaSalida obtenerPorId(Integer id);

    Page<ReservaSalida> listarPorCliente(Integer idCliente, int pagina, int tamano);

    // Agenda del guía.
    Page<ReservaSalida> listarPorGuia(Integer idGuia, int pagina, int tamano);

    // Administración: todas las reservas, en cualquier estado.
    Page<ReservaSalida> listar(int pagina, int tamano);

    // Confirmar o completar del Administrador. El estado debe ser de tipo "Reserva".
    ReservaSalida cambiarEstado(Integer id, CambioEstadoModificar dto);

    ReservaSalida cancelar(Integer id);
}