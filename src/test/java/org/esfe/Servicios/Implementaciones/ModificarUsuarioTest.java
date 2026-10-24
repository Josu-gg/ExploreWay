package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.esfe.Controladores.UsuarioController;
import org.esfe.DTOs.usuario.UsuarioModificar;
import org.esfe.DTOs.usuario.UsuarioSalida;
import org.esfe.Modelos.Estado;
import org.esfe.Modelos.Persona;
import org.esfe.Modelos.Rol;
import org.esfe.Modelos.Usuario;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModificarUsuarioTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void cp11_modificarUsuario_retorna200ConCambiosGuardados() throws Exception {
        Integer idUsuario = 5;

        UsuarioModificar dto = new UsuarioModificar();
        dto.setNombre("Carlos");
        dto.setApellido("Martínez");
        dto.setTelefono("7890-1234");
        dto.setDireccion("Col. Las Flores, Ahuachapán");
        dto.setFechaNacimiento(LocalDate.of(1995, 3, 20));
        dto.setFoto("https://res.cloudinary.com/exploreway/foto5.jpg");
        dto.setCorreo("carlos.martinez@exploreway.com");
        dto.setIdRol(3);
        dto.setIdEstado(1);

        UsuarioSalida respuestaEsperada = construirUsuarioSalida(idUsuario, dto);

        when(usuarioService.modificar(eq(idUsuario), any(UsuarioModificar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(put("/api/usuarios/{id}", idUsuario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(idUsuario))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellido").value("Martínez"))
                .andExpect(jsonPath("$.telefono").value("7890-1234"))
                .andExpect(jsonPath("$.correo").value("carlos.martinez@exploreway.com"))
                .andExpect(jsonPath("$.idRol").value(3))
                .andExpect(jsonPath("$.nombreRol").value("Guia"))
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombreEstado").value("Activo"));
    }

    private UsuarioSalida construirUsuarioSalida(Integer idUsuario, UsuarioModificar dto) {
        Persona persona = new Persona();
        persona.setId(10);
        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setTelefono(dto.getTelefono());
        persona.setDireccion(dto.getDireccion());
        persona.setFechaNacimiento(dto.getFechaNacimiento());
        persona.setFoto(dto.getFoto());

        Estado estadoGeneral = new Estado(1, "Activo", "General");
        Rol rolGuia = new Rol(3, "Guia", estadoGeneral);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setCorreo(dto.getCorreo());
        usuario.setContra("$2a$10$hashSimuladoNoSeExpone");
        usuario.setRol(rolGuia);
        usuario.setEstado(estadoGeneral);
        usuario.setPersona(persona);

        return UsuarioSalida.desde(usuario);
    }
}