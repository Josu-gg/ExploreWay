package org.esfe.Controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.guia.GuiaGuardar;
import org.esfe.DTOs.guia.GuiaModificar;
import org.esfe.DTOs.guia.GuiaSalida;
import org.esfe.Servicios.Interfaces.IGuiaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/guias")
@RequiredArgsConstructor
public class GuiaController {

    private final IGuiaService guiaService;

    @GetMapping
    public ResponseEntity<List<GuiaSalida>> listar() {
        return ResponseEntity.ok(guiaService.listar());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<GuiaSalida>> listarDisponibles() {
        return ResponseEntity.ok(guiaService.listarDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuiaSalida> buscarPorId(@PathVariable Integer id) {
        return guiaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/persona/{idPersona}")
    public ResponseEntity<GuiaSalida> buscarPorPersona(@PathVariable Integer idPersona) {
        return guiaService.buscarPorPersona(idPersona)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GuiaSalida> guardar(@Valid @RequestBody GuiaGuardar dto) {
        GuiaSalida guardado = guiaService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuiaSalida> modificar(@PathVariable Integer id, @Valid @RequestBody GuiaModificar dto) {
        return guiaService.modificar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (guiaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
