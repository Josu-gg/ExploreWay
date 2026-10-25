package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esfe.Controladores.DestinoActividadController;
import org.esfe.DTOs.destinoactividad.DestinoActividadGuardar;
import org.esfe.DTOs.destinoactividad.DestinoActividadModificar;
import org.esfe.DTOs.destinoactividad.DestinoActividadSalida;
import org.esfe.Modelos.Actividad;
import org.esfe.Modelos.Destino;
import org.esfe.Modelos.DestinoActividad;
import org.esfe.Modelos.Estado;
import org.esfe.Servicios.Interfaces.IDestinoActividadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DestinoActividadController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DestinoActividadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDestinoActividadService destinoActividadService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // CP-50: Vinculación de actividad a destino
    @Test
    void cp50_crearDestinoActividad_retorna201ConPrecioYDuracion() throws Exception {
        DestinoActividadGuardar dto = new DestinoActividadGuardar();
        dto.setIdDestino(7);
        dto.setIdActividad(9);
        dto.setDuracionMinutos(180);
        dto.setPrecioBase(new BigDecimal("25.50"));

        DestinoActividadSalida respuestaEsperada = construirDestinoActividadSalida(
                3, 7, "Cerro Verde", 9, "Senderismo", "Media", 180, new BigDecimal("25.50"), 1, "Activo");

        when(destinoActividadService.crear(any(DestinoActividadGuardar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/destino-actividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDestinoActividad").value(3))
                .andExpect(jsonPath("$.idDestino").value(7))
                .andExpect(jsonPath("$.nombreDestino").value("Cerro Verde"))
                .andExpect(jsonPath("$.idActividad").value(9))
                .andExpect(jsonPath("$.nombreActividad").value("Senderismo"))
                .andExpect(jsonPath("$.duracionMinutos").value(180))
                .andExpect(jsonPath("$.precioBase").value(25.50));
    }

    // CP-53: Modificar tarifa/duración de Actividad en Destino
    @Test
    void cp53_modificarDestinoActividad_retorna200ConDatosActualizados() throws Exception {
        Integer idDestinoActividad = 3;

        DestinoActividadModificar dto = new DestinoActividadModificar();
        dto.setDuracionMinutos(240);
        dto.setPrecioBase(new BigDecimal("30.00"));
        dto.setIdEstado(1);

        DestinoActividadSalida respuestaEsperada = construirDestinoActividadSalida(
                idDestinoActividad, 7, "Cerro Verde", 9, "Senderismo", "Media",
                240, new BigDecimal("30.00"), 1, "Activo");

        when(destinoActividadService.modificar(eq(idDestinoActividad), any(DestinoActividadModificar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(put("/api/destino-actividades/{id}", idDestinoActividad)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDestinoActividad").value(idDestinoActividad))
                .andExpect(jsonPath("$.duracionMinutos").value(240))
                .andExpect(jsonPath("$.precioBase").value(30.00))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    // Único builder reutilizado por ambos tests.
    private DestinoActividadSalida construirDestinoActividadSalida(Integer idDestinoActividad,
                                                                   Integer idDestino, String nombreDestino,
                                                                   Integer idActividad, String nombreActividad,
                                                                   String dificultad, Integer duracionMinutos,
                                                                   BigDecimal precioBase,
                                                                   Integer idEstado, String nombreEstado) {
        Estado estado = new Estado(idEstado, nombreEstado, "General");

        Destino destino = new Destino();
        destino.setId(idDestino);
        destino.setNombre(nombreDestino);
        destino.setDepartamento("Santa Ana");
        destino.setEstado(estado);

        Actividad actividad = new Actividad();
        actividad.setId(idActividad);
        actividad.setNombre(nombreActividad);
        actividad.setDificultad(dificultad);
        actividad.setEstado(estado);

        DestinoActividad destinoActividad = new DestinoActividad();
        destinoActividad.setIdDestinoActividad(idDestinoActividad);
        destinoActividad.setDestino(destino);
        destinoActividad.setActividad(actividad);
        destinoActividad.setDuracionMinutos(duracionMinutos);
        destinoActividad.setPrecioBase(precioBase);
        destinoActividad.setEstado(estado);

        return DestinoActividadSalida.desde(destinoActividad);
    }

    // CP-55: Consulta de actividades disponibles por destino
    @Test
    void cp55_listarDisponiblesPorDestino_retorna200ConActividadesYCostos() throws Exception {
        Integer idDestino = 7;

        DestinoActividadSalida senderismo = construirDestinoActividadSalida(
                3, idDestino, "Cerro Verde", 9, "Senderismo", "Media",
                180, new BigDecimal("25.50"));
        DestinoActividadSalida avistamiento = construirDestinoActividadSalida(
                4, idDestino, "Cerro Verde", 11, "Avistamiento de aves", "Baja",
                120, new BigDecimal("15.00"));

        when(destinoActividadService.listarDisponiblesPorDestino(eq(idDestino)))
                .thenReturn(List.of(senderismo, avistamiento));

        mockMvc.perform(get("/api/destino-actividades/destino/{idDestino}", idDestino))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idDestinoActividad").value(3))
                .andExpect(jsonPath("$[0].nombreActividad").value("Senderismo"))
                .andExpect(jsonPath("$[0].precioBase").value(25.50))
                .andExpect(jsonPath("$[1].idDestinoActividad").value(4))
                .andExpect(jsonPath("$[1].nombreActividad").value("Avistamiento de aves"))
                .andExpect(jsonPath("$[1].precioBase").value(15.00));
    }

    private DestinoActividadSalida construirDestinoActividadSalida(Integer idDestinoActividad,
                                                                   Integer idDestino, String nombreDestino,
                                                                   Integer idActividad, String nombreActividad,
                                                                   String dificultad, Integer duracionMinutos,
                                                                   BigDecimal precioBase) {
        Estado estadoActivo = new Estado(1, "Activo", "General");

        Destino destino = new Destino();
        destino.setId(idDestino);
        destino.setNombre(nombreDestino);
        destino.setDepartamento("Santa Ana");
        destino.setEstado(estadoActivo);

        Actividad actividad = new Actividad();
        actividad.setId(idActividad);
        actividad.setNombre(nombreActividad);
        actividad.setDificultad(dificultad);
        actividad.setEstado(estadoActivo);

        DestinoActividad destinoActividad = new DestinoActividad();
        destinoActividad.setIdDestinoActividad(idDestinoActividad);
        destinoActividad.setDestino(destino);
        destinoActividad.setActividad(actividad);
        destinoActividad.setDuracionMinutos(duracionMinutos);
        destinoActividad.setPrecioBase(precioBase);
        destinoActividad.setEstado(estadoActivo);

        return DestinoActividadSalida.desde(destinoActividad);
    }
}