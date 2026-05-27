# PlataformaStreaming — Backend

A REST API built with **Spring Boot 4** for a streaming platform with content management, JWT-based authentication, and a full **Two-Factor Authentication (2FA)** system using SMS and voice biometrics.

This backend is one of three components in the StreamingApp project:

| Component | Technology | Description |
|---|---|---|
| **streaming-app-back-end** | Spring Boot 4 | REST API, auth, business logic |
| **VoiceRecognition** | Python FastAPI | Voice biometric microservice |
| **streaming-app-front-end** | Next.js | User interface |

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Authentication Flow](#authentication-flow)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Environment Variables](#environment-variables)
- [Running Locally](#running-locally)
- [Running with Docker](#running-with-docker)
- [VoiceRecognition Microservice](#voicerecognition-microservice)

---

## Architecture Overview

```
┌─────────────────────┐        ┌──────────────────────────┐
│   Next.js Frontend  │──────▶ │  Spring Boot REST API     │
│   (Port 5173)       │        │  (Port 80)                │
└─────────────────────┘        │                           │
                                │  - JWT Authentication     │
                                │  - 2FA (SMS + Voice)      │
                                │  - Content Management     │
                                │  - Firebase Storage       │
                                └────────────┬─────────────┘
                                             │
                          ┌──────────────────┼──────────────────┐
                          │                  │                   │
                 ┌────────▼──────┐  ┌───────▼────────┐  ┌──────▼──────┐
                 │   MariaDB     │  │ FastAPI Voice  │  │  Firebase   │
                 │  (Port 3307)  │  │  (Port 8000)   │  │  Storage    │
                 └───────────────┘  └────────────────┘  └─────────────┘
```

The Spring Boot API communicates with:
- **MariaDB** for all persistent data
- **FastAPI microservice** for voice enrollment and verification
- **Firebase Storage** for movie and series cover images
- **Twilio** for sending SMS verification codes

---

## Tech Stack

- **Java 17**
- **Spring Boot 4.0.2**
- **Spring Security** (stateless JWT, no sessions)
- **Spring Data JPA / Hibernate**
- **MariaDB**
- **JJWT** (JWT generation and validation)
- **Twilio SDK** (SMS 2FA)
- **Firebase Admin SDK** (image storage)
- **Spring WebFlux WebClient** (communication with FastAPI)
- **Lombok**
- **Maven**

---

## Authentication Flow

The system uses a **two-stage login** for regular users, with roles determining the flow.

### Registration

```
POST /api/usuario/registro
    │
    ├── Creates user with hashed password (BCrypt)
    ├── Assigns role: USER, estado: ACTIVO
    ├── Saves preferred 2FA method (SMS or VOZ)
    └── Returns: { idUsuario, nombre, correo, ... }

POST /api/voz/enroll/{idUsuario}   ← Only for users who chose VOZ
    │
    └── Sends audio to FastAPI → stores voice embedding in DB
```

### Login — USER role (2FA required)

```
Step 1: POST /api/usuario/login/password
    │
    ├── Validates email + password
    ├── If SMS method → sends 6-digit code via Twilio
    └── Returns: { token (tempToken), tipo: "Bearer_TEMP", metodoAuth }

Step 2a (SMS): POST /api/2fa/verificar-sms
    │
    ├── Validates tempToken (scope: 2fa-pending)
    ├── Extracts email from token
    ├── Verifies SMS code (5 min expiry, single use)
    └── Returns: { token (JWT), tipo: "Bearer", nombre, rol }

Step 2b (Voice): POST /api/2fa/voz
    │
    ├── Validates tempToken
    ├── Extracts email from token
    ├── Sends audio to FastAPI → cosine similarity check
    └── Returns: { token (JWT), tipo: "Bearer", nombre, rol }
```

### Login — ADMIN role (no 2FA)

```
POST /api/usuario/login/password
    │
    └── Returns: { token (JWT), tipo: "Bearer", nombre, rol }
        directly, no second step required
```

### Token Types

| Type | Field `tipo` | Usage |
|---|---|---|
| Temp Token | `Bearer_TEMP` | Only valid for 2FA verification endpoints |
| JWT | `Bearer` | Access to all protected endpoints |

The `JwtAuthenticationFilter` rejects temp tokens on protected routes — they cannot be used to bypass 2FA.

---

## API Endpoints

### Public (no authentication required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/usuario/registro` | Register new user |
| POST | `/api/usuario/login/password` | Login with password |
| POST | `/api/2fa/verificar-sms` | Verify SMS code |
| POST | `/api/2fa/voz` | Verify via voice |
| POST | `/api/voz/enroll/{idUsuario}` | Enroll voice after registration |
| GET | `/api/metodo-auth` | List available 2FA methods (for registration form) |

### Protected (requires `Authorization: Bearer <token>`)

#### Users

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/categoria` | List all categories |

#### Movies

| Method | Endpoint | Body / Params | Description |
|---|---|---|---|
| GET | `/api/pelicula/list` | — | List all movies |
| GET | `/api/pelicula/{id}` | — | Get movie by id |
| POST | `/api/pelicula` | `multipart: datos (JSON) + imagen (file)` | Create movie |
| PUT | `/api/pelicula/{id}` | `multipart: datos (JSON) + imagen (file)` | Update movie |
| PATCH | `/api/pelicula/{id}/estado/{estado}` | — | Change status (ACTIVO/INACTIVO) |
| DELETE | `/api/pelicula/{id}` | — | Delete movie |

#### Series

| Method | Endpoint | Body / Params | Description |
|---|---|---|---|
| GET | `/api/serie` | — | List all series |
| GET | `/api/serie/{id}` | — | Get serie by id |
| POST | `/api/serie` | `multipart: datos (JSON) + imagen (file)` | Create serie |
| PUT | `/api/serie/{id}` | `multipart: datos (JSON) + imagen (file)` | Update serie |
| PATCH | `/api/serie/{id}/estado/{estado}` | — | Change status (ACTIVO/INACTIVO) |
| DELETE | `/api/serie/{id}` | — | Delete serie |

### Request / Response Examples

**Registration**
```json
POST /api/usuario/registro
{
  "nombre": "Rafael",
  "apellido_1": "Solano",
  "correo": "rafael@email.com",
  "password": "mypassword",
  "telefono": "88888888",
  "metodoAuth": 1
}
```

**Login response (USER)**
```json
{
  "token": "eyJhbGci...",
  "tipo": "Bearer_TEMP",
  "metodoAuth": 1
}
```

**SMS verification**
```json
POST /api/2fa/verificar-sms
{
  "tempToken": "eyJhbGci...",
  "codigo": "482910"
}
```

**Final JWT response**
```json
{
  "token": "eyJhbGci...",
  "tipo": "Bearer",
  "nombre": "Rafael",
  "rol": "USER"
}
```

---

## Database Schema

```
usuario
├── id_usuario    VARCHAR(100) PK  (UUID)
├── nombre
├── apellido_1
├── correo        UNIQUE
├── password      (BCrypt hashed)
├── telefono
├── fecha_registro
├── id_rol        FK → rol
├── id_estado     FK → estado
└── id_metodo_auth FK → metodo_auth

rol                     estado              metodo_auth
├── id_rol (PK)         ├── id_estado (PK)  ├── id_metodo_auth (PK)
└── nombre              └── nombre          └── nombre
    USER, ADMIN             ACTIVO              SMS, VOZ
                            INACTIVO

pelicula / serie
├── id_pelicula / id_serie (PK)
├── titulo
├── año
├── descripcion
├── id_categoria  FK → categoria
├── id_estado     FK → estado
└── id_imagen     FK → imagen (nullable)

imagen
├── id_imagen (PK)
├── nombre_archivo
├── ruta_firebase
└── fecha_carga

registro_error
├── id_error (PK)
├── mensaje
└── fecha
```

> **Note:** The `voz_usuario` table lives in the **VoiceRecognition** microservice database, not here. It stores voice embeddings keyed by the user's UUID.

---

## Environment Variables

Configure these before starting the application. They can be set as system environment variables or in your IDE run configuration.

```bash
# Google OAuth2 (configured, not yet active)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Twilio (SMS 2FA)
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_FROM_PHONE=+1234567890
```

The following are configured directly in `application.properties`:

| Property | Value | Description |
|---|---|---|
| `server.port` | `80` | API port |
| `spring.datasource.url` | `jdbc:mariadb://localhost:3307/pstreaming` | DB connection |
| `voice.ms.url` | `http://127.0.0.1:8000` | FastAPI microservice URL |
| `jwt.expiration` | `21600000` | JWT expiry (6 hours in ms) |
| `jwt.temp.expiration` | `864000` | Temp token expiry (~14 min in ms) |
| `sms.code.expiration` | `300` | SMS code expiry (5 min in seconds) |

> ⚠️ **Security note:** `application.properties` currently contains hardcoded DB credentials and the mail password. Move these to environment variables before any deployment.

---

## Running Locally

### Prerequisites

- JDK 17+
- Maven 3.9+
- MariaDB running on port 3307
- VoiceRecognition microservice running on port 8000 (for voice features)

### Steps

1. Clone the repository
2. Create the database:
```sql
CREATE DATABASE pstreaming;
CREATE USER 'usuariop'@'localhost' IDENTIFIED BY 'Usuariop!';
GRANT ALL PRIVILEGES ON pstreaming.* TO 'usuariop'@'localhost';
```
3. Insert base data (roles, estados, metodos):
```sql
INSERT INTO rol (nombre) VALUES ('USER'), ('ADMIN');
INSERT INTO estado (nombre) VALUES ('ACTIVO'), ('INACTIVO');
INSERT INTO metodo_auth (nombre) VALUES ('SMS'), ('VOZ');
```
4. Set environment variables for Twilio and Google
5. Run:
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:80`.

> On first run with `ddl-auto=update`, Hibernate creates all tables automatically.

---

## Running with Docker

The project includes a `Dockerfile` and `docker-compose.yml`.

1. Copy `ini.env` to `.env` and fill in your credentials
2. Run:
```bash
docker compose up --build
```

This starts both the MariaDB container and the Spring Boot application.

---

## VoiceRecognition Microservice

The voice biometric system runs as a separate Python FastAPI service in the `VoiceRecognition/` folder.

**Model:** SpeechBrain ECAPA-VOXCELEB — generates voice embeddings using cosine similarity with a threshold of 0.90.

**Endpoints used by Spring Boot:**

| Method | Endpoint | Description |
|---|---|---|
| POST | `/enroll/{idUsuario}` | Store voice embedding for user |
| POST | `/verify/{idUsuario}` | Verify audio against stored embedding |

**Setup:**
```bash
cd VoiceRecognition
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8000
```

Requires its own `.env` with `DATABASE_URL` and ffmpeg installed and configured via `FFMPEG_BIN` / `FFPROBE_BIN`.
