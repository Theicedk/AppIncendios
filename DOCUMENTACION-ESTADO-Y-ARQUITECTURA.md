# Documentación de Estado y Arquitectura

## Plataforma Inteligente para la Gestión y Prevención de Incendios
**Municipalidad Valle del Sol**

## 1. Resumen Ejecutivo

La plataforma se diseñó para gestionar reportes de incendios y su trazabilidad operacional mediante una arquitectura de microservicios desacoplada, orientada a eventos y con aislamiento estricto de persistencia.

Las decisiones arquitectónicas principales son las siguientes:

- **Spring Cloud Gateway** como único punto de entrada HTTP al sistema.
- **Kafka** como bus asíncrono para distribuir eventos entre microservicios.
- **Database per Service** para asegurar independencia funcional y evitar dependencias cruzadas a nivel de base de datos.
- **Spring Data JPA** para persistencia transaccional local en cada servicio.
- **PostGIS + Hibernate Spatial** en `ms-geolocalizacion` para soportar geometría espacial (`Point`).
- **DTOs obligatorios en la capa Service** para evitar exponer entidades JPA o tipos internos de persistencia al frontend.
- **Capa de 3 niveles** en backend: Controller, Service y Repository.

El objetivo del sistema es consolidar un flujo confiable desde el registro de un reporte, su persistencia aislada, la emisión de eventos a Kafka y la reacción independiente de los servicios de alarmas y geolocalización.

---

## 2. Topología de Infraestructura

| Componente | Puerto | Persistencia | Tecnología principal | Responsabilidad |
| --- | ---: | --- | --- | --- |
| `api-gateway` | 8080 | No aplica | Spring Cloud Gateway | Entrada única, ruteo HTTP, sin lógica de negocio |
| `ms-reportes` | 8081 | PostgreSQL 15 (`bd_reportes`) en 5431 | Spring Boot 3, JPA, Kafka | Registra reportes, publica eventos y expone lectura DTO |
| `ms-alarmas` | 8082 | PostgreSQL 15 (`bd_alarmas`) en 5433 | Spring Boot 3, JPA, Kafka | Consume eventos y persiste alarmas localmente |
| `ms-geolocalizacion` | 8083 | PostgreSQL 15 + PostGIS (`bd_geolocalizacion`) en 5434 | Spring Boot 3, JPA, Hibernate Spatial, Kafka | Consume eventos y persiste focos con geometría espacial |
| Kafka Broker | 9092 | No aplica | Apache Kafka | Transporte asíncrono de eventos |
| Zookeeper | 2181 | No aplica | Zookeeper | Coordinación de Kafka |
| Frontend | N/A | N/A | React + Vite | Interfaz web y consumo de APIs por Gateway |

### Aislamiento de persistencia

- `ms-reportes` usa exclusivamente su propia base de datos PostgreSQL.
- `ms-alarmas` usa exclusivamente su propia base de datos PostgreSQL.
- `ms-geolocalizacion` usa su propia base PostgreSQL con extensión PostGIS.
- No existen foreign keys entre bases de datos distintas.

---

## 3. Flujo de Datos Core

### 3.1 Registro de un reporte

1. El frontend envía un `POST` al `api-gateway`.
2. El Gateway enruta la solicitud hacia `ms-reportes`.
3. `ms-reportes` recibe el JSON como `ReporteDTO`.
4. La capa `Service` de reportes convierte el DTO en entidad `Reporte`.
5. El repositorio JPA persiste la entidad en `bd_reportes`.
6. El mismo flujo publica el evento en Kafka sobre el topic `topic-prueba-incendio`.
7. Los microservicios consumidores reaccionan de forma independiente.

### 3.2 Reacción de `ms-alarmas`

1. `ms-alarmas` consume el mensaje del topic.
2. El listener deserializa el mensaje a un DTO local de consumo.
3. La capa `Service` procesa el mapeo a la entidad `Alarma`.
4. JPA guarda la alarma en `bd_alarmas`.
5. El servicio conserva independencia del modelo de reportes.

### 3.3 Reacción de `ms-geolocalizacion`

