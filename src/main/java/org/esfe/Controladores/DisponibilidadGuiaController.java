package org.esfe.Controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaGuardar;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaModificar;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaSalida;
import org.esfe.Servicios.Interfaces.IDisponibilidadGuiaService;
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

@RestController
@RequestMapping("/api/guia-disponibilidades")
@RequiredArgsConstructor
@Tag(name = "Disponibilidad de guías", description = "Franjas horarias en las que cada guía puede atender recorridos")
public class DisponibilidadGuiaController {

    private final IDisponibilidadGuiaService disponibilidadGuiaService;

    @GetMapping("/guia/{idGuia}")
    @Operation(summary = "Listar las franjas vigentes de un guía (activas, desde hoy, paginado)")
    public PagedModel<DisponibilidadGuiaSalida> listarPorGuia(@PathVariable Integer idGuia,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(disponibilidadGuiaService.listarVigentesPorGuia(idGuia, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una franja de disponibilidad por Id")
    public DisponibilidadGuiaSalida obtenerPorId(@PathVariable Integer id) {
        return disponibilidadGuiaService.obtenerPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar todas las franjas (paginado, cualquier estado y fecha)")
    public PagedModel<DisponibilidadGuiaSalida> listar(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(disponibilidadGuiaService.listar(page, size));
    }

    @PostMapping
    @Operation(summary = "Registrar una franja de disponibilidad para un guía")
    public ResponseEntity<DisponibilidadGuiaSalida> crear(@Valid @RequestBody DisponibilidadGuiaGuardar dto) {
        DisponibilidadGuiaSalida creada = disponibilidadGuiaService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdDisponibilidad())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar fecha, horario y estado de una franja")
    public DisponibilidadGuiaSalida modificar(@PathVariable Integer id,
                                              @Valid @RequestBody DisponibilidadGuiaModificar dto) {
        return disponibilidadGuiaService.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una franja de disponibilidad")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        disponibilidadGuiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}