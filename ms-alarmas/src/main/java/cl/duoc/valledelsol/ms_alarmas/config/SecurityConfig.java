package cl.duoc.valledelsol.ms_alarmas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Activar y configurar CORS para interceptar peticiones cruzadas de React
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Deshabilitar CSRF
            .csrf(csrf -> csrf.disable())
            
            // 3. Reglas de autorización con coincidencia estricta de rutas
            .authorizeHttpRequests(auth -> auth
                // SOLUCIÓN AL 401: Declaramos la ruta base exacta Y sus subrutas de forma explícita
                .requestMatchers("/ws/alarmas", "/ws/alarmas/**").permitAll()
                
                // Cualquier otra petición REST requerirá token JWT de Auth0
                .anyRequest().authenticated()
            )
            
            // 4. Política de sesión sin estado
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 5. Servidor de recursos para Auth0
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir el origen de tu frontend de Vite
        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        
        // CRÍTICO PARA WEBSOCKETS / SOCKJS: Permite el envío de credenciales de conexión
        configuration.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}