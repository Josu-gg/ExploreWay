package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esfe.Controladores.GuiaActividadController;
import org.esfe.DTOs.guiaactividad.GuiaActividadGuardar;
import org.esfe.DTOs.guiaactividad.GuiaActividadModificar;
import org.esfe.DTOs.guiaactividad.GuiaActividadSalida;
import org.esfe.Modelos.Actividad;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.GuiaActividad;
import org.esfe.Modelos.Persona;
import org.esfe.Servicios.Interfaces.IGuiaActividadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CP-65: Asignar Actividad que puede impartir un Guía vía POST /api/guia-actividades
@WebMvcTest(GuiaActividadController.class)
@AutoConfigureMockMvc(addFilters = false)

public class GuiaActividadTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IGuiaActividadService guiaActividadService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cp65_asignarActividadAGuia_retorna201ConActividadHabilitada() throws Exception {
        GuiaActividadGuardar dto = new GuiaActividadGuardar();
        dto.setIdGuia(6);
        dto.setIdActividad(9);

        GuiaActividadSalida respuestaEsperada = construirGuiaActividadSalida(
                4, 6, "Marta", "Hernández", 9, "Senderismo", "Media");

        when(guiaActividadService.crear(any(GuiaActividadGuardar.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/guia-actividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idGuiaActividad").value(4))
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.nombreGuia").value("Marta"))
                .andExpect(jsonPath("$.idActividad").value(9))
                .andExpect(jsonPath("$.nombreActividad").value("Senderismo"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    private GuiaActividadSalida construirGuiaActividadSalida(Integer idGuiaActividad,
                                                             Integer idGuia, String nombreGuia, String apellidoGuia,
                                                             Integer idActividad, String nombreActividad,
                                                             String dificultad) {
        Estado estadoActivo = new Estado(1, "Activo", "General");

        Persona persona = new Persona();
        persona.setId(20);
        persona.setNombre(nombreGuia);
        persona.setApellido(apellidoGuia);

        Guia guia = new Guia();
        guia.setId(idGuia);
        guia.setPersona(persona);
        guia.setEstado(estadoActivo);

        Actividad actividad = new Actividad();
        actividad.setId(idActividad);
        actividad.setNombre(nombreActividad);
        actividad.setDificultad(dificultad);
        actividad.setEstado(estadoActivo);

        GuiaActividad guiaActividad = new GuiaActividad();
        guiaActividad.setIdGuiaActividad(idGuiaActividad);
        guiaActividad.setGuia(guia);
        guiaActividad.setActividad(actividad);
        guiaActividad.setEstado(estadoActivo);

        return GuiaActividadSalida.desde(guiaActividad);
    }
    // CP-67: Modificar/Desactivar asignación Guía - Actividad vía PUT /api/guia-actividades/{id}

    @Test
    void cp67_modificarAsignacion_retorna200ConEstadoModificado() throws Exception {
        Integer idGuiaActividad = 4;

        GuiaActividadModificar dto = new GuiaActividadModificar();
        dto.setIdEstado(2);

        GuiaActividadSalida respuestaEsperada = construirGuiaActividadSalida(
                idGuiaActividad, 6, "Marta", "Hernández", 9, "Senderismo", "Media",
                2, "Inactivo");

        when(guiaActividadService.modificar(eq(idGuiaActividad), any(GuiaActividadModificar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(put("/api/guia-actividades/{id}", idGuiaActividad)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idGuiaActividad").value(idGuiaActividad))
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.idActividad").value(9))
                .andExpect(jsonPath("$.idEstado").value(2))
                .andExpect(jsonPath("$.nombreEstado").value("Inactivo"));
    }

    private GuiaActividadSalida construirGuiaActividadSalida(Integer idGuiaActividad,
                                                             Integer idGuia, String nombreGuia, String apellidoGuia,
                                                             Integer idActividad, String nombreActividad,
                                                             String dificultad,
                                                             Integer idEstado, String nombreEstado) {
        Estado estadoAsignacion = new Estado(idEstado, nombreEstado, "General");
        Estado estadoActivo = new Estado(1, "Activo", "General");

        Persona persona = new Persona();
        persona.setId(20);
        persona.setNombre(nombreGuia);
        persona.setApellido(apellidoGuia);

        Guia guia = new Guia();
        guia.setId(idGuia);
        guia.setPersona(persona);
        guia.setEstado(estadoActivo);

        Actividad actividad = new Actividad();
        actividad.setId(idActividad);
        actividad.setNombre(nombreActividad);
        actividad.setDificultad(dificultad);
        actividad.setEstado(estadoActivo);

        GuiaActividad guiaActividad = new GuiaActividad();
        guiaActividad.setIdGuiaActividad(idGuiaActividad);
        guiaActividad.setGuia(guia);
        guiaActividad.setActividad(actividad);
        guiaActividad.setEstado(estadoAsignacion);

        return GuiaActividadSalida.desde(guiaActividad);
    }

}
