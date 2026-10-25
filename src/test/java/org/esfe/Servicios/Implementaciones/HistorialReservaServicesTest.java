package org.esfe.Servicios.Implementaciones;

import org.esfe.Controladores.HistorialReservaController;
import org.esfe.DTOs.historialreserva.HistorialReservaSalida;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.HistorialReserva;
import org.esfe.Modelos.Persona;
import org.esfe.Modelos.Reserva;
import org.esfe.Modelos.Usuario;
import org.esfe.Servicios.Interfaces.IHistorialReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistorialReservaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HistorialReservaServicesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IHistorialReservaService historialReservaService;

    // CP-95: Consultar historial y auditoría de Reservas vía GET /api/historial-reservas/reserva/{idReserva}
    @Test
    void cp95_listarHistorialPorReserva_retorna200ConCambiosOrdenados() throws Exception {
        Integer idReserva = 15;

        HistorialReservaSalida creacion = construirHistorialSalida(
                1, idReserva, null, 6, "Marta", "Hernández",
                "Reserva creada.", LocalDateTime.of(2026, 11, 1, 10, 0), 1);
        HistorialReservaSalida cambioEstado = construirHistorialSalida(
                2, idReserva, null, null, null, null,
                "Cambio de estado: Pendiente -> Aceptada", LocalDateTime.of(2026, 11, 2, 9, 0), 1);

        when(historialReservaService.listarPorReserva(eq(idReserva)))
                .thenReturn(List.of(creacion, cambioEstado));

        mockMvc.perform(get("/api/historial-reservas/reserva/{idReserva}", idReserva))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idHistorial").value(1))
                .andExpect(jsonPath("$[0].motivo").value("Reserva creada."))
                .andExpect(jsonPath("$[0].nombreGuiaNuevo").value("Marta Hernández"))
                .andExpect(jsonPath("$[1].idHistorial").value(2))
                .andExpect(jsonPath("$[1].motivo").value("Cambio de estado: Pendiente -> Aceptada"));
    }

    private HistorialReservaSalida construirHistorialSalida(Integer idHistorial, Integer idReserva,
                                                            Guia guiaAnterior, Integer idGuiaNuevo,
                                                            String nombreGuiaNuevo, String apellidoGuiaNuevo,
                                                            String motivo, LocalDateTime fechaCambio,
                                                            Integer idUsuarioResponsable) {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(idReserva);

        Guia guiaNuevo = null;
        if (idGuiaNuevo != null) {
            Persona persona = new Persona();
            persona.setId(20);
            persona.setNombre(nombreGuiaNuevo);
            persona.setApellido(apellidoGuiaNuevo);

            guiaNuevo = new Guia();
            guiaNuevo.setId(idGuiaNuevo);
            guiaNuevo.setPersona(persona);
        }

        Usuario responsable = null;
        if (idUsuarioResponsable != null) {
            responsable = new Usuario();
            responsable.setIdUsuario(idUsuarioResponsable);
        }

        HistorialReserva historial = new HistorialReserva();
        historial.setIdHistorial(idHistorial);
        historial.setReserva(reserva);
        historial.setGuiaAnterior(guiaAnterior);
        historial.setGuiaNuevo(guiaNuevo);
        historial.setMotivo(motivo);
        historial.setFechaCambio(fechaCambio);
        historial.setUsuarioResponsable(responsable);

        return HistorialReservaSalida.desde(historial);
    }
}