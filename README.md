# Renova API

API REST (Spring Boot) para la gestión de inversiones de Renova: consulta pública por
código de seguimiento (NIP) y panel de administración (CRUD) para el equipo de Renova.

## Requisitos

- Java 21
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
| `SPRING_DATASOURCE_URL` | URL JDBC de la base de datos | `jdbc:h2:file:./data/renova;AUTO_SERVER=TRUE` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | _(vacío)_ |
| `SPRING_DATASOURCE_DRIVER` | Driver JDBC (`org.h2.Driver` o `org.postgresql.Driver`) | `org.h2.Driver` |

**Importante para producción:** define `ADMIN_USERNAME`, `ADMIN_PASSWORD`,
`ADMIN_TOKEN_SECRET`, `CORS_ALLOWED_ORIGINS` y las variables `SPRING_DATASOURCE_*`
con valores propios. No dejes los valores por defecto.

Por defecto (sin las variables `SPRING_DATASOURCE_*`) la base de datos es H2
embebida y persiste en el archivo `./data/renova.mv.db` (se crea automáticamente).
Al primer arranque se crea una inversión de ejemplo. **En Render usa siempre
PostgreSQL** (ver sección "Despliegue en Render"), ya que el sistema de archivos
no es persistente entre despliegues/reinicios.

## Ejecutar en desarrollo

```bash
./mvnw spring-boot:run
```

La API quedará disponible en `http://localhost:8080`.

## Compilar para producción

```bash
./mvnw clean package -DskipTests
java -jar target/renova-0.0.1-SNAPSHOT.jar
```

(exporta las variables de entorno antes de ejecutar el `.jar`, o pásalas como
`--ADMIN_USERNAME=... --ADMIN_PASSWORD=...` etc.)

## Despliegue en Render

La API se despliega como un servicio web de Java (Maven). Render expone el
puerto mediante la variable `PORT`, que la app ya soporta (`server.port=${PORT:8080}`).

### Variables de entorno necesarias

```txt
ADMIN_USERNAME=admin
ADMIN_PASSWORD=una-clave-segura
ADMIN_TOKEN_SECRET=un-secreto-largo-y-random
ADMIN_TOKEN_TTL_MINUTES=480

CORS_ALLOWED_ORIGINS=https://URL-DEL-FRONT.vercel.app,http://localhost:3000

SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:PORT/DATABASE
SPRING_DATASOURCE_USERNAME=USUARIO
SPRING_DATASOURCE_PASSWORD=PASSWORD
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
```

Estos valores normalmente se obtienen de la base de datos PostgreSQL que crees
en Render (pestaña "Connect" / "Internal Database URL", separando host, puerto,
nombre de base, usuario y password).

### Build command

```bash
chmod +x mvnw && ./mvnw clean package -DskipTests
```

### Start command

```bash
java -jar target/renova-0.0.1-SNAPSHOT.jar
```

### Notas

- Hibernate (`spring.jpa.hibernate.ddl-auto=update`) crea/actualiza las tablas
  automáticamente en PostgreSQL al iniciar, igual que hace con H2 en local.
- Si Render reinicia o redepliega el servicio, los datos persisten porque
  viven en la base PostgreSQL administrada, no en el sistema de archivos del
  servicio web.
- El frontend (Vercel) debe apuntar a la URL pública de Render mediante
  `NEXT_PUBLIC_API_URL=https://URL-DE-RENDER.onrender.com`.

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
