package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaDatos;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaGuardar;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaModificar;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaSalida;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Excepciones.SolicitudInvalidaException;
import org.esfe.Modelos.DisponibilidadGuia;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Guia;
import org.esfe.Repositorios.IDisponibilidadGuiaRepository;
import org.esfe.Repositorios.IGuiaRepository;
import org.esfe.Servicios.Interfaces.IDisponibilidadGuiaService;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DisponibilidadGuiaServices implements IDisponibilidadGuiaService {

    private final IDisponibilidadGuiaRepository disponibilidadGuiaRepository;
    private final IGuiaRepository guiaRepository;
    private final IEstadoService estadoService;

    @Override
    @Transactional(readOnly = true)
    public Page<DisponibilidadGuiaSalida> listarVigentesPorGuia(Integer idGuia, int pagina, int tamano) {
        // 404 si el guía no existe; página vacía si existe pero no tiene franjas vigentes.
        if (!guiaRepository.existsById(idGuia)) {
            throw new RecursoNoEncontradoException("Guía no encontrado.");
        }
        return disponibilidadGuiaRepository
                .findByGuia_IdAndEstado_NombreEstadoAndFechaGreaterThanEqual(
                        idGuia,
                        IEstadoService.ESTADO_ACTIVO,
                        LocalDate.now(),
                        Paginacion.de(pagina, tamano, "fecha", "horaInicio"))
                .map(DisponibilidadGuiaSalida::desde);
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadGuiaSalida obtenerPorId(Integer id) {
        return DisponibilidadGuiaSalida.desde(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisponibilidadGuiaSalida> listar(int pagina, int tamano) {
        return disponibilidadGuiaRepository
                .findAll(Paginacion.de(pagina, tamano, "idDisponibilidad"))
                .map(DisponibilidadGuiaSalida::desde);
    }

    @Override
    @Transactional
    public DisponibilidadGuiaSalida crear(DisponibilidadGuiaGuardar dto) {
        validarHorario(dto);

        Guia guia = guiaRepository.findById(dto.getIdGuia())
                .orElseThrow(() -> new RecursoNoEncontradoException("El guía indicado no existe."));

        validarFranjaExacta(guia.getId(), dto);
        validarSinSolapamiento(guia.getId(), dto, null);

        DisponibilidadGuia disponibilidad = new DisponibilidadGuia();
        disponibilidad.setGuia(guia);
        asignarHorario(disponibilidad, dto);
        disponibilidad.setEstado(estadoService.obtenerActivo(IEstadoService.TIPO_GENERAL));

        // saveAndFlush: ante dos peticiones iguales simultáneas, UQ_DisponibilidadGuia falla aquí -> 409.
        return DisponibilidadGuiaSalida.desde(disponibilidadGuiaRepository.saveAndFlush(disponibilidad));
    }

    @Override
    @Transactional
    public DisponibilidadGuiaSalida modificar(Integer id, DisponibilidadGuiaModificar dto) {
        DisponibilidadGuia disponibilidad = buscar(id);
        validarHorario(dto);

        // Solo estados de tipo "General": evita asignar estados de Reserva, Usuario, etc.
        Estado nuevoEstado = estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_GENERAL);
        Integer idGuia = disponibilidad.getGuia().getId();

        // Si el horario no cambió, la franja exacta es ella misma: no se consulta.
        if (cambioHorario(disponibilidad, dto)) {
            validarFranjaExacta(idGuia, dto);
        }

        if (IEstadoService.ESTADO_ACTIVO.equals(nuevoEstado.getNombreEstado())) {
            validarSinSolapamiento(idGuia, dto, id);
        }

        // TODO (Reserva): si cambia el horario o se desactiva, verificar que no deje reservas
        // activas fuera de la disponibilidad del guía o disparar la reasignación.
        asignarHorario(disponibilidad, dto);
        disponibilidad.setEstado(nuevoEstado);

        return DisponibilidadGuiaSalida.desde(disponibilidadGuiaRepository.saveAndFlush(disponibilidad));
    }

    // Borrado físico permitido: ninguna otra tabla tiene FK hacia DisponibilidadGuia.
    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!disponibilidadGuiaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Disponibilidad no encontrada.");
        }
        // TODO (Reserva): impedir el borrado si hay reservas activas del guía dentro de esta franja.
        disponibilidadGuiaRepository.deleteById(id);
    }

    private DisponibilidadGuia buscar(Integer id) {
        return disponibilidadGuiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Disponibilidad no encontrada."));
    }

    //  para responder 400 y no un error de BD.
    private static void validarHorario(DisponibilidadGuiaDatos dto) {
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new SolicitudInvalidaException("La hora de inicio debe ser anterior a la hora de fin.");
        }
    }

    // Respaldo de UQ_DisponibilidadGuia aplica aunque la franja existente esté inactiva.
    private void validarFranjaExacta(Integer idGuia, DisponibilidadGuiaDatos dto) {
        if (disponibilidadGuiaRepository.existsByGuia_IdAndFechaAndHoraInicioAndHoraFin(
                idGuia, dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new ConflictoException("El guía ya tiene registrada esa misma franja horaria.");
        }
    }

    private void validarSinSolapamiento(Integer idGuia, DisponibilidadGuiaDatos dto, Integer idExcluir) {
        if (disponibilidadGuiaRepository.existeSolapamiento(
                idGuia, dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin(),
                IEstadoService.ESTADO_ACTIVO, idExcluir)) {
            throw new ConflictoException("La franja se cruza con otra disponibilidad activa del guía ese día.");
        }
    }

    private static boolean cambioHorario(DisponibilidadGuia actual, DisponibilidadGuiaDatos dto) {
        return !actual.getFecha().equals(dto.getFecha())
                || !actual.getHoraInicio().equals(dto.getHoraInicio())
                || !actual.getHoraFin().equals(dto.getHoraFin());
    }

    private static void asignarHorario(DisponibilidadGuia disponibilidad, DisponibilidadGuiaDatos dto) {
        disponibilidad.setFecha(dto.getFecha());
        disponibilidad.setHoraInicio(dto.getHoraInicio());
        disponibilidad.setHoraFin(dto.getHoraFin());
    }
}