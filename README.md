# StreamingApp

Aplicación web de streaming construida con **Spring Boot**, **Thymeleaf** y **MariaDB**. El proyecto ofrece una experiencia básica de catálogo para películas y series, además de un flujo de autenticación con soporte para inicio de sesión tradicional, Google OAuth y mecanismos de verificación adicional por **SMS** y **voz**.

## Características principales

- Registro de usuarios con contraseña, palabra clave y muestra de voz.
- Inicio de sesión por contraseña o por palabra clave dictada por voz.
- Flujo de verificación en dos pasos (2FA) mediante SMS o validación de voz.
- Catálogo de películas y series con formularios para registrar nuevo contenido.
- Carga de imágenes a Firebase Storage.
- Integración con Google OAuth2 para autenticación social.
- UI server-side renderizada con Thymeleaf + Bootstrap.

## Stack tecnológico

- **Java 17**
- **Spring Boot 4**
- **Spring MVC + Thymeleaf**
- **Spring Security**
- **Spring Data JPA**
- **MariaDB**
- **Bootstrap 5 / Font Awesome / JavaScript vanilla**
- **Firebase Storage** para imágenes
- **Twilio** para envío de SMS
- **WebClient** para consumir un microservicio externo de autenticación por voz

## Estructura funcional del proyecto

### Autenticación y usuarios

El flujo de usuarios está concentrado en los controladores de `/usuario`:

- `/usuario/registro`: crea un usuario, cifra contraseña y palabra clave, y opcionalmente registra la voz.
- `/usuario/login/password`: login por correo y contraseña.
- `/usuario/login/voz`: login por correo y palabra clave dictada.
- `/usuario/2fa`: verificación por SMS.
- `/usuario/2fa/voz`: verificación por voz.
- `/usuario/perfil`: muestra información básica del usuario autenticado.

### Catálogo

El sistema expone dos módulos principales:

- `/pelicula/pelicula`: listado, creación y eliminación de películas.
- `/serie/serie`: listado, creación y eliminación de series.

Ambos módulos dependen de categorías almacenadas en base de datos y soportan carga de imagen hacia Firebase Storage.

### Servicios externos

Para funcionar completamente, el proyecto espera integraciones con:

- **MariaDB** en `localhost:3307`
- **Firebase Storage** con una service account JSON en `src/main/resources/firebase/`
- **Twilio** mediante variables de entorno
- **Google OAuth2** mediante variables de entorno
- **Microservicio de voz** accesible en `http://127.0.0.1:8000`

## Estructura del código

```text
src/main/java/com/pstreaming
├── controller     # Endpoints MVC
├── domain         # Entidades JPA y clases de seguridad/config
├── repository     # Repositorios Spring Data
├── service        # Lógica de negocio e integraciones externas
└── *.java         # Configuración principal de la app

src/main/resources
├── templates      # Vistas Thymeleaf
├── static         # CSS y JavaScript
├── messages.properties
└── application.properties
```

## Requisitos previos

Antes de ejecutar la aplicación, asegúrate de tener instalado:

- JDK 17
- Maven 3.9+
- MariaDB disponible localmente
- Un proyecto/configuración de Firebase Storage
- Credenciales de Twilio para SMS
- Un cliente OAuth de Google
- El microservicio de autenticación de voz ejecutándose localmente

## Configuración

### 1. Base de datos

La configuración actual apunta a una base MariaDB local:

- **Host:** `localhost`
- **Puerto:** `3307`
- **Base de datos:** `pstreaming`

Crea la base de datos y ajusta credenciales en `src/main/resources/application.properties` según tu entorno.

> Recomendación: mover credenciales sensibles a variables de entorno o a un archivo `.env`/perfil local antes de desplegar el proyecto.

### 2. Variables de entorno

El proyecto espera estas variables para integraciones externas:

