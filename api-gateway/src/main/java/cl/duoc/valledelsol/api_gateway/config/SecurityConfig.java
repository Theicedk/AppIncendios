package cl.duoc.valledelsol.api_gateway.config;

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
    @Bean //Funcion que se encarga de guardar el resultado de la config de seguridad, para que luego pueda ser reutilizada en spring
    //Objeto que sirve como filtro de seguridad para las rutas del API Gateway, se encarga de validar los tokens JWT y verificar los permisos de acceso a las rutas protegidas
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // 1. Configuración de accesos a las rutas
            .authorizeExchange(exchanges -> exchanges
                //Rutas públicas (sin autenticación)
                 .pathMatchers(HttpMethod.GET,"/api/focos/**").permitAll()
                 .pathMatchers(HttpMethod.POST, "/api/reportes/**").permitAll()
                 .pathMatchers(HttpMethod.GET, "/api/bff/dashboard-combinado/**", "/api/bff/dashboard-combinado").permitAll()

                .anyExchange().authenticated() 
            )
            
            // 2. Le decimos a Spring Boot que valide los Tokens JWT usando Auth0
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    // Le inyectamos tu convertidor personalizado para que lea los ROLES y SCOPES de Auth0
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            
            // 3. Deshabilitar CSRF (Innecesario ya que usamos JWT)
            .csrf(csrf -> csrf.disable())
            
            // 4. Desabilitar Cors para que GlobalCorsConfig lo maneje
            .cors(cors -> cors.disable());
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