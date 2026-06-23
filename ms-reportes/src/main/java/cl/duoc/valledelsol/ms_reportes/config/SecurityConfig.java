package cl.duoc.valledelsol.ms_reportes.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;



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
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }
}
