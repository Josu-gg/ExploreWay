package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.esfe.Controladores.ReservaController;
import org.esfe.DTOs.reserva.ReservaGuardar;
import org.esfe.DTOs.reserva.ReservaSalida;
import org.esfe.Modelos.*;
import org.esfe.Servicios.Interfaces.IReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservaServicesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IReservaService reservaService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // CP-84: Crear nueva Reserva vía POST /api/reservas
    @Test
    void cp84_crearReserva_retorna201ConReservaCreada() throws Exception {
        ReservaGuardar dto = new ReservaGuardar();
        dto.setIdCliente(4);
        dto.setIdGuia(6);
        dto.setIdDestinoActividad(3);
        dto.setFechaRecorrido(LocalDate.of(2026, 11, 10));
        dto.setHoraInicio(LocalTime.of(8, 0));
        dto.setCantidadPersonas(2);
        dto.setObservaciones("Llevar repelente de insectos");

        ReservaSalida respuestaEsperada = construirReservaSalida(
                15, 4, "Juan", "Pérez", 6, "Marta", "Hernández",
                3, "Cerro Verde", "Senderismo",
                LocalDate.of(2026, 11, 10), LocalTime.of(8, 0), LocalTime.of(11, 0),
                2, new BigDecimal("51.00"), "Pendiente");

        when(reservaService.crear(any(ReservaGuardar.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/reservas/15")))
                .andExpect(jsonPath("$.idReserva").value(15))
                .andExpect(jsonPath("$.idCliente").value(4))
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.nombreDestino").value("Cerro Verde"))
                .andExpect(jsonPath("$.nombreActividad").value("Senderismo"))
                .andExpect(jsonPath("$.cantidadPersonas").value(2))
                .andExpect(jsonPath("$.precioTotal").value(51.00))
                .andExpect(jsonPath("$.nombreEstado").value("Pendiente"));
    }

    // CP-88: Consultar detalle de una Reserva vía GET /api/reservas/{id}
    @Test
    void cp88_obtenerDetalleReserva_retorna200ConDatosDeLaReserva() throws Exception {
        Integer idReserva = 15;

        ReservaSalida respuestaEsperada = construirReservaSalida(
                idReserva, 4, "Juan", "Pérez", 6, "Marta", "Hernández",
                3, "Cerro Verde", "Senderismo",
                LocalDate.of(2026, 11, 10), LocalTime.of(8, 0), LocalTime.of(11, 0),
                2, new BigDecimal("51.00"), "Pendiente");

        when(reservaService.obtenerPorId(idReserva)).thenReturn(respuestaEsperada);

        mockMvc.perform(get("/api/reservas/{id}", idReserva))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(idReserva))
                .andExpect(jsonPath("$.nombreCliente").value("Juan"))
                .andExpect(jsonPath("$.nombreGuia").value("Marta"))
                .andExpect(jsonPath("$.nombreDestino").value("Cerro Verde"))
                .andExpect(jsonPath("$.nombreActividad").value("Senderismo"))
                .andExpect(jsonPath("$.cantidadPersonas").value(2))
                .andExpect(jsonPath("$.precioTotal").value(51.00))
                .andExpect(jsonPath("$.nombreEstado").value("Pendiente"));
    }

    // CP-89: Listar Reservas por Cliente y por Guía (paginado)
    @Test
    void cp89_listarReservasPorCliente_retorna200ConPaginaDeReservas() throws Exception {
        Integer idCliente = 4;

        ReservaSalida reserva = construirReservaSalida(
                15, idCliente, "Juan", "Pérez", 6, "Marta", "Hernández",
                3, "Cerro Verde", "Senderismo",
                LocalDate.of(2026, 11, 10), LocalTime.of(8, 0), LocalTime.of(11, 0),
                2, new BigDecimal("51.00"), "Pendiente");

        Page<ReservaSalida> paginaEsperada = new PageImpl<>(
                List.of(reserva), PageRequest.of(0, 20), 1);

        when(reservaService.listarPorCliente(eq(idCliente), anyInt(), anyInt()))
                .thenReturn(paginaEsperada);

        mockMvc.perform(get("/api/reservas/cliente/{idCliente}", idCliente)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].idReserva").value(15))
                .andExpect(jsonPath("$.content[0].idCliente").value(idCliente))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }


    // CP-91: Cancelar una Reserva vía PATCH /api/reservas/{id}/cancelacion
    @Test
    void cp91_cancelarReserva_retorna200ConReservaCancelada() throws Exception {
        Integer idReserva = 15;

        ReservaSalida respuestaEsperada = construirReservaSalida(
                idReserva, 4, "Juan", "Pérez", 6, "Marta", "Hernández",
                3, "Cerro Verde", "Senderismo",
                LocalDate.of(2026, 11, 10), LocalTime.of(8, 0), LocalTime.of(11, 0),
                2, new BigDecimal("51.00"), "Cancelada");

        when(reservaService.cancelar(idReserva)).thenReturn(respuestaEsperada);

        mockMvc.perform(patch("/api/reservas/{id}/cancelacion", idReserva))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(idReserva))
                .andExpect(jsonPath("$.nombreEstado").value("Cancelada"));
    }

    // Único builder reutilizado por todos los tests de esta clase.
    private ReservaSalida construirReservaSalida(Integer idReserva,
                                                 Integer idCliente, String nombreCliente, String apellidoCliente,
                                                 Integer idGuia, String nombreGuia, String apellidoGuia,
                                                 Integer idDestinoActividad, String nombreDestino, String nombreActividad,
                                                 LocalDate fechaRecorrido, LocalTime horaInicio, LocalTime horaFin,
                                                 Integer cantidadPersonas, BigDecimal precioTotal, String nombreEstado) {
        Estado estadoActivo = new Estado(1, "Activo", "General");
        Estado estadoReserva = new Estado(2, nombreEstado, "reservas");

        Persona personaCliente = new Persona();
        personaCliente.setId(40);
        personaCliente.setNombre(nombreCliente);
        personaCliente.setApellido(apellidoCliente);
        personaCliente.setTelefono("7000-0000");

        Cliente cliente = new Cliente();
        cliente.setIdCliente(idCliente);
        cliente.setPersona(personaCliente);
        cliente.setEstado(estadoActivo);

        Persona personaGuia = new Persona();
        personaGuia.setId(20);
        personaGuia.setNombre(nombreGuia);
        personaGuia.setApellido(apellidoGuia);

        Guia guia = new Guia();
        guia.setId(idGuia);
        guia.setPersona(personaGuia);
        guia.setEstado(estadoActivo);

        Destino destino = new Destino();
        destino.setId(7);
        destino.setNombre(nombreDestino);
        destino.setDepartamento("Santa Ana");
        destino.setEstado(estadoActivo);

        Actividad actividad = new Actividad();
        actividad.setId(9);
        actividad.setNombre(nombreActividad);
        actividad.setEstado(estadoActivo);

        DestinoActividad destinoActividad = new DestinoActividad();
        destinoActividad.setIdDestinoActividad(idDestinoActividad);
        destinoActividad.setDestino(destino);
        destinoActividad.setActividad(actividad);
        destinoActividad.setDuracionMinutos(180);
        destinoActividad.setPrecioBase(new BigDecimal("25.50"));
        destinoActividad.setEstado(estadoActivo);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(idReserva);
        reserva.setCliente(cliente);
        reserva.setGuia(guia);
        reserva.setDestinoActividad(destinoActividad);
        reserva.setEstado(estadoReserva);
        reserva.setFechaRecorrido(fechaRecorrido);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);
        reserva.setCantidadPersonas(cantidadPersonas);
        reserva.setPrecioTotal(precioTotal);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setObservaciones("Llevar repelente de insectos");

        return ReservaSalida.desde(reserva);
    }
}