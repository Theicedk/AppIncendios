package cl.duoc.valledelsol.api_gateway.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration // Avisa a Spring que esta clase contiene configuraciones del sistema
@EnableWebFluxSecurity // Version especial para gateway de @EnableWebSecurity
public class SecurityConfig {
    
    // Funcion value que sirve para inyectar el valor de nuestra variable audience desde el application.yml
    @Value("${spring.security.oauth2.resourceserver.jwt.audiences}")
    private String audience;

    @Bean //Funcion que se encarga de guardar el resultado de la config de seguridad, para que luego pueda ser reutilizada en spring
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // Empieza la config de seguridad
            .authorizeExchange(exchanges -> exchanges
                // Se permiten todas las rutas de forma temporal para facilitar el testeo en desarrollo
                .anyExchange().permitAll()
            )
            // Se mantiene la configuración del Resource Server para que el contexto de Spring no falle,
            // pero .permitAll() tiene prioridad sobre la validación de tokens.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        // Deshabilitamos CSRF para permitir peticiones POST, PUT, DELETE sin token CSRF
        http.csrf(csrf -> csrf.disable()); 
        
        // Se cierra la config de seguridad y se devuelve para que Spring la utilice en el API Gateway
        return http.build();
    }
}