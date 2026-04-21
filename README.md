# StreamingApp (PlataformaStreaming)

Aplicación web desarrollada con **Spring Boot + Thymeleaf** para gestionar una plataforma de streaming con catálogo de películas y series, autenticación de usuarios y mecanismos de seguridad como **2FA por SMS** y validación de voz.

## Tabla de contenido
- [Características principales](#características-principales)
- [Stack tecnológico](#stack-tecnológico)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Configuración de entorno](#configuración-de-entorno)
- [Ejecución en local](#ejecución-en-local)
- [Ejecución con Docker](#ejecución-con-docker)
- [Rutas principales](#rutas-principales)
- [Notas de seguridad importantes](#notas-de-seguridad-importantes)
- [Comandos útiles](#comandos-útiles)

---

## Características principales

- Registro e inicio de sesión de usuarios.
- Inicio de sesión tradicional y flujo con OAuth2 (Google).
- Verificación en dos pasos (2FA) mediante código SMS.
- Enrolamiento y verificación de voz para autenticación adicional.
- Gestión de catálogo de **películas** y **series** con categorías.
- Carga de imágenes de contenido a Firebase Storage.
- Interfaz web con Thymeleaf, Bootstrap y mensajes internacionalizados.

---

## Stack tecnológico

- **Java 17**
- **Spring Boot 4**
- **Spring Web / Thymeleaf**
- **Spring Security + OAuth2 Client**
- **Spring Data JPA (Hibernate)**
- **MariaDB / MySQL**
- **Twilio SDK** (SMS)
- **Google Cloud Storage / Firebase Admin**
- **Azure Speech SDK** (integración de voz)
- **Maven**

---

## Estructura del proyecto

```text
src/main/java/com/pstreaming
├── controller   # Controladores MVC
├── domain       # Entidades/modelos
├── repository   # Repositorios JPA
└── service      # Lógica de negocio e integraciones externas

src/main/resources
├── templates    # Vistas Thymeleaf
├── static.img   # Recursos estáticos de imágenes
├── firebase     # Credenciales/config de Firebase
├── application.properties
└── messages.properties
```

---

## Requisitos previos

Asegúrate de tener instalado:

- **JDK 17**
- **Maven 3.9+**
- **MySQL/MariaDB**
- (Opcional) **Docker + Docker Compose**

---

## Configuración de entorno

La aplicación usa propiedades directas y variables de entorno para servicios externos.

### Variables recomendadas

Configura como mínimo las siguientes variables antes de arrancar:

```bash
# OAuth2 Google
export GOOGLE_CLIENT_ID="tu_client_id"
export GOOGLE_CLIENT_SECRET="tu_client_secret"

# Twilio
export TWILIO_ACCOUNT_SID="tu_account_sid"
export TWILIO_AUTH_TOKEN="tu_auth_token"
export TWILIO_FROM_PHONE="+123456789"
```

### Base de datos

Por defecto se espera una base `pstreaming` en `localhost:3307` según `application.properties`.

Ejemplo de URL:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3307/pstreaming?useGssApi=false
```

> Si usas otros puertos/credenciales, ajústalos en `src/main/resources/application.properties` o por variables de entorno de Spring (`SPRING_DATASOURCE_*`).

---

## Ejecución en local

1. Clona el repositorio.
2. Configura la base de datos y variables de entorno.
3. Ejecuta:

```bash
mvn spring-boot:run
```

La aplicación levantará en el puerto configurado por `server.port` (actualmente `80`).

---

## Ejecución con Docker

El repositorio incluye `Dockerfile` y `docker-compose.yml`.

1. Crea/ajusta un archivo `.env` con tus credenciales (puedes basarte en `ini.env`, pero **no** uses secretos reales en repositorio).
2. Levanta servicios:

```bash
docker compose up --build
```

Esto inicia:
- `mysql-db`
- `streamingapp`

---

## Rutas principales

- `/` → redirige a `/index`
- `/index` → página principal
- `/usuario/registro` → registro de usuario
- `/usuario/login` → login
- `/usuario/2fa` → verificación por código
- `/pelicula/pelicula` → catálogo/gestión de películas
- `/serie/serie` → catálogo/gestión de series
- `/usuario/perfil` → perfil del usuario

---

## Notas de seguridad importantes

- Antes de publicar o desplegar, mueve credenciales sensibles a variables de entorno o un gestor de secretos.
- No subas llaves/API keys reales al repositorio.
- Revisa la configuración de Spring Security para asegurar las rutas privadas en producción.

---

## Comandos útiles

```bash
# Compilar
mvn clean compile

# Ejecutar pruebas
mvn test

# Empaquetar JAR
mvn clean package
```

---

Si quieres, puedo prepararte una versión de este README enfocada a **despliegue en producción** (con checklist de seguridad, variables por entorno y pipeline CI/CD).
