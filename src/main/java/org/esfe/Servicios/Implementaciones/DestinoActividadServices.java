package org.esfe.Servicios.Implementaciones;

import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.destinoactividad.DestinoActividadGuardar;
import org.esfe.DTOs.destinoactividad.DestinoActividadModificar;
import org.esfe.DTOs.destinoactividad.DestinoActividadSalida;
import org.esfe.Excepciones.ConflictoException;
import org.esfe.Excepciones.RecursoNoEncontradoException;
import org.esfe.Modelos.Actividad;
import org.esfe.Modelos.Destino;
import org.esfe.Modelos.DestinoActividad;
import org.esfe.Repositorios.IActividadRepository;
import org.esfe.Repositorios.IDestinoActividadRepository;
import org.esfe.Repositorios.IDestinoRepository;
import org.esfe.Servicios.Interfaces.IDestinoActividadService;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.esfe.Utilidades.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinoActividadServices implements IDestinoActividadService {

    private static final String ESTADO_ACTIVO = "Activo";

    private final IDestinoActividadRepository destinoActividadRepository;
    private final IDestinoRepository destinoRepository;
    private final IActividadRepository actividadRepository;
    private final IEstadoService estadoService;

    @Override
    @Transactional(readOnly = true)
    public List<DestinoActividadSalida> listarDisponiblesPorDestino(Integer idDestino) {
        // 404 si el destino no existe; lista vacía si existe pero no tiene actividades activas.
        if (!destinoRepository.existsById(idDestino)) {
            throw new RecursoNoEncontradoException("Destino no encontrado.");
        }
        return destinoActividadRepository.buscarPorDestinoYEstado(idDestino, ESTADO_ACTIVO)
                .stream()
                .map(DestinoActividadSalida::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DestinoActividadSalida obtenerPorId(Integer id) {
        return DestinoActividadSalida.desde(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DestinoActividadSalida> listar(int pagina, int tamano) {
        return destinoActividadRepository
                .findAll(Paginacion.de(pagina, tamano, "idDestinoActividad"))
                .map(DestinoActividadSalida::desde);
    }

    @Override
    @Transactional
    public DestinoActividadSalida crear(DestinoActividadGuardar dto) {
        Destino destino = destinoRepository.findById(dto.getIdDestino())
                .orElseThrow(() -> new RecursoNoEncontradoException("El destino indicado no existe."));
        Actividad actividad = actividadRepository.findById(dto.getIdActividad())
                .orElseThrow(() -> new RecursoNoEncontradoException("La actividad indicada no existe."));

        if (destinoActividadRepository.existsByDestino_IdAndActividad_Id(destino.getId(), actividad.getId())) {
            throw new ConflictoException("Esa actividad ya está asignada a ese destino.");
        }

        DestinoActividad destinoActividad = new DestinoActividad();
        destinoActividad.setDestino(destino);
        destinoActividad.setActividad(actividad);
        destinoActividad.setDuracionMinutos(dto.getDuracionMinutos());
        destinoActividad.setPrecioBase(dto.getPrecioBase());
        destinoActividad.setEstado(estadoService.obtenerActivo(IEstadoService.TIPO_GENERAL));

        // saveAndFlush: si dos peticiones iguales llegan a la vez, UQ_DestinoActividad falla aquí
        // y el GlobalExceptionHandler responde 409.
        return DestinoActividadSalida.desde(destinoActividadRepository.saveAndFlush(destinoActividad));
    }

    @Override
    @Transactional
    public DestinoActividadSalida modificar(Integer id, DestinoActividadModificar dto) {
        DestinoActividad destinoActividad = buscar(id);

        destinoActividad.setDuracionMinutos(dto.getDuracionMinutos());
        destinoActividad.setPrecioBase(dto.getPrecioBase());
        // Solo estados de tipo "General": evita asignar estados de Reserva, Pago, Usuario, etc.
        destinoActividad.setEstado(estadoService.obtenerDeTipo(dto.getIdEstado(), IEstadoService.TIPO_GENERAL));

        return DestinoActividadSalida.desde(destinoActividadRepository.save(destinoActividad));
    }

    private DestinoActividad buscar(Integer id) {
        return destinoActividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad del destino no encontrada."));
    }
}