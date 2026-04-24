# 🎬 StreamingApp — Plataforma de Streaming

Una plataforma de streaming full-stack inspirada en Netflix, construida con **Spring Boot 4** y **Java 17**, que incluye autenticación avanzada mediante **biometría de voz** y **autenticación de dos factores (2FA)**. Incluye un **microservicio Python/FastAPI** para reconocimiento de hablantes con inteligencia artificial.

---

## 📋 Tabla de Contenidos

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

StreamingApp es una plataforma de streaming completa que permite a los usuarios explorar películas y series, gestionar suscripciones y autenticarse mediante múltiples métodos. Su característica principal es el sistema de **autenticación biométrica por voz** — los usuarios pueden registrar su voz y usarla como segundo factor o identificador principal, impulsado por un modelo de aprendizaje automático basado en PyTorch y SpeechBrain.

---

## ✨ Funcionalidades

- **Catálogo de Contenido** — Explora películas y series organizadas por categoría
- **Control de Acceso por Roles** — Roles `ADMIN` y `USUARIO` con rutas protegidas
- **Registro e Inicio de Sesión** — Contraseñas cifradas con BCrypt
- **OAuth2 con Google** — Inicia sesión con tu cuenta de Google
- **Autenticación de Dos Factores (2FA)** — Códigos OTP enviados por SMS vía Twilio
- **Autenticación Biométrica por Voz** — Registro y verificación de usuarios mediante voz con modelo de deep learning
- **Planes de Suscripción** — Niveles BÁSICA, ESTÁNDAR y PREMIUM
- **Notificaciones por Correo** — Correos transaccionales vía Gmail SMTP
- **Almacenamiento de Media en la Nube** — Miniaturas y contenido servidos desde Firebase / Google Cloud Storage
- **Generación de Códigos QR** — Para flujos de configuración de 2FA
- **Gestión de Sesión** — Expiración a los 30 minutos con cookies seguras y HTTP-only

---

## 🛠 Stack Tecnológico

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
| Twilio | SMS para 2FA |
| Microsoft Cognitive Services Speech | Texto a voz |
| Gmail SMTP | Notificaciones por correo |

### Microservicio de Reconocimiento de Voz (Python)
| Tecnología | Uso |
|---|---|
| FastAPI + Uvicorn | Servidor REST API |
| PyTorch + SpeechBrain | Modelo ECAPA-VoxCeleb para identificación de hablantes |
| librosa / soundfile | Procesamiento de audio |
| SQLAlchemy + PyMySQL | Almacenamiento de embeddings de voz |

---

## 🏗 Arquitectura

```
┌──────────────────────────────────────────────────┐
│               Navegador Web                      │
│         (Interfaz Thymeleaf + Bootstrap)         │
└─────────────────────┬────────────────────────────┘
                      │ HTTP / HTTPS
┌─────────────────────▼────────────────────────────┐
│      Aplicación Spring Boot (Puerto 80)          │
│                                                  │
│  Controladores → Servicios → Repositorios        │
│                                                  │
│  ┌─────────────┐  ┌───────────┐  ┌───────────┐  │
│  │  Auth /     │  │ Contenido │  │ Usuarios /│  │
│  │  OAuth2 /   │  │(Películas,│  │  Roles /  │  │
│  │  2FA / Voz  │  │  Series)  │  │   Subs.   │  │
│  └─────────────┘  └───────────┘  └───────────┘  │
└──────┬──────────────┬───────────────┬────────────┘
       │              │               │
┌──────▼──────┐ ┌─────▼──────┐ ┌─────▼──────────────┐
│  MariaDB /  │ │  Firebase  │ │  Microservicio de   │
│  MySQL DB   │ │  Cloud     │ │  Voz (Python)       │
│ Puerto 3307 │ │  Storage   │ │  FastAPI Puerto 8000│
└─────────────┘ └────────────┘ └────────────────────┘
```

---

## 🚀 Primeros Pasos

### Requisitos Previos

- **Docker** y **Docker Compose** (recomendado)
- O bien: **Java 17+**, **Maven 3.9+**, **MariaDB/MySQL 8**, **Python 3.9+**
- Un proyecto de Google Cloud con credenciales OAuth2
- Un proyecto de Firebase con JSON de cuenta de servicio
- Una cuenta de Twilio (para SMS con 2FA)
- Una cuenta de Gmail (para notificaciones por correo)

---

### Variables de Entorno

Copia `ini.env` a `.env` y completa tus credenciales:

```env
# Base de datos
MYSQL_DATABASE=pstreaming
MYSQL_USER=usuariop
MYSQL_PASSWORD=tu_contraseña_db
MYSQL_ROOT_PASSWORD=tu_contraseña_root

# Google OAuth2
GOOGLE_CLIENT_ID=tu_google_client_id
GOOGLE_CLIENT_SECRET=tu_google_client_secret

# Twilio (SMS 2FA)
TWILIO_ACCOUNT_SID=tu_twilio_sid
TWILIO_AUTH_TOKEN=tu_twilio_auth_token
TWILIO_FROM_PHONE=+1234567890

# Gmail SMTP
GMAIL_USERNAME=tucorreo@gmail.com
GMAIL_PASSWORD=tu_contraseña_de_app
```

