package org.esfe.Controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.usuario.UsuarioGuardar;
import org.esfe.DTOs.usuario.UsuarioModificar;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
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

// Solo delega en el servicio: aquí no hay lógica de negocio.
// TODO (paso 7, seguridad): restringir este controlador al rol Administrador.
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Administración de usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear un usuario (Persona + Usuario)")
    public ResponseEntity<UsuarioSalida> crear(@Valid @RequestBody UsuarioGuardar dto) {
        UsuarioSalida creado = usuarioService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getIdUsuario())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un usuario (datos personales, correo, rol y estado)")
    public UsuarioSalida modificar(@PathVariable Integer id, @Valid @RequestBody UsuarioModificar dto) {
        return usuarioService.modificar(id, dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por Id")
    public UsuarioSalida obtenerPorId(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios (paginado)")
    public PagedModel<UsuarioSalida> listar(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(usuarioService.listar(page, size));
    }
}