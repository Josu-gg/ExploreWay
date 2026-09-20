package org.esfe.Controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.guiaactividad.GuiaActividadGuardar;
import org.esfe.DTOs.guiaactividad.GuiaActividadModificar;
import org.esfe.DTOs.guiaactividad.GuiaActividadSalida;
import org.esfe.Servicios.Interfaces.IGuiaActividadService;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
// TODO (paso 7, seguridad):
//   - GET /guia/{idGuia} y GET /{id} -> públicos
//   - GET /, POST, PUT y DELETE      -> solo Administrador
//   (Si más adelante el rol Guia gestiona sus propias actividades, el servicio debe comprobar
//    que idGuia corresponde al usuario autenticado, no confiar en el id que llega en la petición.)
@RestController
@RequestMapping("/api/guia-actividades")
@RequiredArgsConstructor
@Tag(name = "Actividades por guía", description = "Actividades que cada guía está capacitado para realizar")
public class GuiaActividadController {

    private final IGuiaActividadService guiaActividadService;

    @GetMapping("/guia/{idGuia}")
    @Operation(summary = "Listar las actividades activas que realiza un guía")
    public List<GuiaActividadSalida> listarPorGuia(@PathVariable Integer idGuia) {
        return guiaActividadService.listarActivasPorGuia(idGuia);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una asignación guía-actividad por Id")
    public GuiaActividadSalida obtenerPorId(@PathVariable Integer id) {
        return guiaActividadService.obtenerPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar todas las asignaciones (paginado, cualquier estado)")
    public PagedModel<GuiaActividadSalida> listar(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(guiaActividadService.listar(page, size));
    }

    @PostMapping
    @Operation(summary = "Asignar una actividad a un guía")
    public ResponseEntity<GuiaActividadSalida> crear(@Valid @RequestBody GuiaActividadGuardar dto) {
        GuiaActividadSalida creada = guiaActividadService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdGuiaActividad())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cambiar el estado de una asignación (suspender o reactivar)")
    public GuiaActividadSalida modificar(@PathVariable Integer id,
                                         @Valid @RequestBody GuiaActividadModificar dto) {
        return guiaActividadService.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una asignación guía-actividad")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        guiaActividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}