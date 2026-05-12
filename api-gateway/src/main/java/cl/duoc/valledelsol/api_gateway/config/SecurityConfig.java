package cl.duoc.valledelsol.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
                //.pathMatchers("/api/reportes/**").hasAuthority("SCOPE_read:reportes")
                //.pathMatchers("/api/geolocalizacion/**").hasAuthority("SCOPE_read:geolocalizacion")
                  // Endpoints publicos para pruebas desde Expo Go en red local.
                // Lecturas abiertas para que el frontend pueda mostrar mapa y reportes mientras no envíe JWT real.
                // Cuando el frontend adjunte access token, estas reglas se pueden volver a restringir por roles/scopes.
                .pathMatchers(HttpMethod.GET, "/api/bff/dashboard-combinado").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/reportes/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/focos/**").permitAll()

                // Permisos de escritura/edicion/borrado: permiten tokens con scope `write:*` o roles con privilegios (Usuario, Moderador, Autoridad).
                .pathMatchers(HttpMethod.POST, "/api/reportes/**").hasAnyAuthority("SCOPE_write:reportes", "ROLE_Usuario", "ROLE_Moderador", "ROLE_Autoridad")
                .pathMatchers(HttpMethod.PUT, "/api/reportes/**").hasAnyAuthority("SCOPE_write:reportes", "ROLE_Usuario", "ROLE_Moderador", "ROLE_Autoridad")
                .pathMatchers(HttpMethod.DELETE, "/api/reportes/**").hasAnyAuthority("SCOPE_write:reportes", "ROLE_Usuario", "ROLE_Moderador", "ROLE_Autoridad")

                .pathMatchers(HttpMethod.POST, "/api/focos/**").hasAnyAuthority("SCOPE_write:geolocalizacion", "ROLE_Usuario", "ROLE_Moderador", "ROLE_Autoridad")
                .pathMatchers(HttpMethod.PUT, "/api/focos/**").hasAnyAuthority("SCOPE_write:geolocalizacion", "ROLE_Usuario", "ROLE_Moderador", "ROLE_Autoridad")
                .pathMatchers(HttpMethod.DELETE, "/api/focos/**").hasAnyAuthority("SCOPE_write:geolocalizacion", "ROLE_Usuario", "ROLE_Moderador", "ROLE_Autoridad")
                //Verificador que cualquier otra ruta que no coincida con las anteriores, requiera autenticacion (token JWT valido)
                .anyExchange().authenticated()
            )
            //Avisa al gateway que para validar los tokens JWT, debe utilizar info de la configuracion de seguridad de OAuth2 Resource Server, que se encuentra en el application.yml
            //Usamos un convertidor reactivo que además extrae la claim `roles` y `permissions` y las mapea a authorities
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        http.csrf(csrf -> csrf.disable()); // Deshabilitamos CSRF para permitir peticiones POST, PUT, DELETE sin token CSRF
        http.cors(org.springframework.security.config.Customizer.withDefaults()); // Habilitamos CORS dentro de Spring Security

        //Se cierra la config de seguridad y se devuelve para que Spring la utilice en el API Gateway
        return http.build();
    }

    // Convierte el JWT en authorities combinando scopes (permissions) y la claim `roles`.
    // Esto permite usar reglas como `.hasAuthority("ROLE_admin")` o `.hasAuthority("SCOPE_read:reportes")`.
    private ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        // mantenga el prefijo SCOPE_ para permisos/permissions
        scopesConverter.setAuthorityPrefix("SCOPE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter((Jwt jwt) -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Agregar scopes/permissions (si existen)
            Collection<GrantedAuthority> scopeAuth = scopesConverter.convert(jwt);
            if (scopeAuth != null) {
                authorities.addAll(scopeAuth);
            }

            // Agregar roles desde claim `roles` (lista de strings) — mapeados a ROLE_<rol>
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                for (String r : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
                }
            }

            // Algunos tokens pueden usar `permissions` o `scope` claim; agregar si existen
            List<String> permissions = jwt.getClaimAsStringList("permissions");
            if (permissions != null) {
                for (String p : permissions) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + p));
                }
            }

            return authorities;
        });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }


}