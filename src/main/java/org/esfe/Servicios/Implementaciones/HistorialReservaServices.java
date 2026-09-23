package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.historialreserva.HistorialReservaSalida;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.HistorialReserva;
import org.esfe.Modelos.Reserva;
import org.esfe.Repositorios.IHistorialReservaRepository;
import org.esfe.Repositorios.IReservaRepository;
import org.esfe.Servicios.Interfaces.IHistorialReservaService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialReservaServices implements IHistorialReservaService {

    private final IHistorialReservaRepository historialReservaRepository;
    private final IReservaRepository reservaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HistorialReservaSalida> listarPorReserva(Integer idReserva) {
        if (!reservaRepository.existsById(idReserva)) {
            throw new RecursoNoEncontradoException("Reserva no encontrada.");
        }
        return historialReservaRepository.findByReserva_IdReservaOrderByFechaCambioAsc(idReserva)
                .stream()
                .map(HistorialReservaSalida::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistorialReservaSalida> listar(int pagina, int tamano) {
        return historialReservaRepository
                .findAll(Paginacion.de(pagina, tamano, "idHistorial"))
                .map(HistorialReservaSalida::desde);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void registrar(Reserva reserva, Guia guiaAnterior, Guia guiaNuevo, String motivo) {
        HistorialReserva historial = new HistorialReserva();
        historial.setReserva(reserva);
        historial.setGuiaAnterior(guiaAnterior);
        historial.setGuiaNuevo(guiaNuevo);
        historial.setMotivo(motivo);
        historial.setFechaCambio(LocalDateTime.now());

        historialReservaRepository.save(historial);
    }
}