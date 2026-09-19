package org.esfe.Controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.destino.DestinoGuardar;
import org.esfe.DTOs.destino.DestinoModificar;
import org.esfe.DTOs.destino.DestinoSalida;
import org.esfe.Servicios.Interfaces.IDestinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinos")
@RequiredArgsConstructor
public class DestinoController {
    private final IDestinoService destinoService;

    @GetMapping
    public ResponseEntity<List<DestinoSalida>> listar() {
        return ResponseEntity.ok(destinoService.listar());
    }

    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<DestinoSalida>> listarPorEstado(@PathVariable Integer idEstado) {
        return ResponseEntity.ok(destinoService.listarPorEstado(idEstado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinoSalida> buscarPorId(@PathVariable Integer id) {
        return destinoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DestinoSalida> guardar(@Valid @RequestBody DestinoGuardar dto) {
        DestinoSalida guardado = destinoService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinoSalida> modificar(@PathVariable Integer id, @Valid @RequestBody DestinoModificar dto) {
        return destinoService.modificar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (destinoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
