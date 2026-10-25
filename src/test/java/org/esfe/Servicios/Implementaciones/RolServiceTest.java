package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esfe.Controladores.RolController;
import org.esfe.DTOs.rol.RolGuardar;
import org.esfe.DTOs.rol.RolSalida;
import org.esfe.Servicios.Interfaces.IRolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CP-13: Registrar un nuevo rol vía POST /api/roles
@WebMvcTest(RolController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RolServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRolService rolService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cp13_registrarRol_retorna201ConRolCreado() throws Exception {
        RolGuardar dto = new RolGuardar();
        dto.setNombreRol("Supervisor");
        dto.setIdEstado(1);

        RolSalida respuestaEsperada = new RolSalida(4, "Supervisor", 1, "Activo");

        when(rolService.guardar(any(RolGuardar.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRol").value(4))
                .andExpect(jsonPath("$.nombreRol").value("Supervisor"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }
    @Test
    void cp19_listarRoles_retorna200ConListaDeRoles() throws Exception {
        RolSalida rolAdmin = new RolSalida(1, "Admin", 1, "Activo");
        RolSalida rolCliente = new RolSalida(2, "Cliente", 1, "Activo");
        RolSalida rolGuia = new RolSalida(3, "Guia", 1, "Activo");

        when(rolService.listar()).thenReturn(List.of(rolAdmin, rolCliente, rolGuia));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].idRol").value(1))
                .andExpect(jsonPath("$[0].nombreRol").value("Admin"))
                .andExpect(jsonPath("$[1].nombreRol").value("Cliente"))
                .andExpect(jsonPath("$[2].nombreRol").value("Guia"));
    }
}
