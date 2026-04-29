# StreamingApp — Plataforma de Streaming

Plataforma de streaming full-stack inspirada en Netflix, construida con **Spring Boot 4** y **Java 17**. Incorpora autenticación avanzada mediante **biometría de voz** y **autenticación de dos factores (2FA)**, además de un **microservicio Python/FastAPI** para el reconocimiento de hablantes mediante inteligencia artificial.

---

## Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Funcionalidades](#funcionalidades)
- [Stack Tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Primeros Pasos](#primeros-pasos)
  - [Requisitos Previos](#requisitos-previos)
  - [Variables de Entorno](#variables-de-entorno)
  - [Ejecución con Docker](#ejecución-con-docker)
  - [Ejecución Local](#ejecución-local)
- [Microservicio de Reconocimiento de Voz](#microservicio-de-reconocimiento-de-voz)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Seguridad](#seguridad)
- [Contribuciones](#contribuciones)

---

## Descripción General

StreamingApp es una plataforma de streaming completa que permite a los usuarios explorar películas y series, gestionar suscripciones y autenticarse mediante múltiples métodos. Su característica distintiva es el sistema de **autenticación biométrica por voz** — los usuarios pueden registrar su voz y utilizarla como segundo factor o identificador principal, impulsado por un modelo de aprendizaje automático basado en PyTorch y SpeechBrain.

---

## Funcionalidades

- **Catálogo de Contenido** — Exploración de películas y series organizadas por categoría.
- **Control de Acceso por Roles** — Roles `ADMIN` y `USUARIO` con rutas protegidas.
- **Registro e Inicio de Sesión** — Contraseñas cifradas con BCrypt.
- **OAuth2 con Google** — Inicio de sesión con cuenta de Google.
- **Autenticación de Dos Factores (2FA)** — Códigos OTP enviados por SMS mediante Twilio.
- **Autenticación Biométrica por Voz** — Registro y verificación de usuarios mediante voz con modelo de aprendizaje profundo.
- **Planes de Suscripción** — Niveles BÁSICA, ESTÁNDAR y PREMIUM.
- **Notificaciones por Correo Electrónico** — Correos transaccionales mediante Gmail SMTP.
- **Almacenamiento de Media en la Nube** — Miniaturas y contenido servidos desde Firebase / Google Cloud Storage.
- **Generación de Códigos QR** — Para flujos de configuración de 2FA.
- **Gestión de Sesión** — Expiración a los 30 minutos con cookies seguras y HTTP-only.

---

## Stack Tecnológico

### Backend

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.0 |
| Spring Security + OAuth2 | Última |
| Spring Data JPA (Hibernate) | Última |
| Spring WebFlux | Última |
| MariaDB / MySQL | 8.0 |

### Frontend

| Tecnología | Versión |
|---|---|
| Thymeleaf | Última |
| Bootstrap | 5.3.8 |
| jQuery | 3.7.1 |
| Font Awesome | 7.0.1 |

### Servicios Externos

| Servicio | Uso |
|---|---|
| Google Cloud Storage | Almacenamiento de media e imágenes |
| Firebase Admin SDK | Servicios en la nube de backend |
| Twilio | Envío de SMS para 2FA |
| Microsoft Cognitive Services Speech | Procesamiento de texto a voz |
| Gmail SMTP | Notificaciones por correo electrónico |

### Microservicio de Reconocimiento de Voz (Python)

| Tecnología | Uso |
|---|---|
| FastAPI + Uvicorn | Servidor REST API |
| PyTorch + SpeechBrain | Modelo ECAPA-VoxCeleb para identificación de hablantes |
| librosa / soundfile | Procesamiento de audio |
| SQLAlchemy + PyMySQL | Persistencia de embeddings de voz |

---

## Arquitectura

```
+--------------------------------------------------+
|                  Navegador Web                   |
|         (Interfaz Thymeleaf + Bootstrap)         |
+---------------------+----------------------------+
                      |  HTTP / HTTPS
+---------------------v----------------------------+
|      Aplicación Spring Boot (Puerto 80)          |
|                                                  |
|  Controladores  ->  Servicios  ->  Repositorios  |
|                                                  |
|  +-----------+   +-------------+  +-----------+  |
|  | Auth /    |   | Contenido   |  | Usuarios /|  |
|  | OAuth2 /  |   | (Películas, |  | Roles /   |  |
|  | 2FA / Voz |   |  Series)    |  | Suscripc. |  |
|  +-----------+   +-------------+  +-----------+  |
+------+---------------+------------------+--------+
       |               |                  |
+------v------+  +------v-----+  +--------v-----------+
| MariaDB /   |  | Firebase   |  | Microservicio Voz  |
| MySQL DB    |  | Cloud      |  | (Python)           |
| Puerto 3307 |  | Storage    |  | FastAPI Puerto 8000|
+-------------+  +------------+  +--------------------+
```

---

## Primeros Pasos

### Requisitos Previos

- **Docker** y **Docker Compose** (recomendado), o bien:
- **Java 17+**, **Maven 3.9+**, **MariaDB/MySQL 8**, **Python 3.9+**
- Un proyecto de Google Cloud con credenciales OAuth2 configuradas.
- Un proyecto de Firebase con un archivo JSON de cuenta de servicio.
- Una cuenta de Twilio para 2FA por SMS.
- Una cuenta de Gmail configurada para acceso SMTP a nivel de aplicación.

---

### Variables de Entorno

Copie `ini.env` a `.env` y complete los valores requeridos:

```env
# Base de datos
MYSQL_DATABASE=pstreaming
MYSQL_USER=usuariop
MYSQL_PASSWORD=contraseña_db
MYSQL_ROOT_PASSWORD=contraseña_root

# Google OAuth2
GOOGLE_CLIENT_ID=tu_google_client_id
GOOGLE_CLIENT_SECRET=tu_google_client_secret

# Twilio (SMS 2FA)
TWILIO_ACCOUNT_SID=tu_twilio_sid
TWILIO_AUTH_TOKEN=tu_twilio_auth_token
TWILIO_FROM_PHONE=+1234567890

# Gmail SMTP
GMAIL_USERNAME=tucorreo@gmail.com
GMAIL_PASSWORD=tu_contraseña_de_aplicacion
```

> **Advertencia:** No incluya archivos `.env` o `ini.env` con credenciales reales en el control de versiones.

---

### Ejecución con Docker

```bash
# 1. Clone el repositorio
git clone https://github.com/tu-usuario/StreamingApp.git
cd StreamingApp

# 2. Configure el archivo de entorno
cp ini.env .env
# Edite .env con sus credenciales

# 3. Construya e inicie todos los servicios
docker compose up --build
```

La aplicación estará disponible en `http://localhost:8080`.

Docker Compose levanta los siguientes servicios:
- `mysql-db` — MySQL 8.0 en el puerto `3307`
- `streamingapp` — Aplicación Spring Boot en el puerto `8080`

> El microservicio de reconocimiento de voz debe iniciarse de forma independiente. Consulte la sección correspondiente a continuación.

---

### Ejecución Local

**1. Inicie la base de datos**

Asegúrese de que MariaDB o MySQL esté en ejecución en el puerto `3307` y de que exista una base de datos llamada `pstreaming`.

**2. Configure las propiedades de la aplicación**

Actualice `src/main/resources/application.properties` con sus credenciales locales.

**3. Compile y ejecute la aplicación Spring Boot**

```bash
mvn clean package -DskipTests
java -jar target/PlataformaStreaming-1.jar
```

**4. Inicie el microservicio de reconocimiento de voz**

```bash
cd VoiceRecognition
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8000
```

La aplicación estará disponible en `http://localhost:80`.

---

## Microservicio de Reconocimiento de Voz

Ubicado en el directorio `VoiceRecognition/`, es un servicio **Python FastAPI** independiente que gestiona la autenticación biométrica por voz.

**Modelo:** `speechbrain/spkrec-ecapa-voxceleb` — un modelo de verificación de hablantes de última generación entrenado con el conjunto de datos VoxCeleb.

**Flujo de autenticación:**

1. **Registro (Enrollment)** — El usuario graba una muestra de voz. El modelo genera un embedding del hablante, el cual se almacena en la base de datos.
2. **Verificación** — Al iniciar sesión, una nueva muestra de audio se compara contra el embedding almacenado mediante similitud coseno. El umbral de aceptación es `0.85`.

**Endpoints de la API:**

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/enroll/{usuario_id}` | Registra el embedding de voz de un usuario |
| `POST` | `/verify/{usuario_id}` | Verifica una muestra de voz contra el embedding almacenado |

**Requisitos de audio:** El audio de entrada se remuestrea automáticamente a 16 kHz mono WAV antes del procesamiento.

---

## Estructura del Proyecto

```
StreamingApp/
├── src/main/java/com/pstreaming/
│   ├── PlataformaStreamingApplication.java   # Punto de entrada de la aplicación
│   ├── ProjectConfig.java                    # Configuración de Spring Security
│   ├── controller/                           # Capa HTTP (8 controladores)
│   ├── domain/                               # Modelos de entidad JPA (10 clases)
│   ├── service/                              # Lógica de negocio (12 servicios)
│   └── repository/                           # Capa de acceso a datos (8 repositorios)
├── src/main/resources/
│   ├── application.properties                # Configuración de la aplicación
│   ├── templates/                            # Plantillas HTML Thymeleaf
│   └── static/                               # CSS, JavaScript e imágenes
├── VoiceRecognition/
│   ├── app.py                                # Servicio FastAPI de autenticación por voz
│   └── requirements.txt                      # Dependencias Python
├── Dockerfile                                # Build multi-etapa de Java
├── docker-compose.yml                        # Orquestación de servicios
└── ini.env                                   # Plantilla de variables de entorno
```

---

## Seguridad

- Las contraseñas se cifran mediante **BCrypt**.
- Las sesiones expiran tras **30 minutos** de inactividad.
- Las cookies se configuran como **HTTP-only** con el atributo `SameSite=Lax`.
- La autenticación OAuth2 es gestionada por Spring Security.
- Los códigos 2FA se transmiten mediante **SMS (Twilio)**.
- La verificación de voz emplea **similitud coseno** con un umbral de aceptación estricto.

> **Nota para producción:** Asegúrese de que las reglas de autorización de Spring Security en `ProjectConfig.java` estén completamente habilitadas. Revise las prácticas de gestión de secretos antes de realizar cualquier despliegue público.

---

## Contribuciones

1. Realice un fork del repositorio.
2. Cree una rama para su funcionalidad: `git checkout -b feature/nombre-funcionalidad`
3. Confirme sus cambios: `git commit -m "Descripción del cambio"`
4. Suba la rama: `git push origin feature/nombre-funcionalidad`
5. Abra un Pull Request.

---

## Licencia

Este proyecto tiene carácter educativo y de portafolio. Se permite su fork y adaptación.
