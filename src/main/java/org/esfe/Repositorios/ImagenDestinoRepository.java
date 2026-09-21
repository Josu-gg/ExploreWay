package org.esfe.Repositorios;

import org.esfe.Modelos.ImagenDestino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ImagenDestinoRepository extends JpaRepository<ImagenDestino, Integer> {
    List<ImagenDestino> findByDestino_IdOrderByEsPrincipalDescIdImagen(Integer idDestino);
    
    //Evita manipulacion de los ids
    Optional<ImagenDestino> findByIdImagenAndDestino_Id(Integer idImagen, Integer idDestino);

    List<ImagenDestino> findByDestino_IdAndEsPrincipalTrue(Integer idDestino);

    Optional<ImagenDestino> findFirstByDestino_IdOrderByIdImagenAsc(Integer idDestino);

    long countByDestino_Id(Integer idDestino);

    boolean existsByDestino_IdAndUrlImagen(Integer idDestino, String urlImagen);

    //Portadas de varios destino en una sola consulta(Listado paginado)
    List<ImagenDestino> findByDestino_IdInAndEsPrincipalTrue(Collection<Integer> idDestino);
}
