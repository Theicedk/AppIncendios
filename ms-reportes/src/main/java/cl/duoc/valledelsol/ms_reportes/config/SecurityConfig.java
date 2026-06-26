package cl.duoc.valledelsol.ms_reportes.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desactivamos CSRF ya que es una API REST sin estado
            .csrf(csrf -> csrf.disable())
            
            // Configuramos los permisos de las rutas
            .authorizeHttpRequests(auth -> auth
                // Dejamos pasar los GETs a reportes sin necesidad de token
                .requestMatchers(HttpMethod.GET, "/api/reportes", "/api/reportes/**").permitAll()
                
                // Cualquier otra petición (como POST o PUT) exigirá estar autenticado
                .anyRequest().authenticated()
            )
            
            // Configuramos el microservicio para que valide los tokens JWT de Auth0
            .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        return http.build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        // Le decimos a Spring que los permisos vienen en la llave "permissions" de Auth0
        converter.setAuthoritiesClaimName("permissions"); 
        // Le quitamos el prefijo "SCOPE_" por defecto, para que busque el texto exacto
        converter.setAuthorityPrefix(""); 

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }
}
