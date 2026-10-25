package org.esfe.Servicios.Implementaciones;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.esfe.Controladores.GuiaDestinoControlller;
import org.esfe.DTOs.comun.CambioEstadoModificar;
import org.esfe.DTOs.guiadestino.GuiaDestinoGuardar;
import org.esfe.DTOs.guiadestino.GuiaDestinoSalida;
import org.esfe.Modelos.Destino;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Guia;
import org.esfe.Modelos.GuiaDestino;
import org.esfe.Modelos.Persona;
import org.esfe.Servicios.Interfaces.IGuiaDestinoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CP-71: Asignar Destino que puede cubrir un Guía vía POST /api/guia-destinos
@WebMvcTest(GuiaDestinoControlller.class)
@AutoConfigureMockMvc(addFilters = false)
public class GuiaDestinoTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IGuiaDestinoService guiaDestinoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cp71_asignarDestinoAGuia_retorna201ConDestinoHabilitado() throws Exception {
        GuiaDestinoGuardar dto = new GuiaDestinoGuardar();
        dto.setIdGuia(6);
        dto.setIdDestino(7);

        GuiaDestinoSalida respuestaEsperada = construirGuiaDestinoSalida(
                5, 6, "Marta", "Hernández", 7, "Cerro Verde", "Santa Ana", "Cerro Verde");

        when(guiaDestinoService.crear(any(GuiaDestinoGuardar.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/guia-destinos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idGuiaDestino").value(5))
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.nombreGuia").value("Marta"))
                .andExpect(jsonPath("$.idDestino").value(7))
                .andExpect(jsonPath("$.nombreDestino").value("Cerro Verde"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    private GuiaDestinoSalida construirGuiaDestinoSalida(Integer idGuiaDestino,
                                                         Integer idGuia, String nombreGuia, String apellidoGuia,
                                                         Integer idDestino, String nombreDestino,
                                                         String departamento, String municipio) {
        Estado estadoActivo = new Estado(1, "Activo", "General");

        Persona persona = new Persona();
        persona.setId(20);
        persona.setNombre(nombreGuia);
        persona.setApellido(apellidoGuia);

        Guia guia = new Guia();
        guia.setId(idGuia);
        guia.setPersona(persona);
        guia.setEstado(estadoActivo);

        Destino destino = new Destino();
        destino.setId(idDestino);
        destino.setNombre(nombreDestino);
        destino.setDepartamento(departamento);
        destino.setMunicipio(municipio);
        destino.setEstado(estadoActivo);

        GuiaDestino guiaDestino = new GuiaDestino();
        guiaDestino.setIdGuiaDestino(idGuiaDestino);
        guiaDestino.setGuia(guia);
        guiaDestino.setDestino(destino);
        guiaDestino.setEstado(estadoActivo);

        return GuiaDestinoSalida.desde(guiaDestino);
    }
    // CP-73: Modificar/Desactivar asignación Guía - Destino vía PUT /api/guia-destinos/{id}
    @Test
    void cp73_modificarAsignacion_retorna200ConEstadoModificado() throws Exception {
        Integer idGuiaDestino = 5;

        CambioEstadoModificar dto = new CambioEstadoModificar();
        dto.setIdEstado(2);

        GuiaDestinoSalida respuestaEsperada = construirGuiaDestinoSalida(
                idGuiaDestino, 6, "Marta", "Hernández", 7, "Cerro Verde",
                "Santa Ana", "Cerro Verde", 2, "Inactivo");

        when(guiaDestinoService.modificar(eq(idGuiaDestino), any(CambioEstadoModificar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(put("/api/guia-destinos/{id}", idGuiaDestino)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idGuiaDestino").value(idGuiaDestino))
                .andExpect(jsonPath("$.idGuia").value(6))
                .andExpect(jsonPath("$.idDestino").value(7))
                .andExpect(jsonPath("$.idEstado").value(2))
                .andExpect(jsonPath("$.nombreEstado").value("Inactivo"));
    }

    private GuiaDestinoSalida construirGuiaDestinoSalida(Integer idGuiaDestino,
                                                         Integer idGuia, String nombreGuia, String apellidoGuia,
                                                         Integer idDestino, String nombreDestino,
                                                         String departamento, String municipio,
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

        Destino destino = new Destino();
        destino.setId(idDestino);
        destino.setNombre(nombreDestino);
        destino.setDepartamento(departamento);
        destino.setMunicipio(municipio);
        destino.setEstado(estadoActivo);

        GuiaDestino guiaDestino = new GuiaDestino();
        guiaDestino.setIdGuiaDestino(idGuiaDestino);
        guiaDestino.setGuia(guia);
        guiaDestino.setDestino(destino);
        guiaDestino.setEstado(estadoAsignacion);

        return GuiaDestinoSalida.desde(guiaDestino);
    }
}
