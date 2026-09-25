package org.esfe.Controladores;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.auth.LoginGuardar;
import org.esfe.DTOs.auth.TokenSalida;
import org.esfe.DTOs.cliente.ClienteGuardar;
import org.esfe.DTOs.cliente.ClienteSalida;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Servicios.Interfaces.IAuthService;
import org.esfe.Servicios.Interfaces.IClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro de clientes, inicio de sesión y usuario autenticado")
public class AuthController {
    private final IAuthService authService;
    private final IClienteService clienteService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar un cliente (el rol Cliente lo asigna el servidor)")
    public ResponseEntity<ClienteSalida> registrar(@Valid @RequestBody ClienteGuardar dto) {
        ClienteSalida creado = clienteService.registrar(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/clientes/{id}")
                .buildAndExpand(creado.getIdCliente())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión con correo y contraseña")
    public TokenSalida login(@Valid @RequestBody LoginGuardar dto) {
        return authService.login(dto);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener el usuario autenticado")
    public UsuarioSalida me(Authentication authentication) {
        return authService.usuarioActual(authentication.getName());
    }
}
