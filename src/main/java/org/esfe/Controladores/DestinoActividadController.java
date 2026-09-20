package org.esfe.Controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.destinoactividad.DestinoActividadGuardar;
import org.esfe.DTOs.destinoactividad.DestinoActividadModificar;
import org.esfe.DTOs.destinoactividad.DestinoActividadSalida;
import org.esfe.Servicios.Interfaces.IDestinoActividadService;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// Solo delega en el servicio: aquí no hay lógica de negocio.
// No hay DELETE: las reservas referencian esta tabla (FK sin cascada); la baja es lógica vía estado.
// TODO (paso 7, seguridad):
//   - GET /destino/{idDestino} y GET /{id} -> públicos
//   - GET /, POST y PUT                    -> solo Administrador
@RestController
@RequestMapping("/api/destino-actividades")
@RequiredArgsConstructor
@Tag(name = "Actividades por destino", description = "Duración, precio y dificultad de cada actividad según el destino")
public class DestinoActividadController {

    private final IDestinoActividadService destinoActividadService;

    @GetMapping("/destino/{idDestino}")
    @Operation(summary = "Listar las actividades disponibles de un destino")
    public List<DestinoActividadSalida> listarPorDestino(@PathVariable Integer idDestino) {
        return destinoActividadService.listarDisponiblesPorDestino(idDestino);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una actividad de destino por Id")
    public DestinoActividadSalida obtenerPorId(@PathVariable Integer id) {
        return destinoActividadService.obtenerPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar todas las actividades por destino (paginado, cualquier estado)")
    public PagedModel<DestinoActividadSalida> listar(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(destinoActividadService.listar(page, size));
    }

    @PostMapping
    @Operation(summary = "Asignar una actividad a un destino con su duración y precio base")
    public ResponseEntity<DestinoActividadSalida> crear(@Valid @RequestBody DestinoActividadGuardar dto) {
        DestinoActividadSalida creada = destinoActividadService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdDestinoActividad())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar duración, precio base y estado")
    public DestinoActividadSalida modificar(@PathVariable Integer id,
                                            @Valid @RequestBody DestinoActividadModificar dto) {
        return destinoActividadService.modificar(id, dto);
    }
}