package org.esfe.Controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.comun.CambioEstadoModificar;
import org.esfe.DTOs.reserva.ReservaGuardar;
import org.esfe.DTOs.reserva.ReservaSalida;
import org.esfe.Servicios.Interfaces.IReservaService;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Reservas de actividades con guía")
public class ReservaController {

    private final IReservaService reservaService;

    @PostMapping
    @Operation(summary = "Crear una reserva")
    public ResponseEntity<ReservaSalida> crear(@Valid @RequestBody ReservaGuardar dto) {
        ReservaSalida creada = reservaService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdReserva())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle de una reserva")
    public ReservaSalida obtenerPorId(@PathVariable Integer id) {
        return reservaService.obtenerPorId(id);
    }

    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "Listar las reservas de un cliente (paginado)")
    public PagedModel<ReservaSalida> listarPorCliente(@PathVariable Integer idCliente,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(reservaService.listarPorCliente(idCliente, page, size));
    }

    @GetMapping("/guia/{idGuia}")
    @Operation(summary = "Listar las reservas asignadas a un guía (paginado)")
    public PagedModel<ReservaSalida> listarPorGuia(@PathVariable Integer idGuia,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(reservaService.listarPorGuia(idGuia, page, size));
    }

    @GetMapping
    @Operation(summary = "Listar todas las reservas (paginado, cualquier estado)")
    public PagedModel<ReservaSalida> listar(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(reservaService.listar(page, size));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Confirmar o completar una reserva")
    public ReservaSalida cambiarEstado(@PathVariable Integer id,
                                       @Valid @RequestBody CambioEstadoModificar dto) {
        return reservaService.cambiarEstado(id, dto);
    }

    @PatchMapping("/{id}/cancelacion")
    @Operation(summary = "Cancelar una reserva")
    public ReservaSalida cancelar(@PathVariable Integer id) {
        return reservaService.cancelar(id);
    }
}