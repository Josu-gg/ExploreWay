package org.esfe.Servicios.Implementaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esfe.Controladores.ImagenDestinoController;
import org.esfe.DTOs.ImagenDestino.ImagenDestinoGuardar;
import org.esfe.DTOs.ImagenDestino.ImagenDestinoSalida;
import org.esfe.Servicios.Interfaces.IIImagenDestinoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CP-38: Carga de fotografía a la galería del destino vía POST /api/destinos/{idDestino}/imagenes
@WebMvcTest(ImagenDestinoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ImagenDestinoTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IIImagenDestinoService imagenDestinoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cp38_agregarImagen_retorna201ConImagenYLocation() throws Exception {
        Integer idDestino = 7;

        ImagenDestinoGuardar dto = new ImagenDestinoGuardar(
                "https://res.cloudinary.com/exploreway/cerro-verde-1.jpg",
                "Vista panorámica desde el mirador",
                true);

        ImagenDestinoSalida respuestaEsperada = new ImagenDestinoSalida(
                12, dto.urlImagen(), dto.descripcion(), true);

        when(imagenDestinoService.agregar(eq(idDestino), any(ImagenDestinoGuardar.class)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/destinos/{idDestino}/imagenes", idDestino)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        containsString("/api/destinos/" + idDestino + "/imagenes/12")))
                .andExpect(jsonPath("$.idImagen").value(12))
                .andExpect(jsonPath("$.urlImagen").value(dto.urlImagen()))
                .andExpect(jsonPath("$.descripcion").value(dto.descripcion()))
                .andExpect(jsonPath("$.esPrincipal").value(true));
    }

    //CP-41 Definir una imagen como principal
    @Test
    void cp41_marcarComoPrincipal_retorna200ConImagenMarcada() throws Exception {
        Integer idDestino = 7;
        Integer idImagen = 15;

        ImagenDestinoSalida respuestaEsperada = new ImagenDestinoSalida(
                idImagen,
                "https://res.cloudinary.com/exploreway/cerro-verde-2.jpg",
                "Vista desde el sendero principal",
                true);

        when(imagenDestinoService.marcarComoPrincipal(eq(idDestino), eq(idImagen)))
                .thenReturn(respuestaEsperada);

        mockMvc.perform(patch("/api/destinos/{idDestino}/imagenes/{idImagen}/principal",
                        idDestino, idImagen))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idImagen").value(idImagen))
                .andExpect(jsonPath("$.urlImagen").value(respuestaEsperada.urlImagen()))
                .andExpect(jsonPath("$.esPrincipal").value(true));
    }
}
