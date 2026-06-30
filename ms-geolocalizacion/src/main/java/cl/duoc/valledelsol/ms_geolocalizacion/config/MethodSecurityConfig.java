package cl.duoc.valledelsol.ms_geolocalizacion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@Profile("!local")
@EnableMethodSecurity
public class MethodSecurityConfig {
}
