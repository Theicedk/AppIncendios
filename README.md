 # AppIncendios - Municipalidad Valle del Sol

Proyecto de microservicios para la gestión de incendios con arquitectura basada en Spring Cloud Gateway, Spring Boot, PostgreSQL, PostGIS y Kafka.

## Arquitectura

- `api-gateway`: punto de entrada único del sistema.
- `ms-reportes`: microservicio de reportes.
- `ms-alarmas`: microservicio de alarmas.
- `ms-geolocalizacion`: microservicio geoespacial con PostGIS.
- `frontend`: aplicación web React.

## 🚀 Setup Rápido del Entorno

### Prerrequisitos

- Docker Desktop ejecutándose.
- Java 17 (JDK) instalado.
- VS Code con `Extension Pack for Java` y `Spring Boot Extension Pack`.

### 1. Levantar Infraestructura (BDs y Kafka)

Abrir una terminal en la raíz del proyecto y ejecutar:

```bash
docker-compose up -d
```

Esto levanta las bases de datos PostgreSQL necesarias para cada microservicio y el broker de Kafka.

### 2. Levantar Microservicios

Abrir las 4 carpetas de Spring Boot en VS Code como workspace. Ejecutar la clase main de cada una en este orden:

1. `api-gateway` (Puerto 8080)
2. `ms-reportes` (Puerto 8081)
3. `ms-alarmas` (Puerto 8082)
4. `ms-geolocalizacion` (Puerto 8083)

### 3. Validar Arquitectura

Ingresar en el navegador o en Postman a:

```text
http://localhost:8080/api/reportes/test
```

Si retorna el mensaje de OK, el Gateway está enrutando correctamente hacia `ms-reportes`.

## Configuración local de cada servicio

### `api-gateway`

- Puerto: `8080`
- No usa base de datos
- No usa JPA
- No usa Kafka
- Su función es solo enrutar peticiones

### `ms-reportes`

- Puerto: `8081`
- Base de datos: `bd_reportes`
- Conexión PostgreSQL: `localhost:5431`
- JPA con `ddl-auto: update`

### `ms-alarmas`

- Puerto: `8082`
- Base de datos: `bd_alarmas`
- Conexión PostgreSQL: `localhost:5433`
- JPA con `ddl-auto: update`

### `ms-geolocalizacion`

- Puerto: `8083`
- Base de datos: `bd_geolocalizacion`
- Conexión PostgreSQL / PostGIS: `localhost:5434`
- JPA con `ddl-auto: update`
- Usa `hibernate-spatial` para soporte geoespacial

## Estructura del workspace

El proyecto está pensado para trabajar con Multi-root Workspace en VS Code:

- `api-gateway/`
- `ms-reportes/`
- `ms-alarmas/`
- `ms-geolocalizacion/`
- `frontend/`

Esto permite mantener cada componente aislado y, al mismo tiempo, visible en una sola ventana de trabajo.

## Notas para continuar trabajando

- Mantener `api-gateway` sin lógica de negocio.
- Usar PostgreSQL en todos los microservicios.
- Mantener `ms-geolocalizacion` sobre PostGIS.
- Usar Kafka para comunicación asíncrona entre microservicios cuando el caso de uso lo requiera.

## Guía breve para nuevos integrantes

1. Clonar el repositorio.
2. Abrir el workspace en VS Code.
3. Levantar la infraestructura con `docker-compose up -d`.
4. Ejecutar los microservicios en el orden indicado.
5. Probar primero el endpoint de prueba de reportes a través del Gateway.
