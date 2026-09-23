package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.comun.CambioEstadoModificar;
import org.esfe.DTOs.reserva.ReservaGuardar;
import org.esfe.DTOs.reserva.ReservaSalida;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Excepciones.SolicitudInvalidaException;
import org.esfe.Modelos.Cliente;
import org.esfe.Modelos.DestinoActividad;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.Reserva;
import org.esfe.Repositorios.IClienteRepository;
import org.esfe.Repositorios.IDestinoActividadRepository;
import org.esfe.Repositorios.IDisponibilidadGuiaRepository;
import org.esfe.Repositorios.IGuiaActividadRepository;
import org.esfe.Repositorios.IGuiaDestinoRepository;
import org.esfe.Repositorios.IGuiaRepository;
import org.esfe.Repositorios.IReservaRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IReservaService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServices implements IReservaService {

    // Estados que ocupan la agenda del guía: una reserva cancelada libera el horario.
    private static final List<String> ESTADOS_VIGENTES =
            List.of(IEstadoService.ESTADO_PENDIENTE, IEstadoService.ESTADO_CONFIRMADA);

    private final IReservaRepository reservaRepository;
    private final IClienteRepository clienteRepository;
    private final IGuiaRepository guiaRepository;
    private final IDestinoActividadRepository destinoActividadRepository;
    private final IGuiaDestinoRepository guiaDestinoRepository;
    private final IGuiaActividadRepository guiaActividadRepository;
    private final IDisponibilidadGuiaRepository disponibilidadGuiaRepository;
    private final IEstadoService estadoService;

    @Override
    @Transactional
    public ReservaSalida crear(ReservaGuardar dto) {
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("El cliente indicado no existe."));
        Guia guia = guiaRepository.findById(dto.getIdGuia())
                .orElseThrow(() -> new RecursoNoEncontradoException("El guía indicado no existe."));
        DestinoActividad oferta = destinoActividadRepository.findById(dto.getIdDestinoActividad())
                .orElseThrow(() -> new RecursoNoEncontradoException("La actividad del destino no existe."));

        validarOfertaActiva(oferta);
        validarGuiaCompatible(guia.getId(), oferta);

        LocalTime horaFin = dto.getHoraInicio().plusMinutes(oferta.getDuracionMinutos());
        validarHorarioDelDia(dto.getHoraInicio(), horaFin);
        validarGuiaDisponible(guia.getId(), dto.getFechaRecorrido(), dto.getHoraInicio(), horaFin, null);

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setGuia(guia);
        reserva.setDestinoActividad(oferta);
        reserva.setFechaRecorrido(dto.getFechaRecorrido());
        reserva.setHoraInicio(dto.getHoraInicio());
        reserva.setHoraFin(horaFin);
        reserva.setCantidadPersonas(dto.getCantidadPersonas());
        // Precio congelado: se calcula aquí y ya no depende de PrecioBase.
        reserva.setPrecioTotal(oferta.getPrecioBase()
                .multiply(BigDecimal.valueOf(dto.getCantidadPersonas())));
        reserva.setObservaciones(dto.getObservaciones());
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setEstado(estadoService.obtener(
                IEstadoService.ESTADO_PENDIENTE, IEstadoService.TIPO_RESERVA));

        return ReservaSalida.desde(reservaRepository.save(reserva));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaSalida obtenerPorId(Integer id) {
        return ReservaSalida.desde(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaSalida> listarPorCliente(Integer idCliente, int pagina, int tamano) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado.");
        }
        return reservaRepository
                .findByCliente_IdCliente(idCliente, Paginacion.de(pagina, tamano, "fechaRecorrido", "horaInicio"))
                .map(ReservaSalida::desde);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaSalida> listarPorGuia(Integer idGuia, int pagina, int tamano) {
        if (!guiaRepository.existsById(idGuia)) {
            throw new RecursoNoEncontradoException("Guía no encontrado.");
        }
        return reservaRepository
                .findByGuia_Id(idGuia, Paginacion.de(pagina, tamano, "fechaRecorrido", "horaInicio"))
                .map(ReservaSalida::desde);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaSalida> listar(int pagina, int tamano) {
        return reservaRepository
                .findAll(Paginacion.de(pagina, tamano, "idReserva"))
                .map(ReservaSalida::desde);
    }

    @Override
    @Transactional
    public ReservaSalida cambiarEstado(Integer id, CambioEstadoModificar dto) {
        Reserva reserva = buscar(id);
        Estado nuevoEstado = estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_RESERVA);
        String actual = reserva.getEstado().getNombreEstado();

        if (esFinal(actual)) {
            throw new ConflictoException("La reserva ya está " + actual.toLowerCase() + " y no puede cambiar de estado.");
        }
        // Cancelar tiene sus propias reglas y su propio endpoint.
        if (IEstadoService.ESTADO_CANCELADA.equals(nuevoEstado.getNombreEstado())) {
            throw new SolicitudInvalidaException("Para cancelar una reserva debe usarse la operación de cancelación.");
        }

        reserva.setEstado(nuevoEstado);
        return ReservaSalida.desde(reservaRepository.save(reserva));
    }

    @Override
    @Transactional
    public ReservaSalida cancelar(Integer id) {
        Reserva reserva = buscar(id);
        String actual = reserva.getEstado().getNombreEstado();

        if (esFinal(actual)) {
            throw new ConflictoException("La reserva ya está " + actual.toLowerCase() + ".");
        }
        if (yaComenzo(reserva)) {
            throw new ConflictoException("No se puede cancelar una reserva cuyo recorrido ya comenzó.");
        }

        reserva.setEstado(estadoService.obtener(
                IEstadoService.ESTADO_CANCELADA, IEstadoService.TIPO_RESERVA));
        return ReservaSalida.desde(reservaRepository.save(reserva));
    }

    private Reserva buscar(Integer id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada."));
    }

    private static boolean esFinal(String nombreEstado) {
        return IEstadoService.ESTADO_COMPLETADA.equals(nombreEstado)
                || IEstadoService.ESTADO_CANCELADA.equals(nombreEstado);
    }

    private static boolean yaComenzo(Reserva reserva) {
        return reserva.getFechaRecorrido().atTime(reserva.getHoraInicio()).isBefore(LocalDateTime.now());
    }

    private static void validarOfertaActiva(DestinoActividad oferta) {
        if (!IEstadoService.ESTADO_ACTIVO.equals(oferta.getEstado().getNombreEstado())) {
            throw new ConflictoException("Esa actividad no está disponible para reservar.");
        }
    }

    // El guía debe trabajar en ese destino y realizar esa actividad (ambas asignaciones activas).
    private void validarGuiaCompatible(Integer idGuia, DestinoActividad oferta) {
        if (!guiaDestinoRepository.existsByGuia_IdAndDestino_IdAndEstado_NombreEstado(
                idGuia, oferta.getDestino().getId(), IEstadoService.ESTADO_ACTIVO)) {
            throw new ConflictoException("El guía no atiende ese destino.");
        }
        if (!guiaActividadRepository.existsByGuia_IdAndActividad_IdAndEstado_NombreEstado(
                idGuia, oferta.getActividad().getId(), IEstadoService.ESTADO_ACTIVO)) {
            throw new ConflictoException("El guía no realiza esa actividad.");
        }
    }

    private static void validarHorarioDelDia(LocalTime horaInicio, LocalTime horaFin) {
        if (!horaInicio.isBefore(horaFin)) {
            throw new SolicitudInvalidaException("La actividad no puede extenderse más allá del final del día.");
        }
    }

    private void validarGuiaDisponible(Integer idGuia, LocalDate fecha,
                                       LocalTime horaInicio, LocalTime horaFin, Integer idExcluir) {
        if (!disponibilidadGuiaRepository.cubreHorario(
                idGuia, fecha, horaInicio, horaFin, IEstadoService.ESTADO_ACTIVO)) {
            throw new ConflictoException("El guía no tiene disponibilidad registrada para ese horario.");
        }
        if (reservaRepository.existeSolapamiento(
                idGuia, fecha, horaInicio, horaFin, ESTADOS_VIGENTES, idExcluir)) {
            throw new ConflictoException("El guía ya tiene otra reserva en ese horario.");
        }
    }
}