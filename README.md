# Cuerdas Guitarra

Web para consultar y registrar las compras y cambios de cuerdas de las guitarras
(eléctrica, acústica y clásica). Los datos iniciales vienen de `Cuerdas Guitarra (2026).xlsx`.

- **frontend/**: Angular 21 (standalone, signals, zoneless), servido por nginx.
- **backend/**: Spring Boot 4.1 (Java 25), API REST, JPA y Flyway.
- **db**: PostgreSQL 18. Las imágenes de los packs se guardan en la base de datos.

## Arrancar con Docker

```sh
docker compose up -d --build
```

Abre <http://localhost:8090>. El puerto se cambia con la variable `WEB_PORT`
(por ejemplo `WEB_PORT=80 docker compose up -d`).

La primera vez que el backend arranca con la base de datos vacía importa los 25 sets
del Excel, con sus imágenes. Los datos se guardan en el volumen `db-data`, así que
sobreviven a `docker compose down`. Para empezar de cero: `docker compose down -v`.

## API

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/cuerdas` | Lista todos los sets |
| GET | `/api/cuerdas/{id}` | Un set |
| POST | `/api/cuerdas` | Crea un set (JSON) |
| PUT | `/api/cuerdas/{id}` | Actualiza un set (JSON) |
| DELETE | `/api/cuerdas/{id}` | Borra un set |
| GET | `/api/cuerdas/{id}/imagen` | Imagen del set |
| PUT | `/api/cuerdas/{id}/imagen` | Sube imagen (multipart, campo `archivo`) |
| DELETE | `/api/cuerdas/{id}/imagen` | Quita la imagen |

Cuerpo JSON de un set:

```json
{
  "tipo": "ELECTRICA",
  "descripcion": "Ernie Ball 2221 Regular Slinky 10-46",
  "cantidad": 1,
  "fechaCompra": "2026-10-13",
  "fechaCambio": null,
  "notas": "Montadas en la Strato"
}
```

`tipo` es `ELECTRICA`, `ACUSTICA` o `CLASICA`.

## Desarrollo sin Docker

```sh
# Base de datos
docker compose up -d db   # o un PostgreSQL local con usuario/clave cuerdas

# Backend (http://localhost:8080)
cd backend && mvn spring-boot:run

# Frontend (http://localhost:4200, redirige /api al backend)
cd frontend && npm start
```

Para que el backend llegue a la base de datos del compose, publica el puerto 5432
añadiendo `ports: ["5432:5432"]` al servicio `db`.

## Cómo se importó el Excel

Las fechas del Excel (`DD.MM.AA`, columna «Compra / Cambio») se separan en fecha de
compra y fecha de cambio. Cuando solo hay una fecha se toma como fecha de compra.
Las fechas que no se pudieron interpretar (`00.08.13`, «Sin fecha compra») se
conservan en las notas del set. La cantidad sale de `(x2)`, `(x5)` o `(Pack 3)`.
