package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.esfe.Controladores.DisponibilidadGuiaController;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaGuardar;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaModificar;
import org.esfe.DTOs.disponibilidadguia.DisponibilidadGuiaSalida;
import org.esfe.Modelos.DisponibilidadGuia;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.Persona;
import org.esfe.Servicios.Interfaces.IDisponibilidadGuiaService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DisponibilidadGuiaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GuiaDisponibilidadTest {

    private static final Integer ID_ESTADO_ACTIVO = 1;
    private static final String NOMBRE_ESTADO_ACTIVO = "Activo";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDisponibilidadGuiaService disponibilidadGuiaService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // CP-77: Registrar disponibilidad / agenda de un Guía vía POST /api/guia-disponibilidades
    @Test
    void cp77_registrarDisponibilidad_retorna201ConFranjaCreada() throws Exception {
        DisponibilidadGuiaGuardar dto = new DisponibilidadGuiaGuardar();
        dto.setIdGuia(6);
        dto.setFecha(LocalDate.of(2026, 11, 10));
        dto.setHoraInicio(LocalTime.of(8, 0));
        dto.setHoraFin(LocalTime.of(14, 0));

        DisponibilidadGuiaSalida respuestaEsperada = construirDisponibilidadSalida(
                11, 6, "Marta", "Hernández",
                dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin());

        when(disponibilidadGuiaService.crear(any(DisponibilidadGuiaGuardar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/guia-disponibilidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/guia-disponibilidades/11")))
                .andExpect(jsonPath("$.idDisponibilidad").value(11))
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.nombreGuia").value("Marta"))
                .andExpect(jsonPath("$.fecha").value("2026-11-10"))
                .andExpect(jsonPath("$.horaInicio").value("08:00:00"))
                .andExpect(jsonPath("$.horaFin").value("14:00:00"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    // CP-80: Modificar horario de disponibilidad de un Guía vía PUT /api/guia-disponibilidades/{id}
    @Test
    void cp80_modificarHorarioDisponibilidad_retorna200ConHorarioActualizado() throws Exception {
        Integer idDisponibilidad = 11;

        DisponibilidadGuiaModificar dto = new DisponibilidadGuiaModificar();
        dto.setFecha(LocalDate.of(2026, 11, 10));
        dto.setHoraInicio(LocalTime.of(9, 0));
        dto.setHoraFin(LocalTime.of(15, 0));
        dto.setIdEstado(1);

        DisponibilidadGuiaSalida respuestaEsperada = construirDisponibilidadSalida(
                idDisponibilidad, 6, "Marta", "Hernández",
                dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin());

        when(disponibilidadGuiaService.modificar(eq(idDisponibilidad), any(DisponibilidadGuiaModificar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(put("/api/guia-disponibilidades/{id}", idDisponibilidad)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDisponibilidad").value(idDisponibilidad))
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.fecha").value("2026-11-10"))
                .andExpect(jsonPath("$.horaInicio").value("09:00:00"))
                .andExpect(jsonPath("$.horaFin").value("15:00:00"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    // CP-83: Listar disponibilidades de Guía (paginado) vía GET /api/guia-disponibilidades/guia/{idGuia}
    @Test
    void cp83_listarDisponibilidadesPorGuia_retorna200ConPaginaDeFranjas() throws Exception {
        Integer idGuia = 6;

        DisponibilidadGuiaSalida franja1 = construirDisponibilidadSalida(
                11, idGuia, "Marta", "Hernández",
                LocalDate.of(2026, 11, 10), LocalTime.of(8, 0), LocalTime.of(14, 0));
        DisponibilidadGuiaSalida franja2 = construirDisponibilidadSalida(
                12, idGuia, "Marta", "Hernández",
                LocalDate.of(2026, 11, 12), LocalTime.of(9, 0), LocalTime.of(13, 0));

        Page<DisponibilidadGuiaSalida> paginaEsperada = new PageImpl<>(
                List.of(franja1, franja2),
                PageRequest.of(0, 20),
                2);

        when(disponibilidadGuiaService.listarVigentesPorGuia(eq(idGuia), anyInt(), anyInt()))
                .thenReturn(paginaEsperada);

        mockMvc.perform(get("/api/guia-disponibilidades/guia/{idGuia}", idGuia)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].idDisponibilidad").value(11))
                .andExpect(jsonPath("$.content[0].idGuia").value(idGuia))
                .andExpect(jsonPath("$.content[1].idDisponibilidad").value(12))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    // Overload de conveniencia: usa el estado "Activo" por defecto (lo que necesitan CP-77, CP-80 y CP-83).
    private DisponibilidadGuiaSalida construirDisponibilidadSalida(Integer idDisponibilidad,
                                                                   Integer idGuia, String nombreGuia, String apellidoGuia,
                                                                   LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        return construirDisponibilidadSalida(idDisponibilidad, idGuia, nombreGuia, apellidoGuia,
                fecha, horaInicio, horaFin, ID_ESTADO_ACTIVO, NOMBRE_ESTADO_ACTIVO);
    }

    // Builder base: único lugar donde se arma el objeto DisponibilidadGuiaSalida.
    private DisponibilidadGuiaSalida construirDisponibilidadSalida(Integer idDisponibilidad,
                                                                   Integer idGuia, String nombreGuia, String apellidoGuia,
                                                                   LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                                                   Integer idEstado, String nombreEstado) {
        Estado estado = new Estado(idEstado, nombreEstado, "General");

        Persona persona = new Persona();
        persona.setId(20);
        persona.setNombre(nombreGuia);
        persona.setApellido(apellidoGuia);

        Guia guia = new Guia();
        guia.setId(idGuia);
        guia.setPersona(persona);
        guia.setEstado(estado);

        DisponibilidadGuia disponibilidad = new DisponibilidadGuia();
        disponibilidad.setIdDisponibilidad(idDisponibilidad);
        disponibilidad.setGuia(guia);
        disponibilidad.setFecha(fecha);
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);
        disponibilidad.setEstado(estado);

        return DisponibilidadGuiaSalida.desde(disponibilidad);
    }
}