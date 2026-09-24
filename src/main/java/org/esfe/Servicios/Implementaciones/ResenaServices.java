package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.resena.ResenaGuardar;
import org.esfe.DTOs.resena.ResenaModificar;
import org.esfe.DTOs.resena.ResenaSalida;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.Resena;
import org.esfe.Modelos.Reserva;
import org.esfe.Repositorios.IClienteRepository;
import org.esfe.Repositorios.IGuiaRepository;
import org.esfe.Repositorios.IResenaRepository;
import org.esfe.Repositorios.IReservaRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IResenaService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResenaServices implements IResenaService {

    private final IResenaRepository resenaRepository;
    private final IReservaRepository reservaRepository;
    private final IGuiaRepository guiaRepository;
    private final IClienteRepository clienteRepository;

    // TODO (paso 7, seguridad): comprobar que la reserva pertenece al cliente autenticado.
    @Override
    @Transactional
    public ResenaSalida crear(ResenaGuardar dto) {
        Reserva reserva = reservaRepository.findById(dto.getIdReserva())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada."));

        if (!IEstadoService.ESTADO_COMPLETADA.equals(reserva.getEstado().getNombreEstado())) {
            throw new ConflictoException("Solo se pueden calificar reservas completadas.");
        }
        if (resenaRepository.existsByReserva_IdReserva(reserva.getIdReserva())) {
            throw new ConflictoException("Esta reserva ya tiene una reseña.");
        }

        Resena resena = new Resena();
        resena.setReserva(reserva);
        resena.setCliente(reserva.getCliente());
        resena.setGuia(reserva.getGuia());
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(normalizarComentario(dto.getComentario()));
        resena.setFecha(LocalDateTime.now());

        // saveAndFlush: ante dos peticiones simultáneas, UQ_Resena_Reserva falla aquí -> 409.
        Resena guardada = resenaRepository.saveAndFlush(resena);
        actualizarPromedio(guardada.getGuia());
        return ResenaSalida.desde(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public ResenaSalida obtenerPorId(Integer id) {
        return ResenaSalida.desde(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResenaSalida> listarPorGuia(Integer idGuia, int pagina, int tamano) {
        if (!guiaRepository.existsById(idGuia)) {
            throw new RecursoNoEncontradoException("Guía no encontrado.");
        }
        return resenaRepository
                .findByGuia_IdOrderByFechaDesc(idGuia, Paginacion.de(pagina, tamano))
                .map(ResenaSalida::desde);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResenaSalida> listarPorCliente(Integer idCliente, int pagina, int tamano) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado.");
        }
        return resenaRepository
                .findByCliente_IdClienteOrderByFechaDesc(idCliente, Paginacion.de(pagina, tamano))
                .map(ResenaSalida::desde);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResenaSalida> listar(int pagina, int tamano) {
        return resenaRepository
                .findAll(Paginacion.de(pagina, tamano, "idResena"))
                .map(ResenaSalida::desde);
    }

    @Override
    @Transactional
    public ResenaSalida modificar(Integer id, ResenaModificar dto) {
        Resena resena = buscar(id);
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(normalizarComentario(dto.getComentario()));

        Resena guardada = resenaRepository.saveAndFlush(resena);
        actualizarPromedio(guardada.getGuia());
        return ResenaSalida.desde(guardada);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Resena resena = buscar(id);
        Guia guia = resena.getGuia();

        resenaRepository.delete(resena);
        resenaRepository.flush();
        actualizarPromedio(guia);
    }

    private Resena buscar(Integer id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reseña no encontrada."));
    }

    // Se recalcula desde la BD para que CalificacionPromedio nunca quede desincronizada.
    private void actualizarPromedio(Guia guia) {
        Double promedio = resenaRepository.calcularPromedioPorGuia(guia.getId());
        BigDecimal valor = promedio == null ? BigDecimal.ZERO : BigDecimal.valueOf(promedio);
        guia.setCalificacionPromedio(valor.setScale(2, RoundingMode.HALF_UP));
        guiaRepository.save(guia);
    }

    private static String normalizarComentario(String comentario) {
        if (comentario == null || comentario.isBlank()) {
            return null;
        }
        return comentario.trim();
    }
}