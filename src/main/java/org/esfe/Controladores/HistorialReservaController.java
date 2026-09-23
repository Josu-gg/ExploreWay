package org.esfe.Controladores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.historialreserva.HistorialReservaSalida;
import org.esfe.Servicios.Interfaces.IHistorialReservaService;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historial-reservas")
@RequiredArgsConstructor
@Tag(name = "Historial de reservas", description = "Bitácora de cambios de las reservas")
public class HistorialReservaController {

    private final IHistorialReservaService historialReservaService;

    @GetMapping("/reserva/{idReserva}")
    @Operation(summary = "Listar los cambios de una reserva, del más antiguo al más reciente")
    public List<HistorialReservaSalida> listarPorReserva(@PathVariable Integer idReserva) {
        return historialReservaService.listarPorReserva(idReserva);
    }

    @GetMapping
    @Operation(summary = "Listar todos los cambios registrados (paginado)")
    public PagedModel<HistorialReservaSalida> listar(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return new PagedModel<>(historialReservaService.listar(page, size));
    }
}