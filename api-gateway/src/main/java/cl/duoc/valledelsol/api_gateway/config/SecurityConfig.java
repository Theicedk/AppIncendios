package cl.duoc.valledelsol.api_gateway.config;

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
    //Objeto que sirve como filtro de seguridad para las rutas del API Gateway, se encarga de validar los tokens JWT y verificar los permisos de acceso a las rutas protegidas
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http)
                                                            //Objeto que sirve para configurar y establecer estos filtros (en este caso con http)
    {
        http
            //Empieza la config de seguridad
            .authorizeExchange(exchanges -> exchanges
                //Busca coincidencias con el token JWT ya sea para reportes o geolocalizacion
                //.hasAuthority realiza la verificacion que el token JWT tenga el permiso para acceder a la ruta
                .pathMatchers("/api/reportes/**").hasAuthority("SCOPE_read:reportes")
                .pathMatchers("/api/geolocalizacion/**").hasAuthority("SCOPE_read:geolocalizacion")
                
                //Verificador que cualquier otra ruta que no coincida con las anteriores, requiera autenticacion (token JWT valido)
                .anyExchange().authenticated()
            )
            //Avisa al gateway que para validar los tokens JWT, debe utilizar info de la configuracion de seguridad de OAuth2 Resource Server, que se encuentra en el application.yml
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        http.csrf(csrf -> csrf.disable()); // Deshabilitamos CSRF para permitir peticiones POST, PUT, DELETE sin token CSRF
        
        //Se cierra la config de seguridad y se devuelve para que Spring la utilice en el API Gateway
        return http.build();
    }


}