> ⚠️ **Nunca subas archivos `.env` o `ini.env` con credenciales reales a control de versiones.**

---

### Ejecución con Docker

```bash
# 1. Clona el repositorio
git clone https://github.com/tu-usuario/StreamingApp.git
cd StreamingApp

# 2. Configura el archivo de entorno
cp ini.env .env
# Edita .env con tus credenciales

# 3. Construye e inicia todos los servicios
docker compose up --build

# La app estará disponible en http://localhost:8080
```

Docker Compose levanta:
- `mysql-db` — MySQL 8.0 en el puerto `3307`
- `streamingapp` — Aplicación Spring Boot en el puerto `8080`

> El microservicio de reconocimiento de voz debe iniciarse por separado (ver más abajo).

---

### Ejecución Local

**1. Inicia la base de datos**
```bash
# Asegúrate de que MariaDB/MySQL esté corriendo en el puerto 3307
# Crea una base de datos llamada `pstreaming`
```

**2. Configura `application.properties`**
Actualiza `src/main/resources/application.properties` con tus credenciales locales.

**3. Compila y ejecuta la app Spring Boot**
```bash
mvn clean package -DskipTests
java -jar target/PlataformaStreaming-1.jar
```

**4. Inicia el microservicio de reconocimiento de voz**
```bash
cd VoiceRecognition
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8000
```

La aplicación estará disponible en **http://localhost:80**.

---

## 🎤 Microservicio de Reconocimiento de Voz

Ubicado en la carpeta `VoiceRecognition/`, es un servicio **Python FastAPI** independiente que gestiona la autenticación biométrica por voz.

**Modelo:** `speechbrain/spkrec-ecapa-voxceleb` — un modelo de verificación de hablantes de última generación entrenado con VoxCeleb.

**Cómo funciona:**
1. **Registro (Enrollment)** — El usuario graba una muestra de voz; el modelo genera un embedding del hablante y lo almacena.
2. **Verificación** — Al iniciar sesión, se compara una nueva muestra contra el embedding almacenado usando similitud coseno (umbral: `0.85`).

**Endpoints:**

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/enroll/{usuario_id}` | Registra la voz de un usuario |
| `POST` | `/verify/{usuario_id}` | Verifica una voz contra el embedding almacenado |

**Requisitos de audio:** El audio se remuestrea automáticamente a 16 kHz mono WAV antes del procesamiento.

---

## 📁 Estructura del Proyecto

```
StreamingApp/
├── src/main/java/com/pstreaming/
│   ├── PlataformaStreamingApplication.java   # Punto de entrada
│   ├── ProjectConfig.java                    # Configuración Spring Security
│   ├── controller/                           # Controladores HTTP (8 clases)
│   ├── domain/                               # Modelos de entidad JPA (10 clases)
│   ├── service/                              # Lógica de negocio (12 servicios)
│   └── repository/                           # Repositorios Spring Data JPA (8 repos)
├── src/main/resources/
│   ├── application.properties                # Configuración de la app
│   ├── templates/                            # Plantillas HTML Thymeleaf
│   └── static/                               # CSS, JS, imágenes
├── VoiceRecognition/
│   ├── app.py                                # Servicio FastAPI de autenticación por voz
│   └── requirements.txt                      # Dependencias Python
├── Dockerfile                                # Build multi-etapa de Java
├── docker-compose.yml                        # Orquestación de servicios
└── ini.env                                   # Plantilla de variables de entorno
```

---

## 🔐 Seguridad

- Contraseñas cifradas con **BCrypt**
- Las sesiones expiran tras **30 minutos** de inactividad
- Las cookies son **HTTP-only** y `SameSite=Lax`
- OAuth2 gestionado por Spring Security
- Códigos 2FA enviados por **SMS (Twilio)**
- La verificación de voz usa **similitud coseno** con un umbral estricto

> **Nota para producción:** Asegúrate de que las reglas de autorización de Spring Security en `ProjectConfig.java` estén completamente habilitadas. Revisa las prácticas de gestión de secretos antes de desplegar públicamente.

---

## 🤝 Contribuciones

1. Haz un fork del repositorio
2. Crea una rama para tu funcionalidad: `git checkout -b feature/mi-funcionalidad`
3. Confirma tus cambios: `git commit -m 'Agrega mi funcionalidad'`
4. Sube la rama: `git push origin feature/mi-funcionalidad`
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto es de carácter educativo y de portafolio. Siéntete libre de hacer un fork y adaptarlo.

---

*Construido con ❤️ usando Spring Boot, PyTorch y SpeechBrain.*
