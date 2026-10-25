package org.esfe.Servicios.Implementaciones;
import org.esfe.Controladores.EstadoController;
import org.esfe.DTOs.estado.EstadoSalida;
import org.esfe.Servicios.Interfaces.IEstadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CP-30: Consulta de catálogo general de estados vía GET /api/estados
@WebMvcTest(EstadoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EstadoServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IEstadoService estadoService;

    @Test
    void cp30_listarEstados_retorna200ConListaDeEstados() throws Exception {
        EstadoSalida activo = new EstadoSalida(1, "Activo", "General");
        EstadoSalida inactivo = new EstadoSalida(2, "Inactivo", "General");
        EstadoSalida pendiente = new EstadoSalida(3, "Pendiente", "reservas");

        when(estadoService.listar()).thenReturn(List.of(activo, inactivo, pendiente));

        mockMvc.perform(get("/api/estados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].idEstado").value(1))
                .andExpect(jsonPath("$[0].nombreEstado").value("Activo"))
                .andExpect(jsonPath("$[1].nombreEstado").value("Inactivo"))
                .andExpect(jsonPath("$[2].nombreEstado").value("Pendiente"))
                .andExpect(jsonPath("$[2].tipoEstado").value("reservas"));
    }
}
