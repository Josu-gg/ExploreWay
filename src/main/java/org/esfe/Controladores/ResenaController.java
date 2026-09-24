package org.esfe.Controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.resena.ResenaGuardar;
import org.esfe.DTOs.resena.ResenaModificar;
import org.esfe.DTOs.resena.ResenaSalida;
import org.esfe.Servicios.Interfaces.IResenaService;
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

// TODO (paso 7, seguridad):
//   - GET /guia/{idGuia} y GET /{id} -> públicos
//   - POST, PUT, GET /cliente/{idCliente} -> Cliente (solo sus propias reseñas)
//   - GET / y DELETE -> solo Administrador
@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Calificaciones de guías por reservas completadas")
public class ResenaController {

    private final IResenaService resenaService;

    @PostMapping
    @Operation(summary = "Calificar una reserva completada")
    public ResponseEntity<ResenaSalida> crear(@Valid @RequestBody ResenaGuardar dto) {
        ResenaSalida creada = resenaService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdResena())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reseña por Id")
    public ResenaSalida obtenerPorId(@PathVariable Integer id) {
        return resenaService.obtenerPorId(id);
    }

    @GetMapping("/guia/{idGuia}")
    @Operation(summary = "Listar las reseñas de un guía (paginado, más recientes primero)")
    public PagedModel<ResenaSalida> listarPorGuia(@PathVariable Integer idGuia,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(resenaService.listarPorGuia(idGuia, page, size));
    }

    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "Listar las reseñas hechas por un cliente (paginado)")
    public PagedModel<ResenaSalida> listarPorCliente(@PathVariable Integer idCliente,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(resenaService.listarPorCliente(idCliente, page, size));
    }

    @GetMapping
    @Operation(summary = "Listar todas las reseñas (paginado)")
    public PagedModel<ResenaSalida> listar(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(resenaService.listar(page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar la calificación o el comentario de una reseña")
    public ResenaSalida modificar(@PathVariable Integer id, @Valid @RequestBody ResenaModificar dto) {
        return resenaService.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una reseña")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}