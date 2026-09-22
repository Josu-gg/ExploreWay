package org.esfe.Controladores;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.comun.CambioEstadoModificar;
import org.esfe.DTOs.guiadestino.GuiaDestinoGuardar;
import org.esfe.DTOs.guiadestino.GuiaDestinoSalida;
import org.esfe.Servicios.Interfaces.IGuiaDestinoService;
import org.springframework.beans.factory.annotation.Autowired;
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
//   (Si más adelante el rol Guia gestiona sus propios destinos, el servicio debe comprobar
//    que idGuia corresponde al usuario autenticado, no confiar en el id que llega en la petición.)
@RestController
@RequestMapping("/api/guia-destinos")
@RequiredArgsConstructor
@Tag(name = "Destinos por guía", description = "Destinos en los que trabaja cada guía")
public class GuiaDestinoControlller {

    private final IGuiaDestinoService guiaDestinoService;

    @GetMapping("/guia/{idGuia}")
    @Operation(summary = "Listar los destinos activos en los que trabaja un guía")
    public List<GuiaDestinoSalida> listarPorGuia(@PathVariable Integer idGuia) {
        return guiaDestinoService.listarActivosPorGuia(idGuia);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener una asignación guía-destino por Id")
    public GuiaDestinoSalida obtenerPorId(@PathVariable Integer id) {
        return guiaDestinoService.obtenerPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar las asignaciones(paginado, cualquier estado)")
    public PagedModel<GuiaDestinoSalida> listar(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(guiaDestinoService.listar(page,size));
    }
    @PostMapping
    @Operation(summary = "Asignar un destino a un guía")
    public ResponseEntity<GuiaDestinoSalida> crear(@Valid @RequestBody GuiaDestinoGuardar dto) {
        GuiaDestinoSalida creada = guiaDestinoService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdGuiaDestino())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cambiar el estado de una asignación (suspender o reactivar)")
    public GuiaDestinoSalida modificar(@PathVariable Integer id,
                                       @Valid @RequestBody CambioEstadoModificar dto) {
        return guiaDestinoService.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una asignación guía-destino")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        guiaDestinoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
