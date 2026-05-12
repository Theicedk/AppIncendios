# ms-geolocalizacion — PostGIS / JPA (Guía rápida)

Resumen
- Objetivo: configurar JPA/Hibernate para persistir `Point` (geom) en PostGIS y consumir mensajes Kafka que contienen latitud/longitud.

Paso 1 — Dependencia (pom.xml)
Agrega estas dependencias en `ms-geolocalizacion/pom.xml`:

```xml
<dependency>
  <groupId>org.hibernate.orm</groupId>
  <artifactId>hibernate-spatial</artifactId>
</dependency>
<dependency>
  <groupId>org.locationtech.jts</groupId>
  <artifactId>jts-core</artifactId>
  <version>1.20.0</version>
</dependency>
```

Por qué: JPA "puro" no entiende tipos geométricos de PostGIS (geometry(Point, SRID)). `hibernate-spatial` implementa los mapeos y funciones necesarias; JTS (`jts-core`) aporta las clases `Point`, `Coordinate` y `GeometryFactory` para crear geometrías en Java.

Paso 2 — Entity espacial
Crea/ajusta la entity `FocoIncendio` (ejemplo en `src/main/java/.../entity/FocoIncendio.java`):

```java
package cl.duoc.valledelsol.ms_geolocalizacion.entity;

import org.locationtech.jts.geom.Point;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "foco_incendio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FocoIncendio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporte_id")
    private Long reporteId; // referencia huérfana, sin FK

    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point ubicacion;
}
```

Nota: `columnDefinition` fuerza la creación de la columna como `geometry(Point,4326)` para PostGIS (SRID WGS84).

Paso 3 — Conversor desde JSON (GeometryFactory + Kafka listener)
1) Define un `GeometryFactory` como `@Bean` para inyectarlo: `GeometryConfig.java`:

```java
@Configuration
public class GeometryConfig {
    @Bean
    GeometryFactory geometryFactory() {
        return new GeometryFactory(new PrecisionModel(), 4326);
    }
}
```

2) Listener Kafka (ejemplo minimal en `consumer/FocoIncendioKafkaListener.java`):

```java
@Component
public class FocoIncendioKafkaListener {
    private final ObjectMapper objectMapper;
    private final GeometryFactory geometryFactory;
    private final FocoIncendioRepository repo;

    public FocoIncendioKafkaListener(ObjectMapper objectMapper,
                                     GeometryFactory geometryFactory,
                                     FocoIncendioRepository repo) {
        this.objectMapper = objectMapper;
        this.geometryFactory = geometryFactory;
        this.repo = repo;
    }

    @KafkaListener(topics = "topic-prueba-incendio", groupId = "ms-geolocalizacion")
    public void escucharFoco(String mensaje) throws Exception {
        ReporteIncendioKafkaDTO dto = objectMapper.readValue(mensaje, ReporteIncendioKafkaDTO.class);

        if (dto.latitud() == null || dto.longitud() == null) {
            throw new IllegalArgumentException("Latitud y longitud son obligatorias");
        }

        // IMPORTANTE: JTS usa (x=longitud, y=latitud)
        Point ubicacion = geometryFactory.createPoint(new Coordinate(dto.longitud(), dto.latitud()));
        ubicacion.setSRID(4326);

        FocoIncendio foco = new FocoIncendio();
        foco.setReporteId(dto.reporteId());
        foco.setUbicacion(ubicacion);

        repo.save(foco);
    }
}
```

Prueba rápida
1) Usando el endpoint de `ms-reportes` (recomendado):

```bash
curl -X POST http://localhost:8081/api/reportes/test \
  -H 'Content-Type: application/json' \
  -d '{
    "encabezado":"Fuego en bosque",
    "descripcion":"Humo visible",
    "latitud":-33.45,
    "longitud":-70.66,
    "verificado": false,
    "estadoIncendio": "REPORTADO"
  }'
```

2) Directo a Kafka (si usas herramientas de Kafka):

```bash
kafka-console-producer --broker-list localhost:9092 --topic topic-prueba-incendio <<EOF
{"encabezado":"Fuego","descripcion":"Humo","latitud":-33.45,"longitud":-70.66}
EOF
```

Verificación en BD (psql):
```sql
SELECT id, reporte_id, ST_AsText(ubicacion) FROM foco_incendio ORDER BY id DESC LIMIT 5;
```

Errores clásicos y advertencias
- Usar `org.springframework.data.geo.Point` en lugar de `org.locationtech.jts.geom.Point` → provoca mapeos incorrectos.
- Invertir latitud/longitud al crear `Coordinate` (JTS: x=longitud).
- Olvidar `setSRID(4326)` y tener SRID inconsistente con la columna.
- No tener PostGIS instalado en la BD (`CREATE EXTENSION postgis;`).
- NullPointer: inyecta `GeometryFactory` como bean; cuidado con crear uno manualmente en cada llamada (es barato, pero la inyección evita errores de null).
- Si quieres que `reporteId` no sea null, modifica `ms-reportes` para publicar `reporteId` o implementa Outbox.

Configuración útil en `application.yml`
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.spatial.dialect.postgis.PostgisPG10Dialect
```

Notas finales
- Con `hibernate-spatial` + JTS ya puedes usar funciones espaciales en consultas JPA (ST_DWithin, ST_Distance, etc.).
- Mantén el patrón Database-per-Service: `reporteId` es solo un identificador, nunca una FK a otra base.

---
Generado y verificado localmente. Si quieres, añado ejemplos de consultas espaciales JPA. Happy testing.
