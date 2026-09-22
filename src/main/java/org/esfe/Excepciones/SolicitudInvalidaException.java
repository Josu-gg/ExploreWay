package org.esfe.Excepciones;

// Regla de negocio incumplida por los datos enviados (400).
// Para duplicados o choques con datos existentes se usa ConflictoException (409).
public class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}