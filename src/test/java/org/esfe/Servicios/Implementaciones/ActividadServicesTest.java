package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esfe.Controladores.ActividadController;
import org.esfe.DTOs.actividad.ActividadGuardar;
import org.esfe.DTOs.actividad.ActividadModificar;
import org.esfe.DTOs.actividad.ActividadSalida;
import org.esfe.Servicios.Interfaces.IActividadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.List;
import java.util.Optional;

// CP-44: Registrar nueva Actividad vía POST /api/actividades
@WebMvcTest(ActividadController.class)
@AutoConfigureMockMvc(addFilters = false)

public class ActividadServicesTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IActividadService actividadService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cp44_registrarActividad_retorna201ConActividadCreada() throws Exception {
        ActividadGuardar dto = new ActividadGuardar();
        dto.setNombre("Senderismo");
        dto.setDescripcion("Caminata guiada por sendero natural");
        dto.setDificultad("Media");
        dto.setIdEstado(1);

        ActividadSalida respuestaEsperada = new ActividadSalida(
                9, "Senderismo", "Caminata guiada por sendero natural", "Media", 1, "Activo");

        when(actividadService.guardar(any(ActividadGuardar.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/actividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idActividad").value(9))
                .andExpect(jsonPath("$.nombre").value("Senderismo"))
                .andExpect(jsonPath("$.dificultad").value("Media"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    // CP-45: Modificar Actividad
    @Test
    void cp45_modificarActividad_retorna200ConActividadActualizada() throws Exception {
        Integer idActividad = 9;

        ActividadModificar dto = new ActividadModificar();
        dto.setNombre("Senderismo nocturno");
        dto.setDescripcion("Caminata guiada con linternas por sendero natural");
        dto.setDificultad("Alta");
        dto.setIdEstado(1);

        ActividadSalida respuestaEsperada = new ActividadSalida(
                idActividad, "Senderismo nocturno",
                "Caminata guiada con linternas por sendero natural", "Alta", 1, "Activo");

        when(actividadService.modificar(eq(idActividad), any(ActividadModificar.class)))
                .thenReturn(Optional.of(respuestaEsperada));

        mockMvc.perform(put("/api/actividades/{id}", idActividad)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idActividad").value(idActividad))
                .andExpect(jsonPath("$.nombre").value("Senderismo nocturno"))
                .andExpect(jsonPath("$.dificultad").value("Alta"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    // CP-49: Consulta de catálogo maestro de actividades
    @Test
    void cp49_listarActividades_retorna200ConCatalogoCompleto() throws Exception {
        ActividadSalida senderismo = new ActividadSalida(
                9, "Senderismo", "Caminata guiada por sendero natural", "Media", 1, "Activo");
        ActividadSalida buceo = new ActividadSalida(
                10, "Buceo", "Inmersión guiada en arrecife", "Alta", 1, "Activo");

        when(actividadService.listar()).thenReturn(List.of(senderismo, buceo));

        mockMvc.perform(get("/api/actividades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idActividad").value(9))
                .andExpect(jsonPath("$[0].nombre").value("Senderismo"))
                .andExpect(jsonPath("$[1].idActividad").value(10))
                .andExpect(jsonPath("$[1].nombre").value("Buceo"));
    }


}
