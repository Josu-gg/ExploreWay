package org.esfe.Config;

import org.esfe.DTOs.usuario.UsuarioRegistroDatos;
import org.esfe.Modelos.Rol;
import org.esfe.Repositorios.IRolRepository;
import org.esfe.Repositorios.IUsuarioRepository;
import org.esfe.Servicios.Interfaces.IUsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
@EnableConfigurationProperties(AdminPropiedades.class)
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private static final String ROL_ADMIN = "Admin";

    @Bean
    public CommandLineRunner crearAdminInicial(IUsuarioRepository usuarioRepository,
                                               IRolRepository rolRepository,
                                               IUsuarioService usuarioService,
                                               AdminPropiedades propiedades) {
        return args -> {
            String correo = normalizarCorreo(propiedades.correo());

            if (correo.isEmpty() || propiedades.contra() == null || propiedades.contra().isBlank()) {
                log.warn("ADMIN_CORREO o ADMIN_CONTRA no configurados: no se creó el administrador inicial.");
                return;
            }

            if (usuarioRepository.existsByCorreo(correo)) {
                return;
            }

            Rol rolAdmin = rolRepository.findByNombreRol(ROL_ADMIN)
                    .orElse(null);
            if (rolAdmin == null) {
                log.error("Falta el rol Admin en la tabla Rol: no se creó el administrador inicial.");
                return;
            }

            UsuarioRegistroDatos datos = new UsuarioRegistroDatos() {};
            datos.setCorreo(correo);
            datos.setContra(propiedades.contra());
            datos.setNombre("Administrador");
            datos.setApellido("Sistema");
            datos.setTelefono("0000-0000");

            usuarioService.crearConRol(datos, rolAdmin);
            log.info("Administrador inicial creado: {}", correo);
        };
    }

    private static String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }
}
