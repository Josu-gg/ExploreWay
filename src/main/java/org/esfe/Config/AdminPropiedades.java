package org.esfe.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminPropiedades(String correo, String contra) {
}