```bash
export GOOGLE_CLIENT_ID=...
export GOOGLE_CLIENT_SECRET=...
export TWILIO_ACCOUNT_SID=...
export TWILIO_AUTH_TOKEN=...
export TWILIO_FROM_PHONE=...
```

### 3. Firebase

La carga de imágenes usa un archivo de service account en recursos. Verifica que el JSON exista y corresponda a tu proyecto de Firebase/Google Cloud.

### 4. Microservicio de voz

Levanta el servicio externo que responde a:

- `POST /enroll`
- `POST /verify`

La URL base configurada por defecto es:

```properties
voice.ms.url=http://127.0.0.1:8000
```

## Ejecución local

### Con Maven

```bash
mvn spring-boot:run
```

### Empaquetar el proyecto

```bash
mvn clean package
```

Luego puedes ejecutar el `.jar` generado:

```bash
java -jar target/*.jar
```

## Ejecución con Docker

El repositorio incluye un `Dockerfile` multi-stage.

### Construir imagen

```bash
docker build -t streamingapp .
```

### Ejecutar contenedor

```bash
docker run --rm -p 8080:8080 \
  -e GOOGLE_CLIENT_ID=... \
  -e GOOGLE_CLIENT_SECRET=... \
  -e TWILIO_ACCOUNT_SID=... \
  -e TWILIO_AUTH_TOKEN=... \
  -e TWILIO_FROM_PHONE=... \
  streamingapp
```

> Nota: el contenedor expone `8080`, mientras que la configuración local en `application.properties` usa `server.port=80`. Ajusta ese valor si quieres un comportamiento consistente entre ejecución local y Docker.

## Rutas principales

| Ruta | Descripción |
| --- | --- |
| `/` | Redirección al inicio |
| `/index` | Landing principal |
| `/usuario/registro` | Registro de usuarios |
| `/usuario/login` | Login |
| `/usuario/2fa` | Verificación 2FA |
| `/usuario/perfil` | Perfil del usuario |
| `/pelicula/pelicula` | Catálogo de películas |
| `/serie/serie` | Catálogo de series |

## Modelo de dominio principal

- **Usuario**: datos personales, correo, contraseña, teléfono, palabra clave y roles.
- **Rol**: roles asociados al usuario.
- **Pelicula**: título, fecha, imagen, descripción y categoría.
- **Serie**: título, fecha, temporadas, episodios, imagen, descripción y categoría.
- **Categoria**: clasificación para películas y series.
- **VozUsuario**: huella o embedding de voz para enrolamiento/verificación.
- **Suscripciones**: entidad prevista para administrar planes o suscripciones.

## Consideraciones importantes

- La configuración de seguridad actual permite todas las solicitudes (`permitAll()`), por lo que el endurecimiento de seguridad todavía parece estar en progreso.
- Hay endpoints y clases que sugieren una evolución activa del proyecto, por ejemplo `SuscripcionesController` aún está vacío.
- Parte del flujo de UI depende de atributos de sesión como `usuarioLogueado`.
- Algunas integraciones requieren infraestructura externa para funcionar por completo; sin ellas, la app puede iniciar pero ciertas funciones fallarán.

## Posibles mejoras

- Externalizar secretos y credenciales completamente.
- Agregar migraciones de base de datos con Flyway o Liquibase.
- Activar y completar la configuración real de Spring Security.
- Incorporar pruebas automatizadas.
- Añadir `docker-compose` para levantar MariaDB y servicios dependientes.
- Documentar el contrato del microservicio de voz.

## Estado del proyecto

Este repositorio parece orientado a fines académicos o demostrativos, pero ya integra varias piezas interesantes de una plataforma moderna: catálogo multimedia, autenticación avanzada y consumo de servicios externos.

Si vas a continuar su desarrollo, el mejor siguiente paso es estabilizar la configuración de seguridad y estandarizar el setup local para que cualquier colaborador pueda levantar el proyecto con mínima fricción.
