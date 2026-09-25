package org.esfe.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtPropiedades(String secreto,long expiracionMinutos) {

}
