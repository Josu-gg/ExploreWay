package org.esfe.Controladores;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.esfe.DTOs.ImagenDestino.ImagenDestinoGuardar;
import org.esfe.DTOs.ImagenDestino.ImagenDestinoModificar;
import org.esfe.DTOs.ImagenDestino.ImagenDestinoSalida;
import org.esfe.Modelos.ImagenDestino;
import org.esfe.Servicios.Interfaces.IIImagenDestinoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// Solo delega en el servicio: aquí no hay lógica de negocio.
// La ruta incluye idDestino para que el servicio verifique que la imagen pertenece a ese destino.
// No hay listado paginado: el servicio limita a 10 imágenes por destino.
// TODO (paso 7, seguridad):
//   - GET /api/destinos/{idDestino}/imagenes -> público
//   - POST, PUT, PATCH y DELETE              -> solo Administrador
@RestController
@RequestMapping("/api/destinos/{idDestino}/imagenes")
@RequiredArgsConstructor
@Tag(name = "Imágenes de destino", description = "Galería de imágenes de cada destino (URLs)")
public class ImagenDestinoController {
    private final IIImagenDestinoService imagenDestinoService;
    @GetMapping
    @Operation(summary = "Listar las imagenes de un destino(la principal primero)")
    public List<ImagenDestinoSalida>listarPorDestino(@PathVariable Integer idDestino){
        return imagenDestinoService.listarPorDestino(idDestino);
    }

    @PostMapping
    @Operation(summary = "Agregar una imagen a un destino")
    public ResponseEntity<ImagenDestinoSalida>agregar(@PathVariable Integer idDestino,
                                                      @Valid @RequestBody ImagenDestinoGuardar dto){
        ImagenDestinoSalida creada = imagenDestinoService.agregar(idDestino, dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idImagen}")
                .buildAndExpand(creada.idImagen())
                .toUri();
                return ResponseEntity.created(ubicacion).body(creada);
    }
    @PutMapping("/{idImagen}")
    @Operation(summary = "Modificar la descripcion de  una imagen")
    public  ImagenDestinoSalida modificar(@PathVariable Integer idDestino,
                                          @PathVariable Integer idImagen,
                                          @Valid @RequestBody ImagenDestinoModificar dto){
        return imagenDestinoService.actualizarDescripcion(idDestino, idImagen, dto);
    }
    @PatchMapping("/{idImagen}/principal")
    @Operation(summary = "Marcar una imaagen como portada del destino")
    public ImagenDestinoSalida marcarComoPrincipal(@PathVariable Integer idDestino,
                                                   @PathVariable Integer idImagen){
        return imagenDestinoService.marcarComoPrincipal(idDestino, idImagen);
    }
    @DeleteMapping("/{idImagen}")
    @Operation(summary = "Eliminar una imagen del destino")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idDestino,
                                         @PathVariable Integer idImagen){
        imagenDestinoService.eliminar(idDestino, idImagen);
        return ResponseEntity.noContent().build();
    }
}
