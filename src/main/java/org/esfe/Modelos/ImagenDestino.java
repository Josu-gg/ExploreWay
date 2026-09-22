package org.esfe.Modelos;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ImagenDestino")
@Getter
@Setter
@NoArgsConstructor

public class ImagenDestino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdImagen")
    private Integer idImagen;

    @ManyToOne(fetch= FetchType.LAZY, optional= false)
    @JoinColumn(name="IdDestino", nullable= false)
    private Destino destino;

    @Column(name= "UrlImagen", nullable = false,length = 500)
    private String urlImagen;

    @Column(name ="Descripcion", length = 255)
    private String descripcion;

    @Column(name="EsPrincipal", nullable = false)
    private Boolean esPrincipal= false;
}
