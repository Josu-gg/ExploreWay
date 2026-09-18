package org.esfe.Controladores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.rol.RolGuardar;
import org.esfe.DTOs.rol.RolModificar;
import org.esfe.DTOs.rol.RolSalida;
import org.esfe.Servicios.Interfaces.IRolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final IRolService rolService;

    @GetMapping
    public ResponseEntity<List<RolSalida>> listar() {
        return ResponseEntity.ok(rolService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolSalida> buscarPorId(@PathVariable Integer id) {
        return rolService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RolSalida> guardar(@Valid @RequestBody RolGuardar dto) {
        RolSalida guardado = rolService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolSalida> modificar(@PathVariable Integer id, @Valid @RequestBody RolModificar dto) {
        return rolService.modificar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (rolService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
