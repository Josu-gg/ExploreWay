package org.esfe.Servicios.Interfaces;

import org.esfe.DTOs.estado.EstadoGuardar;
import org.esfe.DTOs.estado.EstadoModificar;
import org.esfe.DTOs.estado.EstadoSalida;
import org.esfe.Modelos.Estado;

import java.util.List;
import java.util.Optional;

public interface IEstadoService {

    String TIPO_GENERAL = "General";
    String TIPO_USUARIO = "Usuario";
    String ESTADO_ACTIVO = "Activo";
    String TIPO_RESERVA = "Reserva";
    String ESTADO_PENDIENTE = "Pendiente";
    String ESTADO_CONFIRMADA = "Confirmada";
    String ESTADO_COMPLETADA = "Completada";
    String ESTADO_CANCELADA = "Cancelada";

    // ── CRUD expuesto por EstadoController ────────────────────
    List<EstadoSalida> listar();

    List<EstadoSalida> listarPorTipos(String tipoEstado);

    Optional<EstadoSalida> buscarPorId(Integer id);

    EstadoSalida guardar(EstadoGuardar dto);

    Optional<EstadoSalida> modificar(Integer id, EstadoModificar dto);

    boolean eliminar(Integer id);

    // ── Uso interno entre servicios ───────────────────────────
    // Los estados se buscan por nombre + tipo (no por Id fijo) porque en la tabla Estado
    // hay varios "Activo" (General, Usuario, Disponibilidad).
    Estado obtener(String nombreEstado, String tipoEstado);

    Estado obtenerActivo(String tipoEstado);

    // Estado elegido por el cliente de la API: debe existir y ser del tipo indicado.
    Estado obtenerDeTipo(Integer idEstado, String tipoEstado);
}
