package org.esfe.Controladores;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.persona.PersonaGuardar;
import org.esfe.DTOs.persona.PersonaModificar;
import org.esfe.DTOs.persona.PersonaSalida;
import org.esfe.Servicios.Interfaces.IPersonaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController {
    private final IPersonaService personaService;

    @GetMapping
    public ResponseEntity<List<PersonaSalida>> listar() {
        return ResponseEntity.ok(personaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaSalida> buscarPorId(@PathVariable Integer id) {
        return personaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonaSalida> guardar(@Valid @RequestBody PersonaGuardar dto) {
        PersonaSalida creada = personaService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaSalida> modificar(@PathVariable Integer id, @Valid @RequestBody PersonaModificar dto) {
        return personaService.modificar(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        return personaService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
