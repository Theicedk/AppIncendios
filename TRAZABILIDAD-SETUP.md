# Trazabilidad de Setup - Municipalidad Valle del Sol

## Objetivo
Este documento resume cómo quedó mapeada la arquitectura local del proyecto para mantener trazabilidad entre el diagrama, los microservicios y la configuración en VS Code/Docker.

## Matriz de trazabilidad

| Componente | Proyecto | Puerto | Base de datos | Observaciones |
| --- | --- | ---: | --- | --- |
| API Gateway | `api-gateway` | 8080 | No aplica | Entrada única al sistema, sin persistencia. |
| Microservicio de Reportes | `ms-reportes` | 8081 | `bd_reportes` en `localhost:5431` | JPA/Hibernate con `ddl-auto: update`. |
| Microservicio de Alarmas | `ms-alarmas` | 8082 | `bd_alarmas` en `localhost:5433` | JPA/Hibernate con `ddl-auto: update`. |
| Microservicio de Geolocalización | `ms-geolocalizacion` | 8083 | `bd_geolocalizacion` en `localhost:5434` | Usa PostGIS con `hibernate-spatial` y `ddl-auto: update`. |

## Infraestructura local

- PostgreSQL para cada microservicio con usuarios y contraseñas de desarrollo.
- Kafka en `localhost:9092` para comunicación asíncrona entre microservicios.
- PostGIS habilitado para el servicio de geolocalización.

## Configuración aplicada por servicio

### `ms-reportes`
- `spring.application.name = ms-reportes`
- `server.port = 8081`
- `spring.datasource.url = jdbc:postgresql://localhost:5431/bd_reportes`
- `spring.jpa.hibernate.ddl-auto = update`
- `spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect`

### `ms-alarmas`
- `spring.application.name = ms-alarmas`
- `server.port = 8082`
- `spring.datasource.url = jdbc:postgresql://localhost:5433/bd_alarmas`
- `spring.jpa.hibernate.ddl-auto = update`
- `spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect`

### `ms-geolocalizacion`
- `spring.application.name = ms-geolocalizacion`
- `server.port = 8083`
- `spring.datasource.url = jdbc:postgresql://localhost:5434/bd_geolocalizacion`
- `spring.jpa.hibernate.ddl-auto = update`
- `spring.jpa.properties.hibernate.dialect = org.hibernate.spatial.dialect.postgis.PostgisPG10Dialect`

### `api-gateway`
- `spring.application.name = api-gateway`
- `server.port = 8080`
- Sin base de datos
- Sin JPA
- Sin Kafka

## Nota de trazabilidad
El gateway solo enruta tráfico y los microservicios contienen la lógica de dominio y persistencia. Esta separación evita mezclar responsabilidades y facilita la revisión académica.
