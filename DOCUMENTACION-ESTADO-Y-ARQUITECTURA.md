# Documentación de Estado y Arquitectura

## Plataforma Inteligente para la Gestión y Prevención de Incendios
**Municipalidad Valle del Sol**
**Última actualización:** 12 de mayo de 2026

## 1. Resumen Ejecutivo

La plataforma gestiona reportes de incendios y su trazabilidad operacional mediante una arquitectura de microservicios desacoplada, orientada a eventos y con aislamiento estricto de persistencia.

Las decisiones arquitectónicas principales son las siguientes:

- **Spring Cloud Gateway** como único punto de entrada HTTP al sistema.
- **Kafka** como bus asíncrono para distribuir eventos entre microservicios.
- **Database per Service** para asegurar independencia funcional y evitar dependencias cruzadas a nivel de base de datos.
- **Spring Data JPA** para persistencia transaccional local en cada servicio.
- **PostGIS + Hibernate Spatial** en `ms-geolocalizacion` para soportar geometría espacial (`Point`).
- **DTOs obligatorios en la capa Service** para evitar exponer entidades JPA o tipos internos de persistencia al frontend.
- **Capa de 3 niveles** en backend: Controller, Service y Repository.
- **Resilience4j Circuit Breaker + fallbacks** configurados en el Gateway para rutas críticas.
- **BFF en el Gateway** (`/api/bff`) para componer datos del dashboard en una sola llamada.

El objetivo del sistema es consolidar un flujo confiable desde el registro de un reporte, su persistencia aislada, la emisión de eventos a Kafka y la reacción independiente de los servicios de alarmas y geolocalización.

---

## 2. Topología de Infraestructura

| Componente | Puerto | Persistencia | Tecnología principal | Responsabilidad |
| --- | ---: | --- | --- | --- |
| `api-gateway` | 8080 | No aplica | Spring Cloud Gateway | Entrada única, rutas, BFF y fallbacks; sin lógica de negocio persistente |
| `ms-reportes` | 8081 | PostgreSQL 15 (`bd_reportes`) en 5431 | Spring Boot 3, JPA, Kafka | Registra reportes, expone lectura, y publica eventos al verificar |
| `ms-alarmas` | 8082 | PostgreSQL 15 (`bd_alarmas`) en 5433 | Spring Boot 3, JPA, Kafka | Consume eventos y persiste alarmas localmente |
| `ms-geolocalizacion` | 8083 | PostgreSQL 15 + PostGIS (`bd_geolocalizacion`) en 5434 | Spring Boot 3, JPA, Hibernate Spatial, Kafka | Consume eventos y persiste focos con geometría espacial |
| Kafka Broker | 9092 | No aplica | Apache Kafka | Transporte asíncrono de eventos |
| Zookeeper | 2181 | No aplica | Zookeeper | Coordinación de Kafka |
| Cliente (externo) | N/A | N/A | React + Vite (fuera de este repo) | Interfaz web y consumo de APIs por Gateway |

### Aislamiento de persistencia

- `ms-reportes` usa exclusivamente su propia base de datos PostgreSQL.
- `ms-alarmas` usa exclusivamente su propia base de datos PostgreSQL.
- `ms-geolocalizacion` usa su propia base PostgreSQL con extensión PostGIS.
- No existen foreign keys entre bases de datos distintas.

---

## 3. Flujo de Datos Core

### 3.1 Registro de un reporte (creación)

1. El cliente envía un `POST /api/reportes` al `api-gateway`.
2. El Gateway enruta la solicitud hacia `ms-reportes`.
3. `ms-reportes` recibe el JSON como `ReporteCreacionDTO`.
4. La capa `Service` crea la entidad `Reporte` con estado `REPORTADO`.
5. JPA persiste la entidad en `bd_reportes`.
6. El response retorna un `ReporteListaDTO` para lectura rápida.
7. En esta etapa **no se publica evento** en Kafka.

### 3.2 Verificación y publicación en Kafka

1. El cliente invoca `PUT /api/reportes/{id}/verificar`.
2. `ms-reportes` actualiza el estado a `ACTIVO` y marca `verificado=true`.
3. Se publica un `ReporteKafkaEvent` en el topic `topic-prueba-incendio`.

### 3.3 Reacción de `ms-alarmas`

1. `ms-alarmas` consume el mensaje del topic.
2. El listener deserializa el mensaje a un DTO local de consumo.
3. Se construye una entidad `Alarma` con severidad `ROJA`.
4. JPA guarda la alarma en `bd_alarmas`.
5. El servicio conserva independencia del modelo de reportes.

### 3.4 Reacción de `ms-geolocalizacion`

1. `ms-geolocalizacion` consume el mismo mensaje Kafka.
2. El listener deserializa latitud y longitud desde el DTO.
3. La capa `Service` construye un `Point` JTS con `GeometryFactory`.
4. El `Point` se persiste en PostGIS como `geometry(Point, 4326)`.
5. El foco queda almacenado en `bd_geolocalizacion` sin exponer geometría directa al frontend.

### 3.5 BFF Dashboard (Gateway)

1. El cliente invoca `GET /api/bff/dashboard-combinado`.
2. El Gateway consulta `ms-reportes` (`GET /api/reportes`).
3. El Gateway consulta `ms-geolocalizacion` (`GET /api/focos`).
4. Se entrega un `DashboardDTO` con `reportes` y `focos` agregados.

