package org.esfe.Repositorios;

import org.esfe.Modelos.HistorialReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IHistorialReservaRepository extends JpaRepository<HistorialReserva, Integer> {
    @EntityGraph(attributePaths = {"guiaAnterior", "guiaAnterior.persona",
            "guiaNuevo", "guiaNuevo.persona", "usuarioResponsable"})
    List<HistorialReserva> findByReserva_IdReservaOrderByFechaCambioAsc(Integer idReserva);

    @EntityGraph(attributePaths = {"guiaAnterior", "guiaAnterior.persona",
            "guiaNuevo", "guiaNuevo.persona", "usuarioResponsable"})
    Page<HistorialReserva> findAll(Pageable pageable);
}
