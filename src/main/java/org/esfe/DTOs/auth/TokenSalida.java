package org.esfe.DTOs.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class TokenSalida {

    private final String token;
    private final String tipoToken;
    private final Instant expiraEn;
    private final String correo;
    private final String rol;
}