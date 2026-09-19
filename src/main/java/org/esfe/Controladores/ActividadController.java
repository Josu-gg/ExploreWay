package org.esfe.Controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.actividad.ActividadGuardar;
import org.esfe.DTOs.actividad.ActividadModificar;
import org.esfe.DTOs.actividad.ActividadSalida;
import org.esfe.Servicios.Interfaces.IActividadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
public class ActividadController {
    private final IActividadService actividadService;

    @GetMapping
    public ResponseEntity<List<ActividadSalida>> listar() {
        return ResponseEntity.ok(actividadService.listar());
    }

    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<ActividadSalida>> listarPorEstado(@PathVariable Integer idEstado) {
        return ResponseEntity.ok(actividadService.listarPorEstado(idEstado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadSalida> buscarPorId(@PathVariable Integer id) {
        return actividadService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ActividadSalida> guardar(@Valid @RequestBody ActividadGuardar dto) {
        ActividadSalida guardada = actividadService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActividadSalida> modificar(@PathVariable Integer id, @Valid @RequestBody ActividadModificar dto) {
        return actividadService.modificar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (actividadService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
