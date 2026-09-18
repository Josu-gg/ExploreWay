package org.esfe.Controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.cliente.ClienteSalida;
import org.esfe.DTOs.cliente.RegistroCliente;
import org.esfe.Servicios.Interfaces.IClienteService;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

// Solo delega en el servicio: aquí no hay lógica de negocio.
// TODO (paso 7, seguridad):
//   - POST /registro  -> público (permitAll)
//   - GET  /{id} y GET / -> solo Administrador
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Registro y consulta de clientes")
public class ClienteController {

    private final IClienteService clienteService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar un cliente (crea Persona, Usuario con rol Cliente y Cliente)")
    public ResponseEntity<ClienteSalida> registrar(@Valid @RequestBody RegistroCliente datos) {
        ClienteSalida creado = clienteService.registrar(datos);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/clientes/{id}")
                .buildAndExpand(creado.getIdCliente())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por Id")
    public ClienteSalida obtenerPorId(@PathVariable Integer id) {
        return clienteService.obtenerPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar clientes (paginado)")
    public PagedModel<ClienteSalida> listar(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(clienteService.listar(page, size));
    }
}