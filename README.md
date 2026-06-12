# Renova API

API REST (Spring Boot) para la gestión de inversiones de Renova: consulta pública por
código de seguimiento (NIP) y panel de administración (CRUD) para el equipo de Renova.

## Requisitos

- Java 25
- Maven (o usar el wrapper `./mvnw` incluido)

## Configuración

La configuración se encuentra en `src/main/resources/application.properties` y puede
sobreescribirse con variables de entorno (recomendado en producción):

| Variable | Descripción | Valor por defecto |
| --- | --- | --- |
| `ADMIN_USERNAME` | Usuario del panel de administración | `admin` |
| `ADMIN_PASSWORD` | Contraseña del panel de administración | `cambia-esta-clave` |
| `ADMIN_TOKEN_SECRET` | Secreto para firmar los tokens de sesión del admin | `cambia-este-secreto-por-uno-largo-y-aleatorio` |
| `ADMIN_TOKEN_TTL_MINUTES` | Duración de la sesión del admin, en minutos | `480` |
| `CORS_ALLOWED_ORIGINS` | Dominios permitidos para consumir la API (separados por coma) | `http://localhost:3000` |
| `PORT` | Puerto del servidor | `8080` |

**Importante para producción:** define `ADMIN_USERNAME`, `ADMIN_PASSWORD`,
`ADMIN_TOKEN_SECRET` y `CORS_ALLOWED_ORIGINS` con valores propios. No dejes los
valores por defecto.

La base de datos es H2 embebida y persiste en el archivo `./data/renova.mv.db`
(se crea automáticamente). Al primer arranque se crea una inversión de ejemplo.

## Ejecutar en desarrollo

```bash
./mvnw spring-boot:run
```

La API quedará disponible en `http://localhost:8080`.

## Compilar para producción

```bash
./mvnw clean package
java -jar target/renova-0.0.1-SNAPSHOT.war \
  --ADMIN_USERNAME=... --ADMIN_PASSWORD=... --ADMIN_TOKEN_SECRET=...
```

(o exporta las variables de entorno antes de ejecutar el `.jar`/`.war`).

## Endpoints

### Públicos

- `GET /api/inversiones/{codigo}` — devuelve el estado de la inversión asociada al
  código (NIP) de seguimiento.

### Administración (requieren `Authorization: Bearer <token>`)

- `POST /api/admin/auth/login` — `{ "username": "...", "password": "..." }` → `{ "token": "...", "tokenType": "Bearer", "expiresInSeconds": ... }`
- `GET /api/admin/inversiones` — lista todas las inversiones (resumen)
- `GET /api/admin/inversiones/{id}` — detalle de una inversión
- `POST /api/admin/inversiones` — crea una inversión. Si `codigoSeguimiento` se omite,
  se genera automáticamente un NIP de 9 dígitos.
- `PUT /api/admin/inversiones/{id}` — actualiza una inversión (datos generales y pasos
  de seguimiento)
- `DELETE /api/admin/inversiones/{id}` — elimina una inversión
