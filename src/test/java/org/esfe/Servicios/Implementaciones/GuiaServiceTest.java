package org.esfe.Servicios.Implementaciones;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.esfe.Controladores.GuiaController;
import org.esfe.DTOs.guia.GuiaPerfilModificar;
import org.esfe.DTOs.guia.GuiaRegistroGuardar;
import org.esfe.DTOs.guia.GuiaSalida;
import org.esfe.Servicios.Interfaces.IGuiaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CP-57: Registrar nuevo Guía (Persona + Usuario + Guía) vía POST /api/guias/registro
@WebMvcTest(GuiaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GuiaServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IGuiaService guiaService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void cp57_registrarGuia_retorna201ConPersonaUsuarioYGuiaCreados() throws Exception {
        GuiaRegistroGuardar dto = new GuiaRegistroGuardar();
        dto.setNombre("Marta");
        dto.setApellido("Hernández");
        dto.setTelefono("7111-2222");
        dto.setDireccion("Ahuachapán Sur");
        dto.setFechaNacimiento(LocalDate.of(1998, 6, 15));
        dto.setFoto("https://res.cloudinary.com/exploreway/marta.jpg");
        dto.setCorreo("marta.hernandez@exploreway.com");
        dto.setContra("Clave1234");
        dto.setBiografia("Guía con experiencia en senderismo y ecoturismo");
        dto.setExperiencia("5 años guiando recorridos en Cerro Verde");
        dto.setEstudios("Técnico en Turismo");
        dto.setPrimerosAuxilios(true);
        dto.setEstadoDisponibilidad(true);

        GuiaSalida respuestaEsperada = new GuiaSalida(
                6, 20, "Marta", "Hernández",
                dto.getBiografia(), dto.getExperiencia(), dto.getEstudios(),
                true, true, BigDecimal.ZERO,
                1, "Activo", dto.getFoto());

        when(guiaService.registrar(any(GuiaRegistroGuardar.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/guias/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.idPersona").value(20))
                .andExpect(jsonPath("$.nombrePersona").value("Marta"))
                .andExpect(jsonPath("$.apellidoPersona").value("Hernández"))
                .andExpect(jsonPath("$.primerosAuxilios").value(true))
                .andExpect(jsonPath("$.estadoDisponibilidad").value(true))
                .andExpect(jsonPath("$.calificacionPromedio").value(0))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    // CP-58: Modificar perfil de Guía
    @Test
    void cp58_modificarMiPerfil_retorna200ConPerfilActualizado() throws Exception {
        GuiaPerfilModificar dto = new GuiaPerfilModificar();
        dto.setBiografia("Guía con experiencia en senderismo y ecoturismo");
        dto.setExperiencia("6 años guiando recorridos en Cerro Verde");
        dto.setEstudios("Técnico en Turismo");
        dto.setPrimerosAuxilios(true);
        dto.setEstadoDisponibilidad(false);
        dto.setFoto("https://res.cloudinary.com/exploreway/marta-nueva.jpg");

        GuiaSalida respuestaEsperada = new GuiaSalida(
                6, 20, "Marta", "Hernández",
                dto.getBiografia(), dto.getExperiencia(), dto.getEstudios(),
                true, false, new BigDecimal("4.50"),
                1, "Activo", dto.getFoto());

        when(guiaService.modificarMiPerfil(any(GuiaPerfilModificar.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(put("/api/guias/mi-perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.experiencia").value("6 años guiando recorridos en Cerro Verde"))
                .andExpect(jsonPath("$.estadoDisponibilidad").value(false))
                .andExpect(jsonPath("$.foto").value("https://res.cloudinary.com/exploreway/marta-nueva.jpg"));
    }
    //CP-64 Consultar/Listar Guías
    @Test
    void cp64_listarGuias_retorna200ConListadoDeGuias() throws Exception {
        GuiaSalida guiaMarta = new GuiaSalida(
                6, 20, "Marta", "Hernández",
                "Guía con experiencia en senderismo y ecoturismo",
                "6 años guiando recorridos en Cerro Verde", "Técnico en Turismo",
                true, true, new BigDecimal("4.50"),
                1, "Activo", "https://res.cloudinary.com/exploreway/marta.jpg");

        GuiaSalida guiaJorge = new GuiaSalida(
                7, 21, "Jorge", "Ramírez",
                "Guía especializado en buceo", "3 años en arrecifes de La Libertad",
                "Bachillerato", false, true, new BigDecimal("4.80"),
                1, "Activo", null);

        when(guiaService.listar()).thenReturn(List.of(guiaMarta, guiaJorge));

        mockMvc.perform(get("/api/guias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idGuia").value(6))
                .andExpect(jsonPath("$[0].nombrePersona").value("Marta"))
                .andExpect(jsonPath("$[1].idGuia").value(7))
                .andExpect(jsonPath("$[1].nombrePersona").value("Jorge"));
    }

}