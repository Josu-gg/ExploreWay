package org.esfe.Controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.estado.EstadoGuardar;
import org.esfe.DTOs.estado.EstadoModificar;
import org.esfe.DTOs.estado.EstadoSalida;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
@RequiredArgsConstructor
public class EstadoController {

    private final IEstadoService estadoService;

    @GetMapping
    public ResponseEntity<List<EstadoSalida>> listar() {
        return ResponseEntity.ok(estadoService.listar());
    }

    @GetMapping("/tipo/{tipoEstado}")
    public ResponseEntity<List<EstadoSalida>> listarPorTipo(@PathVariable String tipoEstado) {
        return ResponseEntity.ok(estadoService.listarPorTipos(tipoEstado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoSalida> buscarPorId(@PathVariable Integer id) {
        return estadoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoSalida> guardar(@Valid @RequestBody EstadoGuardar dto) {
        EstadoSalida guardado = estadoService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoSalida> modificar(@PathVariable Integer id, @Valid @RequestBody EstadoModificar dto) {
        return estadoService.modificar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (estadoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
