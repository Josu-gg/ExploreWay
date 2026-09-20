package org.esfe.Utilidades;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// Construye un Pageable seguro: limita el tamaño de página y el orden es fijo
// (no se acepta sort desde el cliente para no ordenar por columnas sensibles, p. ej. Contra).
public final class Paginacion {

    private static final int TAMANO_MAXIMO_PAGINA = 50;

    private Paginacion() {
    }

    public static Pageable de(int pagina, int tamano, String campoOrden) {
        int paginaSegura = Math.max(pagina, 0);
        int tamanoSeguro = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO_PAGINA);
        return PageRequest.of(paginaSegura, tamanoSeguro, Sort.by(campoOrden));
    }
}