### 3.6 Principio de diseño aplicado

La lógica transaccional no debe vivir en `Controller` ni en `@KafkaListener`. Estos componentes solo coordinan entrada/salida. La persistencia, el mapeo y la decisión de negocio residen en `Service`.

---

## 4. Estado de los Contratos API

### 4.1 Endpoints disponibles

#### `api-gateway`
- `GET /api/reportes/test` (ruta a `ms-reportes`)
- `GET /api/reportes`
- `POST /api/reportes`
- `PUT /api/reportes/{id}/verificar`
- `GET /api/focos`
- `GET /api/bff/dashboard-combinado`
- `GET /fallback/errorReportes` (fallback interno)
- `GET /fallback/errorAlarmas` (fallback interno)
- `GET /fallback/errorGeolocalizacion` (fallback interno)

> Nota: existe ruta `/api/alarmas/**` configurada en el Gateway, pero `ms-alarmas` no expone endpoints HTTP en el estado actual.

#### `ms-reportes`
- `GET /api/reportes/test`
- `GET /api/reportes`
- `POST /api/reportes`
- `PUT /api/reportes/{id}/verificar`

#### `ms-geolocalizacion`
- `GET /api/focos`

#### `ms-alarmas`
- Sin endpoints HTTP (consume eventos de Kafka)

### 4.2 Endpoints en desarrollo o próximos

- Endpoints HTTP de lectura para alarmas.
- Filtros por estado, fecha o proximidad geográfica para reportes y focos.
- CORS centralizado para el cliente web.
- Seguridad JWT en Gateway (configuración aún comentada).

### 4.3 DTOs de lectura y escritura

#### `ReporteCreacionDTO`
Usado para crear un reporte (POST).

```json
{
  "descripcion": "Humo visible cerca del sector norte",
  "latitud": -33.45,
  "longitud": -70.66
}
```

#### `ReporteListaDTO`
Usado para lectura en `ms-reportes` y respuesta de creación.

```json
{
  "id": 1,
  "descripcion": "Humo visible cerca del sector norte",
  "latitud": -33.45,
  "longitud": -70.66,
  "estado": "REPORTADO"
}
```

#### `ReporteDTO`
Usado como respuesta en la verificación y publicación de eventos.

```json
{
  "encabezado": "Reporte Ciudadano",
  "descripcion": "Humo visible cerca del sector norte",
  "latitud": -33.45,
  "longitud": -70.66,
  "verificado": true,
  "estadoIncendio": "ACTIVO"
}
```

#### `ReporteKafkaEvent`
Evento emitido a Kafka cuando un reporte es verificado.

```json
{
  "id": 1,
  "descripcion": "Humo visible cerca del sector norte",
  "latitud": -33.45,
  "longitud": -70.66
}
```

#### `FocoMapaDTO`
Usado para lectura en `ms-geolocalizacion`.

```json
{
  "id": 1,
  "latitud": -33.45,
  "longitud": -70.66,
  "estado": "ACTIVO"
}
```

#### `DashboardDTO`
Usado por el BFF del Gateway.

```json
{
  "reportes": [
    {
      "id": 1,
      "descripcion": "Humo visible cerca del sector norte",
      "latitud": -33.45,
      "longitud": -70.66,
      "estado": "REPORTADO"
    }
  ],
  "focos": [
    {
      "id": 10,
      "latitud": -33.45,
      "longitud": -70.66,
      "estado": "ACTIVO"
    }
  ]
}
```

### 4.4 Regla de serialización

- Nunca se expone una Entity JPA directamente en un `@GetMapping`.
- Nunca se expone un objeto geométrico `Point` en JSON.
- El `Point` se aplana a `latitud` y `longitud` antes de retornar al frontend.

---

## 5. Plan de Acción

### Prioridades inmediatas

- Publicar endpoints HTTP de lectura para `ms-alarmas`.
- Añadir filtros para consultas por estado/fecha/proximidad.
- Activar CORS en Gateway para consumo web.
- Definir estrategia de errores para Kafka (retry/DLQ).

### Mejoras de resiliencia y seguridad

- Activar configuración JWT en Gateway (actualmente comentada).
- Mantener `Circuit Breaker` y ajustar timeouts/ventanas según métricas reales.

---

## 6. Criterios de Calidad Aplicados

- Separación de responsabilidades por capa.
- Persistencia aislada por microservicio.
- Comunicación asíncrona por Kafka.
- DTOs como contrato estable hacia el frontend.
- Compatibilidad espacial real en `ms-geolocalizacion` con PostGIS.
- `open-in-view` desactivado en JPA para evitar lecturas perezosas fuera de servicio.

---

## 7. Estado Actual del Hito

El flujo principal ya está operativo:

- `POST` por Gateway hacia `ms-reportes` (creación sin Kafka).
- Persistencia local en `bd_reportes`.
- Publicación en Kafka solo al verificar (`PUT /api/reportes/{id}/verificar`).
- Consumo y persistencia en `ms-alarmas`.
- Consumo y persistencia espacial en `ms-geolocalizacion`.
- Lectura mediante `GET` usando DTOs.

El Gateway también expone un BFF (`/api/bff/dashboard-combinado`) que agrega reportes y focos para el dashboard.

El siguiente foco de trabajo es completar el frontend y estabilizar la experiencia de consumo de datos por el navegador.
