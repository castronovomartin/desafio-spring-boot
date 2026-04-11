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
- [Documentación interactiva](#documentación-interactiva-swagger-ui)
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

## Documentación interactiva (Swagger UI)

Una vez que la aplicación esté corriendo, accedé a:

```
http://localhost:8080/swagger-ui.html
```

### Cómo autenticarse en Swagger UI

1. Ejecutá el endpoint `POST /auth/login` con cualquiera de las credenciales de prueba
2. Copiá el valor del campo `token` de la respuesta
3. Hacé click en el botón **Authorize** 🔒 (arriba a la derecha)
4. Pegá el token en el campo **Value** y hacé click en **Authorize**
5. Ahora todos los endpoints protegidos están disponibles para probar

### Consola H2

La consola de H2 para inspeccionar la base de datos está disponible en:

```
http://localhost:8080/h2-console
```

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:taskmanagerdb` |
| Username | `sa` |
| Password | *(vacío)* |

---

## Ejemplos de uso con cURL

### 1. Autenticación — obtener JWT token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

Respuesta exitosa (`200 OK`):

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```

> Guardá el token para usar en los siguientes requests:
> ```bash
> TOKEN="eyJhbGciOiJIUzI1NiJ9..."
> ```

---

### 2. Listar todas las tareas

```bash
curl -X GET http://localhost:8080/tasks \
  -H "Authorization: Bearer $TOKEN"
```

Respuesta exitosa (`200 OK`):

```json
[
  {
    "id": 3,
    "title": "Implement authentication",
    "description": "JWT login endpoint and security filter chain",
    "status": "COMPLETED",
    "created_at": "2026-04-09T20:00:00Z",
    "updated_at": "2026-04-09T20:00:00Z"
  },
  {
    "id": 2,
    "title": "Design database schema",
    "description": "Define all entities and relationships for the system",
    "status": "IN_PROGRESS",
    "created_at": "2026-04-09T19:00:00Z",
    "updated_at": "2026-04-09T19:00:00Z"
  }
]
```

---

### 3. Obtener tarea por ID

```bash
curl -X GET http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

Respuesta exitosa (`200 OK`):

```json
{
  "id": 1,
  "title": "Setup project infrastructure",
  "description": "Initialize repository, configure CI/CD pipeline",
  "status": "PENDING",
  "created_at": "2026-04-09T18:00:00Z",
  "updated_at": "2026-04-09T18:00:00Z"
}
```

---

### 4. Crear una nueva tarea

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementar nuevo feature",
    "description": "Descripción detallada del feature",
    "status_id": 1
  }'
```

Respuesta exitosa (`201 Created`):

```json
{
  "id": 4,
  "title": "Implementar nuevo feature",
  "description": "Descripción detallada del feature",
  "status": "PENDING",
  "created_at": "2026-04-09T21:00:00Z",
  "updated_at": "2026-04-09T21:00:00Z"
}
```

---

### 5. Actualizar una tarea

```bash
curl -X PUT http://localhost:8080/tasks/4 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementar nuevo feature",
    "description": "Feature completado y testeado",
    "status_id": 3
  }'
```

Respuesta exitosa (`200 OK`):

```json
{
  "id": 4,
  "title": "Implementar nuevo feature",
  "description": "Feature completado y testeado",
  "status": "COMPLETED",
  "created_at": "2026-04-09T21:00:00Z",
  "updated_at": "2026-04-09T21:30:00Z"
}
```

---

### 6. Eliminar una tarea

```bash
curl -X DELETE http://localhost:8080/tasks/4 \
  -H "Authorization: Bearer $TOKEN"
```

Respuesta exitosa (`204 No Content`) — sin body.

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
  "timestamp": "2026-04-09T21:00:00Z"
}
```

**Sin token (`401`):**

```bash
curl -X GET http://localhost:8080/tasks
```

```json
{
  "status": 401,
  "error": "Unauthorized"
}
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
  "timestamp": "2026-04-09T21:00:00Z"
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
  "message": "title: must not be null",
  "status": 400,
  "timestamp": "2026-04-09T21:00:00Z"
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
