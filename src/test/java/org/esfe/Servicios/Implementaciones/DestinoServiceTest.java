package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esfe.Controladores.DestinoController;
import org.esfe.DTOs.destino.DestinoModificar;
import org.esfe.DTOs.destino.DestinoGuardar;
import org.esfe.DTOs.destino.DestinoSalida;
import org.esfe.Servicios.Interfaces.IDestinoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CP-31: Registrar nuevo Destino vía POST /api/destinos
@WebMvcTest(DestinoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DestinoServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDestinoService destinoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cp31_registrarDestino_retorna201ConDestinoCreado() throws Exception {
        DestinoGuardar dto = new DestinoGuardar();
        dto.setNombre("Cerro Verde");
        dto.setDescripcion("Parque nacional con vista al volcán Izalco");
        dto.setDepartamento("Santa Ana");
        dto.setMunicipio("Cerro Verde");
        dto.setIdEstado(1);

        DestinoSalida respuestaEsperada = new DestinoSalida(
                7, "Cerro Verde", "Parque nacional con vista al volcán Izalco",
                "Santa Ana", "Cerro Verde", 1, "Activo");

        when(destinoService.guardar(any(DestinoGuardar.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/destinos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDestino").value(7))
                .andExpect(jsonPath("$.nombre").value("Cerro Verde"))
                .andExpect(jsonPath("$.departamento").value("Santa Ana"))
                .andExpect(jsonPath("$.municipio").value("Cerro Verde"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }
    //CP-33 Modificar un Destino existente
    @Test
    void cp33_modificarDestino_retorna200ConDestinoModificado() throws Exception {
        Integer idDestino = 7;

        DestinoModificar dto = new DestinoModificar();
        dto.setNombre("Cerro Verde - Parque Nacional");
        dto.setDescripcion("Área natural protegida, mirador del volcán Izalco");
        dto.setDepartamento("Santa Ana");
        dto.setMunicipio("Cerro Verde");
        dto.setIdEstado(1);

        DestinoSalida respuestaEsperada = new DestinoSalida(
                idDestino, "Cerro Verde - Parque Nacional",
                "Área natural protegida, mirador del volcán Izalco",
                "Santa Ana", "Cerro Verde", 1, "Activo");

        when(destinoService.modificar(eq(idDestino), any(DestinoModificar.class)))
                .thenReturn(Optional.of(respuestaEsperada));

        mockMvc.perform(put("/api/destinos/{id}", idDestino)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDestino").value(idDestino))
                .andExpect(jsonPath("$.nombre").value("Cerro Verde - Parque Nacional"))
                .andExpect(jsonPath("$.departamento").value("Santa Ana"))
                .andExpect(jsonPath("$.municipio").value("Cerro Verde"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }
    //CP-34 Eliminar un destino existente
    @Test
    void cp34_eliminarDestino_retorna204SinReservasActivas() throws Exception {
        Integer idDestino = 7;

        when(destinoService.eliminar(idDestino)).thenReturn(true);

        mockMvc.perform(delete("/api/destinos/{id}", idDestino))
                .andExpect(status().isNoContent());

        verify(destinoService).eliminar(idDestino);
    }
    //CP-37 Listar todos los destinos existentes
    @Test
    void cp37_listarDestinos_retorna200ConListaDeDestinos() throws Exception {
        DestinoSalida cerroVerde = new DestinoSalida(
                7, "Cerro Verde", "Parque nacional con vista al volcán Izalco",
                "Santa Ana", "Cerro Verde", 1, "Activo");
        DestinoSalida elTunco = new DestinoSalida(
                8, "El Tunco", "Playa reconocida para el surf",
                "La Libertad", "Tamanique", 1, "Activo");

        when(destinoService.listar()).thenReturn(List.of(cerroVerde, elTunco));

        mockMvc.perform(get("/api/destinos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idDestino").value(7))
                .andExpect(jsonPath("$[0].nombre").value("Cerro Verde"))
                .andExpect(jsonPath("$[1].idDestino").value(8))
                .andExpect(jsonPath("$[1].nombre").value("El Tunco"));
    }
}
