# 🎬 StreamingApp — Streaming Platform

A full-stack, Netflix-inspired streaming platform built with **Spring Boot 4** and **Java 17**, featuring advanced authentication including **voice biometrics** and **two-factor authentication (2FA)**. Includes a companion **Python FastAPI microservice** for AI-powered speaker recognition.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Running with Docker](#running-with-docker)
  - [Running Locally](#running-locally)
- [Voice Recognition Microservice](#voice-recognition-microservice)
- [Project Structure](#project-structure)
- [Security](#security)
- [Contributing](#contributing)

---

## Overview

StreamingApp is a full-featured content streaming platform that allows users to browse movies and TV series, manage subscriptions, and authenticate via multiple methods. What sets it apart is its **voice biometric authentication** system — users can enroll their voice and use it as a second factor or primary identifier, powered by a PyTorch/SpeechBrain ML model.

---

## ✨ Features

- **Content Catalog** — Browse movies and TV series organized by category
- **Role-Based Access** — `ADMIN` and `USER` roles with protected routes
- **User Registration & Login** — Secure BCrypt-hashed passwords
- **Google OAuth2** — Sign in with Google
- **Two-Factor Authentication (2FA)** — OTP codes delivered via SMS (Twilio)
- **Voice Biometric Authentication** — Enroll and verify users by voice using a deep learning model
- **Subscription Tiers** — BASICA, ESTANDAR, and PREMIUM plans
- **Email Notifications** — Transactional emails via Gmail SMTP
- **Cloud Media Storage** — Thumbnails and media served from Firebase / Google Cloud Storage
- **QR Code Generation** — For 2FA setup flows
- **Session Management** — 30-minute timeout with secure, HTTP-only cookies

---

## 🛠 Tech Stack

### Backend
| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.0 |
| Spring Security + OAuth2 | Latest |
| Spring Data JPA (Hibernate) | Latest |
| Spring WebFlux | Latest |
| MariaDB / MySQL | 8.0 |

### Frontend
| Technology | Version |
|---|---|
| Thymeleaf | Latest |
| Bootstrap | 5.3.8 |
| jQuery | 3.7.1 |
| Font Awesome | 7.0.1 |

### External Services
| Service | Purpose |
|---|---|
| Google Cloud Storage | Media / image hosting |
| Firebase Admin SDK | Backend cloud services |
| Twilio | SMS for 2FA |
| Microsoft Cognitive Services Speech | Speech-to-text |
| Gmail SMTP | Email notifications |

### Voice Recognition Microservice (Python)
| Technology | Purpose |
|---|---|
| FastAPI + Uvicorn | REST API server |
| PyTorch + SpeechBrain | ECAPA-VoxCeleb speaker recognition model |
| librosa / soundfile | Audio processing |
| SQLAlchemy + PyMySQL | Voice embedding storage |

---

## 🏗 Architecture

```
┌──────────────────────────────────────────────────┐
│                  Web Browser                     │
│           (Thymeleaf + Bootstrap UI)             │
└─────────────────────┬────────────────────────────┘
                      │ HTTP / HTTPS
┌─────────────────────▼────────────────────────────┐
│         Spring Boot Application (Port 80)        │
│                                                  │
│  Controllers → Services → Repositories           │
│                                                  │
│  ┌─────────────┐  ┌───────────┐  ┌───────────┐  │
│  │  Auth /     │  │ Content   │  │   Users / │  │
│  │  OAuth2 /   │  │ (Movies,  │  │   Roles / │  │
│  │  2FA / Voice│  │  Series)  │  │   Subs.   │  │
│  └─────────────┘  └───────────┘  └───────────┘  │
└──────┬──────────────┬───────────────┬────────────┘
       │              │               │
┌──────▼──────┐ ┌─────▼──────┐ ┌─────▼──────────────┐
│  MariaDB /  │ │  Firebase  │ │ Voice Recognition   │
│  MySQL DB   │ │  Cloud     │ │ Microservice (Py)   │
│  Port 3307  │ │  Storage   │ │ FastAPI Port 8000   │
└─────────────┘ └────────────┘ └────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

- **Docker** and **Docker Compose** (recommended)
- OR: **Java 17+**, **Maven 3.9+**, **MariaDB/MySQL 8**, **Python 3.9+**
- A Google Cloud project with OAuth2 credentials
- A Firebase project with a service account JSON
- A Twilio account (for SMS 2FA)
- A Gmail account (for email notifications)

---

### Environment Variables

Copy `ini.env` to `.env` and fill in your credentials:

```env
# Database
MYSQL_DATABASE=pstreaming
MYSQL_USER=usuariop
MYSQL_PASSWORD=your_db_password
MYSQL_ROOT_PASSWORD=your_root_password

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Twilio (SMS 2FA)
TWILIO_ACCOUNT_SID=your_twilio_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token
TWILIO_FROM_PHONE=+1234567890

# Gmail SMTP
GMAIL_USERNAME=your_email@gmail.com
GMAIL_PASSWORD=your_app_password
```

> ⚠️ **Never commit `.env` or `ini.env` files with real credentials to version control.**

---

### Running with Docker

```bash
# 1. Clone the repository
git clone https://github.com/your-username/StreamingApp.git
cd StreamingApp

# 2. Set up your environment file
cp ini.env .env
# Edit .env with your credentials

# 3. Build and start all services
docker compose up --build

# App will be available at http://localhost:8080
```

Docker Compose spins up:
- `mysql-db` — MySQL 8.0 on port `3307`
- `streamingapp` — Spring Boot app on port `8080`

> The Voice Recognition microservice must be started separately (see below).

---

### Running Locally

**1. Start the database**
```bash
# Ensure MariaDB/MySQL is running on port 3307
# Create a database named `pstreaming`
```

**2. Configure `application.properties`**
Update `src/main/resources/application.properties` with your local credentials.

**3. Build and run the Spring Boot app**
```bash
mvn clean package -DskipTests
java -jar target/PlataformaStreaming-1.jar
```

**4. Start the Voice Recognition microservice**
```bash
cd VoiceRecognition
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8000
```

The app will be available at **http://localhost:80**.

---

## 🎤 Voice Recognition Microservice

Located in the `VoiceRecognition/` folder, this is a standalone **Python FastAPI** service that handles biometric voice authentication.

**Model:** `speechbrain/spkrec-ecapa-voxceleb` — a state-of-the-art speaker verification model trained on VoxCeleb.

**How it works:**
1. **Enrollment** — User records a voice sample; the model generates a speaker embedding and stores it.
2. **Verification** — On login, a new sample is compared against the stored embedding using cosine similarity (threshold: `0.85`).

**Endpoints:**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/enroll/{usuario_id}` | Register a user's voice |
| `POST` | `/verify/{usuario_id}` | Verify a voice against stored embedding |

**Audio requirements:** Audio is automatically resampled to 16 kHz mono WAV before processing.

---

## 📁 Project Structure

```
StreamingApp/
├── src/main/java/com/pstreaming/
│   ├── PlataformaStreamingApplication.java   # Entry point
│   ├── ProjectConfig.java                    # Spring Security config
│   ├── controller/                           # HTTP controllers (8 classes)
│   ├── domain/                               # JPA entity models (10 classes)
│   ├── service/                              # Business logic (12 services)
│   └── repository/                           # Spring Data JPA repos (8 repos)
├── src/main/resources/
│   ├── application.properties                # App configuration
│   ├── templates/                            # Thymeleaf HTML templates
│   └── static/                               # CSS, JS, images
├── VoiceRecognition/
│   ├── app.py                                # FastAPI voice auth service
│   └── requirements.txt                      # Python dependencies
├── Dockerfile                                # Multi-stage Java build
├── docker-compose.yml                        # Service orchestration
└── ini.env                                   # Environment variable template
```

---

## 🔐 Security

- Passwords hashed with **BCrypt**
- Sessions expire after **30 minutes** of inactivity
- Cookies are **HTTP-only** and `SameSite=Lax`
- OAuth2 handled via Spring Security
- 2FA codes delivered over **SMS (Twilio)**
- Voice verification uses **cosine similarity** with a strict threshold

> **Note for production:** Ensure Spring Security's authorization rules in `ProjectConfig.java` are fully enabled. Review all secrets management practices before deploying publicly.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is for educational and portfolio purposes. Feel free to fork and adapt it.

---

*Built with ❤️ using Spring Boot, PyTorch, and SpeechBrain.*
