package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.historialreserva.HistorialReservaSalida;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.Reserva;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IHistorialReservaService {

    List<HistorialReservaSalida> listarPorReserva(Integer idReserva);

    Page<HistorialReservaSalida> listar(int pagina, int tamano);

    // Uso interno desde ReservaServices; no se expone en ningún endpoint.
    void registrar(Reserva reserva, Guia guiaAnterior, Guia guiaNuevo, String motivo);
}