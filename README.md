# Task Manager API

API RESTful para gestión de tareas desarrollada como desafío técnico para **Nuevo SPA**.
Permite crear, listar, actualizar y eliminar tareas con autenticación mediante JWT.

---

## Tabla de contenidos

- [Descripción de la solución](#descripción-de-la-solución)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Decisiones técnicas destacadas](#decisiones-técnicas-destacadas)
- [Requisitos previos](#requisitos-previos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Credenciales de prueba](#credenciales-de-prueba)
- [Guía de pruebas paso a paso](#guía-de-pruebas-paso-a-paso)
- [Consola H2](#consola-h2)
- [Ejemplos de uso con cURL](#ejemplos-de-uso-con-curl)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Ejecución de tests](#ejecución-de-tests)
- [Información del autor](#información-del-autor)

---

## Descripción de la solución

El sistema permite a usuarios autenticados gestionar tareas a través de una API RESTful.
Cada tarea tiene un título, descripción opcional y un estado (`PENDING`, `IN_PROGRESS`, `COMPLETED`).

La solución fue desarrollada siguiendo la metodología **API First** — el contrato de la API
(`openapi.yml`) es la fuente de verdad. Las interfaces de los controllers se generan
automáticamente desde ese contrato mediante el plugin `openapi-generator-maven-plugin`,
garantizando que la documentación y el código nunca puedan divergir.

> **Nota sobre el idioma del código:** Todo el código fuente, nombres de clases, variables y
> métodos está en inglés siguiendo las convenciones estándar de Java. Los únicos nombres en
> español son los de las tablas de la base de datos (`usuarios`, `tareas`, `estados_tarea`),
> respetando la especificación del enunciado del desafío.

---

## Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 (Temurin) | Lenguaje principal |
| Spring Boot | 3.5.13 | Framework base |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Capa de persistencia |
| H2 Database | In-memory | Base de datos |
| jjwt | 0.12.6 | Generación y validación de JWT |
| springdoc-openapi | 2.8.8 | Swagger UI |
| openapi-generator-maven-plugin | 7.12.0 | Generación de interfaces desde contrato |
| Lombok | Latest | Reducción de boilerplate |
| Maven | 3.9.6 | Gestión de dependencias |
| JUnit 5 + Mockito | Latest | Tests unitarios |

---

## Arquitectura

El proyecto sigue una arquitectura en capas con separación estricta de responsabilidades:

```
HTTP Request
     ↓
Controller        → implementa interfaces generadas desde openapi.yml
     ↓
Service           → interfaz + implementación (inversión de dependencias)
     ↓
Repository        → Spring Data JPA
     ↓
Entity / DB       → H2 in-memory
```

### Estructura de paquetes

```
com.nuevospa.taskmanager/
├── controller/          # Controllers que implementan interfaces OpenAPI generadas
├── service/             # Interfaces de servicio
│   └── impl/            # Implementaciones de los servicios
├── repository/          # Repositorios Spring Data JPA
├── entity/              # Entidades JPA (mapean las tablas de la DB)
├── mapper/              # Conversión de entidades a modelos de respuesta
├── model/
│   └── generated/       # Modelos generados automáticamente desde openapi.yml
├── security/            # JWT filter, UserDetailsService, SecurityConfig
├── exception/           # Excepciones custom y GlobalExceptionHandler
└── config/              # OpenApiConfig (Swagger UI con JWT)
```

### Modelo de datos

```
estados_tarea          usuarios
─────────────          ────────────────
id (PK)                id (PK)
name                   username (unique)
                       password (BCrypt)
                       full_name

tareas
──────────────────────
id (PK)
title
description
status_id (FK → estados_tarea)
user_id   (FK → usuarios)
created_at
updated_at
```

---

## Decisiones técnicas destacadas

### API First
Se utilizó la metodología API First con `openapi-generator-maven-plugin`. El archivo
`src/main/resources/openapi.yml` define el contrato completo de la API. El plugin genera
las interfaces `AuthenticationApi` y `TasksApi` en tiempo de compilación. Los controllers
implementan esas interfaces — si el contrato cambia y el código no lo cumple, el proyecto
no compila.

### JWT Stateless
La autenticación es completamente stateless — no hay sesiones del lado del servidor.
Cada request incluye el token JWT en el header `Authorization: Bearer <token>`.
El filtro `JwtAuthenticationFilter` valida el token en cada request antes de que llegue
al controller.

### Java 21
Se aprovechan features modernas del lenguaje:
- **Records:** `TaskValidationData` como Record interno en `TaskServiceImpl` para
  encapsular datos relacionados con inmutabilidad garantizada por el lenguaje.
- **Streams y lambdas:** En `TaskMapper.toResponseList()`, `GlobalExceptionHandler`
  y `UserDetailsServiceImpl`.
- **`HexFormat`** (Java 17+): Para conversión del secret JWT de hex a bytes.
- **`toList()`** (Java 16+): En streams, más conciso que `collect(Collectors.toList())`.

### Seguridad
- No se usa `WebSecurityConfigurerAdapter` (deprecado en Spring Security 5.7,
  eliminado en 6). Se usa `SecurityFilterChain` bean.
- Passwords almacenados con BCrypt — nunca en texto plano.
- Swagger UI correctamente integrado con Spring Security mediante `OpenApiConfig`.
- `AuthenticationEntryPoint` configurado explícitamente para retornar `401` en lugar
  del `403` por defecto de Spring Security en requests sin autenticación.
- `DaoAuthenticationProvider` configurado explícitamente con `ProviderManager` para
  garantizar que el `BCryptPasswordEncoder` correcto se usa en la comparación de credenciales.

### Manejo de errores
`GlobalExceptionHandler` con `@RestControllerAdvice` centraliza el manejo de todas
las excepciones. Retorna siempre el mismo formato `ErrorResponse` con `message`,
`status` y `timestamp`. Los errores internos nunca exponen detalles de implementación
al cliente.

---

## Requisitos previos

- **Java 21** — [Descargar Temurin 21](https://adoptium.net/)
- **Maven 3.9+** — [Descargar Maven](https://maven.apache.org/download.cgi)

Verificar instalación:

```bash
java -version
# java version "21.0.x"

mvn -version
# Apache Maven 3.9.x
```

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/castronovomartin/desafio-spring-boot.git
cd desafio-spring-boot
git checkout feature/martin-castronovo
```

### 2. Compilar el proyecto

```bash
mvn clean install -DskipTests
```

> Este comando compila el proyecto **y genera las interfaces OpenAPI** en
> `target/generated-sources/openapi`. Es un paso necesario antes de ejecutar.

### 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`.

> **Importante:** La base de datos H2 es in-memory — se resetea en cada reinicio.
> Los datos precargados (`usuarios`, `estados_tarea` y tareas de ejemplo) se recrean
> automáticamente desde `schema.sql` y `data.sql` en cada arranque.

---

## Credenciales de prueba

La base de datos se inicializa automáticamente con los siguientes usuarios precargados.
Todos tienen el mismo password por simplicidad del desafío:

| Username | Password | Nombre |
|---|---|---|
| `admin` | `password` | Administrator |
| `user1` | `password` | User One |
| `user2` | `password` | User Two |

Los estados de tarea disponibles son:

| ID | Estado |
|---|---|
| 1 | `PENDING` |
| 2 | `IN_PROGRESS` |
| 3 | `COMPLETED` |

---

## Guía de pruebas paso a paso

### Acceder a Swagger UI

Una vez que la aplicación esté corriendo, abrí en el browser:

```
http://localhost:8080/swagger-ui.html
```

Vas a ver la interfaz con dos secciones: **Authentication** y **Tasks**, y el botón
**Authorize** 🔒 arriba a la derecha.

---

### Paso 1 — Obtener el token JWT

1. Expandí `POST /auth/login` haciendo click en el endpoint
2. Hacé click en **Try it out**
3. Reemplazá el contenido del Request body con:
```json
{
  "username": "admin",
  "password": "password"
}
```
4. Hacé click en **Execute**
5. En **Server response** vas a ver `Code: 200` con este body:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```
6. Copiá el valor completo del campo `token`

---

### Paso 2 — Autenticarse en Swagger UI

1. Hacé click en el botón **Authorize** 🔒 arriba a la derecha
2. En el campo **Value** pegá el token copiado — **solo el token, sin escribir "Bearer" adelante**
3. Hacé click en **Authorize**
4. Hacé click en **Close**

A partir de este momento todos los endpoints con el candado 🔒 van a funcionar
correctamente. Swagger agrega el header `Authorization: Bearer <token>` automáticamente.

---

### Paso 3 — Listar todas las tareas

1. Expandí `GET /tasks`
2. Hacé click en **Try it out** → **Execute**
3. **Resultado esperado (`200 OK`):**
```json
[
  {
    "id": 1,
    "title": "Setup project infrastructure",
    "description": "Initialize repository, configure CI/CD pipeline",
    "status": "PENDING",
    "created_at": "2026-04-11T14:30:17.671675Z",
    "updated_at": "2026-04-11T14:30:17.671675Z"
  },
  {
    "id": 2,
    "title": "Design database schema",
    "description": "Define all entities and relationships for the system",
    "status": "IN_PROGRESS",
    "created_at": "2026-04-11T14:30:17.671675Z",
    "updated_at": "2026-04-11T14:30:17.671675Z"
  },
  {
    "id": 3,
    "title": "Implement authentication",
    "description": "JWT login endpoint and security filter chain",
    "status": "COMPLETED",
    "created_at": "2026-04-11T14:30:17.671675Z",
    "updated_at": "2026-04-11T14:30:17.671675Z"
  }
]
```

> **Nota:** Las tres tareas precargadas se insertan con `NOW()` en el mismo instante
> al arrancar la aplicación. Como sus timestamps son idénticos, el ordenamiento
> por `created_at` descendente las muestra en orden de inserción. Con tareas creadas
> en momentos distintos el orden descendente (más reciente primero) funcionaría correctamente.

---

### Paso 4 — Obtener tarea por ID

1. Expandí `GET /tasks/{id}`
2. **Try it out** → ingresá `1` en el campo `id` → **Execute**
3. **Resultado esperado (`200 OK`):** la tarea con `id: 1`

Para probar el caso de error, ingresá `999`:
- **Resultado esperado (`404`):**
```json
{
  "message": "Task not found with id: 999",
  "status": 404,
  "timestamp": "..."
}
```

---

### Paso 5 — Crear una nueva tarea

1. Expandí `POST /tasks`
2. **Try it out** → reemplazá el body con:
```json
{
  "title": "Mi nueva tarea",
  "description": "Descripción de la tarea",
  "status_id": 1
}
```
3. **Execute**
4. **Resultado esperado (`201 Created`):**
```json
{
  "id": 4,
  "title": "Mi nueva tarea",
  "description": "Descripción de la tarea",
  "status": "PENDING",
  "created_at": "...",
  "updated_at": "..."
}
```

Para probar validaciones:

**Sin título (`400`):**
```json
{
  "description": "Sin título",
  "status_id": 1
}
```
Respuesta: `"message": "title: no debe ser nulo"`

**Con status inexistente (`400`):**
```json
{
  "title": "Tarea válida",
  "status_id": 99
}
```
Respuesta: `"message": "Invalid status id: 99"`

---

### Paso 6 — Actualizar una tarea

1. Expandí `PUT /tasks/{id}`
2. **Try it out** → ingresá `4` en el campo `id` → body:
```json
{
  "title": "Mi nueva tarea",
  "description": "Descripción actualizada",
  "status_id": 2
}
```
3. **Execute**
4. **Resultado esperado (`200 OK`):**
```json
{
  "id": 4,
  "title": "Mi nueva tarea",
  "description": "Descripción actualizada",
  "status": "IN_PROGRESS",
  "created_at": "...",
  "updated_at": "..."
}
```

Notá que `status` cambió de `PENDING` a `IN_PROGRESS`.

---

### Paso 7 — Eliminar una tarea

1. Expandí `DELETE /tasks/{id}`
2. **Try it out** → ingresá `4` → **Execute**
3. **Resultado esperado (`204 No Content`)** — sin body en la respuesta
4. Para verificar que fue eliminada, ejecutá `GET /tasks/4` — debería devolver `404`

---

### Paso 8 — Probar sin autenticación

1. Hacé click en **Authorize** 🔒 → **Logout** → **Close**
2. Ejecutá `GET /tasks`
3. **Resultado esperado (`401 Unauthorized`)** — el endpoint rechaza el request sin token

---

## Consola H2

La consola web de H2 permite inspeccionar la base de datos directamente desde el browser.
Está disponible mientras la aplicación esté corriendo.

### Cómo conectarse

1. Abrí en el browser:
```
http://localhost:8080/h2-console
```

2. Completá los campos exactamente así:

| Campo | Valor |
|---|---|
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:mem:taskmanagerdb` |
| User Name | `sa` |
| Password | *(dejar vacío)* |

3. Hacé click en **Connect**

### Queries útiles para verificar el estado

**Ver todos los usuarios:**
```sql
SELECT id, username, full_name FROM usuarios;
```

**Ver todos los estados de tarea:**
```sql
SELECT * FROM estados_tarea;
```

**Ver todas las tareas:**
```sql
SELECT * FROM tareas;
```

**Ver tareas con sus estados y usuarios (JOIN):**
```sql
SELECT
    t.id,
    t.title,
    t.description,
    e.name AS status,
    u.username AS created_by,
    t.created_at,
    t.updated_at
FROM tareas t
JOIN estados_tarea e ON t.status_id = e.id
JOIN usuarios u ON t.user_id = u.id
ORDER BY t.created_at DESC;
```

> **Recordá:** La base de datos es in-memory — todos los datos se pierden al reiniciar
> la aplicación y se recrean automáticamente desde los scripts SQL.

---

## Ejemplos de uso con cURL

### 1. Autenticación

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```

> Guardá el token:
> ```bash
> TOKEN="eyJhbGciOiJIUzI1NiJ9..."
> ```

---

### 2. Listar tareas

```bash
curl -X GET http://localhost:8080/tasks \
  -H "Authorization: Bearer $TOKEN"
```

---

### 3. Obtener tarea por ID

```bash
curl -X GET http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 4. Crear tarea

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nueva tarea",
    "description": "Descripción",
    "status_id": 1
  }'
```

---

### 5. Actualizar tarea

```bash
curl -X PUT http://localhost:8080/tasks/4 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nueva tarea",
    "description": "Descripción actualizada",
    "status_id": 2
  }'
```

---

### 6. Eliminar tarea

```bash
curl -X DELETE http://localhost:8080/tasks/4 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 7. Ejemplos de errores

**Credenciales inválidas (`401`):**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "wrong"}'
```
```json
{
  "message": "Invalid username or password",
  "status": 401,
  "timestamp": "..."
}
```

**Sin token (`401`):**
```bash
curl -X GET http://localhost:8080/tasks
```

**Tarea no encontrada (`404`):**
```bash
curl -X GET http://localhost:8080/tasks/999 \
  -H "Authorization: Bearer $TOKEN"
```
```json
{
  "message": "Task not found with id: 999",
  "status": 404,
  "timestamp": "..."
}
```

**Datos inválidos (`400`):**
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"description": "Sin título"}'
```
```json
{
  "message": "title: no debe ser nulo",
  "status": 400,
  "timestamp": "..."
}
```

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/nuevospa/taskmanager/
│   │   ├── TaskmanagerApplication.java
│   │   ├── config/
│   │   │   └── OpenApiConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   └── TaskController.java
│   │   ├── entity/
│   │   │   ├── Task.java
│   │   │   ├── TaskStatus.java
│   │   │   └── User.java
│   │   ├── exception/
│   │   │   ├── BadRequestException.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── UnauthorizedException.java
│   │   ├── mapper/
│   │   │   └── TaskMapper.java
│   │   ├── repository/
│   │   │   ├── TaskRepository.java
│   │   │   ├── TaskStatusRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtUtil.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── TaskService.java
│   │       └── impl/
│   │           ├── AuthServiceImpl.java
│   │           └── TaskServiceImpl.java
│   └── resources/
│       ├── application.yml
│       ├── data.sql
│       ├── openapi.yml
│       └── schema.sql
└── test/
    └── java/com/nuevospa/taskmanager/
        └── service/
            ├── AuthServiceImplTest.java
            └── TaskServiceImplTest.java
```

---

## Ejecución de tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar con reporte detallado
mvn test -Dsurefire.useFile=false
```

Los tests cubren los dos services de negocio con 10 casos de prueba:

| Clase | Tests | Cobertura |
|---|---|---|
| `AuthServiceImplTest` | 2 | Login exitoso, credenciales inválidas |
| `TaskServiceImplTest` | 8 | CRUD completo + casos de error |

---

## Información del autor

| Campo | Valor |
|---|---|
| **Nombre** | Martín Castronovo |
| **Email** | tinchocastronovo@gmail.com |
| **Cargo postulado** | Desarrollador Backend Senior |
| **Repositorio** | [castronovomartin/desafio-spring-boot](https://github.com/castronovomartin/desafio-spring-boot) |
| **Rama** | `feature/martin-castronovo` |