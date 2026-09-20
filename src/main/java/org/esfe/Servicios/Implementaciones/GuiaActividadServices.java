package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.guiaactividad.GuiaActividadGuardar;
import org.esfe.DTOs.guiaactividad.GuiaActividadModificar;
import org.esfe.DTOs.guiaactividad.GuiaActividadSalida;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Actividad;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.GuiaActividad;
import org.esfe.Repositorios.IActividadRepository;
import org.esfe.Repositorios.IGuiaActividadRepository;
import org.esfe.Repositorios.IGuiaRepository;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Servicios.Interfaces.IGuiaActividadService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuiaActividadServices implements IGuiaActividadService {

    private final IGuiaActividadRepository guiaActividadRepository;
    private final IGuiaRepository guiaRepository;
    private final IActividadRepository actividadRepository;
    private final IEstadoService estadoService;

    @Override
    @Transactional(readOnly = true)
    public List<GuiaActividadSalida> listarActivasPorGuia(Integer idGuia) {
        // 404 si el guía no existe; lista vacía si existe pero no tiene actividades activas.
        if (!guiaRepository.existsById(idGuia)) {
            throw new RecursoNoEncontradoException("Guía no encontrado.");
        }
        return guiaActividadRepository.buscarPorGuiaYEstado(idGuia, IEstadoService.ESTADO_ACTIVO)
                .stream()
                .map(GuiaActividadSalida::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GuiaActividadSalida obtenerPorId(Integer id) {
        return GuiaActividadSalida.desde(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GuiaActividadSalida> listar(int pagina, int tamano) {
        return guiaActividadRepository
                .findAll(Paginacion.de(pagina, tamano, "idGuiaActividad"))
                .map(GuiaActividadSalida::desde);
    }

    @Override
    @Transactional
    public GuiaActividadSalida crear(GuiaActividadGuardar dto) {
        Guia guia = guiaRepository.findById(dto.getIdGuia())
                .orElseThrow(() -> new RecursoNoEncontradoException("El guía indicado no existe."));
        Actividad actividad = actividadRepository.findById(dto.getIdActividad())
                .orElseThrow(() -> new RecursoNoEncontradoException("La actividad indicada no existe."));

        if (guiaActividadRepository.existsByGuia_IdAndActividad_Id(guia.getId(), actividad.getId())) {
            throw new ConflictoException("El guía ya tiene asignada esa actividad.");
        }

        GuiaActividad guiaActividad = new GuiaActividad();
        guiaActividad.setGuia(guia);
        guiaActividad.setActividad(actividad);
        guiaActividad.setEstado(estadoService.obtenerActivo(IEstadoService.TIPO_GENERAL));

        // saveAndFlush: ante dos peticiones iguales simultáneas, UQ_GuiaActividad falla aquí -> 409.
        return GuiaActividadSalida.desde(guiaActividadRepository.saveAndFlush(guiaActividad));
    }

    @Override
    @Transactional
    public GuiaActividadSalida modificar(Integer id, GuiaActividadModificar dto) {
        GuiaActividad guiaActividad = buscar(id);
        // Solo estados de tipo "General": evita asignar estados de Reserva, Pago, Usuario, etc.
        guiaActividad.setEstado(estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_GENERAL));
        return GuiaActividadSalida.desde(guiaActividadRepository.save(guiaActividad));
    }

    // Borrado físico permitido: ninguna otra tabla referencia GuiaActividad.
    // Las reservas guardan IdGuia e IdDestinoActividad, no esta asignación.
    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!guiaActividadRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Asignación de actividad no encontrada.");
        }
        guiaActividadRepository.deleteById(id);
    }

    private GuiaActividad buscar(Integer id) {
        return guiaActividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación de actividad no encontrada."));
    }
}