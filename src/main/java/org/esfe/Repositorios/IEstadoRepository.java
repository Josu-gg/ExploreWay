package org.esfe.Repositorios;


import org.esfe.Modelos.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface IEstadoRepository extends JpaRepository<Estado, Integer> {

    List<Estado> findByTipoEstado(String tipoEstado);
    boolean existByNombreEstadoAndTipoEstado(String nombreEstado, String tipoEstado);

}