1. `ms-geolocalizacion` consume el mismo mensaje Kafka.
2. El listener deserializa latitud y longitud desde el DTO.
3. La capa `Service` construye un `Point` JTS con `GeometryFactory`.
4. El `Point` se persiste en PostGIS como `geometry(Point, 4326)`.
5. El foco queda almacenado en `bd_geolocalizacion` sin exponer geometría directa al frontend.

### 3.4 Principio de diseño aplicado

La lógica transaccional no debe vivir en `Controller` ni en `@KafkaListener`. Estos componentes solo coordinan entrada/salida. La persistencia, el mapeo y la decisión de negocio residen en `Service`.

---

## 4. Estado de los Contratos API

### 4.1 Endpoints disponibles

#### `api-gateway`
- `GET /api/reportes/test`
- `POST /api/reportes/test`
- `GET /api/reportes/kafka-test`
- `GET /api/focos`
- `GET /api/reportes`

#### `ms-reportes`
- `GET /api/reportes/test`
- `POST /api/reportes/test`
- `GET /api/reportes/kafka-test`
- `GET /api/reportes`

#### `ms-geolocalizacion`
- `GET /api/focos`

### 4.2 Endpoints en desarrollo o próximos

- Rutas adicionales en Gateway para ampliar lectura y futuras pantallas.
- CORS centralizado para frontend.
- Componentes React exportables en modo Library (`.tgz`).
- Endpoints complementarios para filtros por estado, fecha o proximidad geográfica.

### 4.3 DTOs de lectura y escritura

#### `ReporteDTO`  
Usado para escritura y publicación de eventos.

```json
{
  "encabezado": "Reporte de incendio",
  "descripcion": "Humo visible cerca del sector norte",
  "latitud": -33.45,
  "longitud": -70.66,
  "verificado": false,
  "estadoIncendio": "REPORTADO"
}
```

#### `ReporteListaDTO`  
Usado para lectura en `ms-reportes`.

```json
{
  "id": 1,
  "descripcion": "Humo visible cerca del sector norte"
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

### 4.4 Regla de serialización

- Nunca se expone una Entity JPA directamente en un `@GetMapping`.
- Nunca se expone un objeto geométrico `Point` en JSON.
- El `Point` se aplana a `latitud` y `longitud` antes de retornar al frontend.

---

## 5. Plan de Acción

### Fase 9. Endpoints GET y lectura

- Consolidar lectura en DTOs.
- Mantener controllers delgados.
- Verificar que el Gateway enrute correctamente todas las rutas de lectura.

### Fase 10. Mapeo de entidades a DTOs

- Aplanar tipos internos de persistencia.
- En `ms-geolocalizacion`, convertir `Point` a `latitud` y `longitud`.
- En `ms-reportes`, exponer solo `id` y `descripcion` para la vista de lista.

### Fase 11. Integración frontend y Gateway

- Configurar rutas finales en el Gateway.
- Habilitar CORS para el frontend.
- Empaquetar React + Vite en modo Library.
- Exponer módulos como `MapaFocos` y `FormularioReporte`.

### Deuda técnica intencional

- **Resilience4j / Circuit Breaker** quedó pospuesto para iteraciones futuras.
- La decisión fue deliberada para no frenar el avance funcional del flujo principal.

---

## 6. Criterios de Calidad Aplicados

- Separación de responsabilidades por capa.
- Persistencia aislada por microservicio.
- Comunicación asíncrona por Kafka.
- DTOs como contrato estable hacia el frontend.
- Compatibilidad espacial real en `ms-geolocalizacion` con PostGIS.

---

## 7. Estado Actual del Hito

El flujo principal ya está operativo:

- `POST` por Gateway hacia `ms-reportes`.
- Persistencia local en `bd_reportes`.
- Publicación en Kafka.
- Consumo y persistencia en `ms-alarmas`.
- Consumo y persistencia espacial en `ms-geolocalizacion`.
- Lectura mediante `GET` usando DTOs.

El siguiente foco de trabajo es completar el frontend y estabilizar la experiencia de consumo de datos por el navegador.
