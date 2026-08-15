# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# StreamingApp — Project Summary

Streaming platform with JWT auth and two-factor authentication (SMS via Twilio, voice biometrics via a Python microservice). Four components in this monorepo:

| Folder | Stack | Role | Port |
|---|---|---|---|
| `streaming-app-gestion` | C# with .Net core 10 | REST API, only for the content management | to define the port|
| `streaming-app-auth` | Spring Boot 4 (Java 25), Maven | REST API, only for the auth | 80 |
| `streaming-app-front-end` | Next.js 16 (App Router), React 19, TypeScript, Tailwind 4 | User interface | 5173 (dev) |
| `VoiceRecognition` | Python FastAPI, SpeechBrain ECAPA-VOXCELEB | Voice enroll/verify microservice | 8000 |

Infra: MariaDB (port 3307, db `pstreaming`), SSMS (port (localdb)\MSSQLLocalDB, db `pstreaming_content`), Firebase Storage (cover images), Twilio (SMS codes).

> The root `README.md` predates the REST migration — endpoint paths and field names there are outdated (Spanish). The controllers are the source of truth; current contracts are documented below.

## Backend layout

`com.pstreaming` packages: `controller`, `service`, `repository`, `domain`, `dto` (request/response objects). 

Security config in `ProjectConfig.java` (stateless-ish JWT filter, CORS allows only `http://localhost:5173`, credentials on).

## Auth flow (two-stage for USER role)

1. `POST /api/user/register` → body `{ name, surname, email, password, phone, authMethod: number }` → 201 with `UserResponse { idUsuario, name, surname, email, phone, status, authMethod }`. Password is BCrypt-hashed; role USER, status ACTIVO.
2. If the user picked VOZ: `POST /api/voz/enroll/{idUsuario}` — multipart, file field `audio`.
3. `POST /api/user/login` → body `{ email, password }`:
   - USER role → `{ token, tokenType: "Bearer_TEMP", authMethod }` (temp token, scope 2fa-pending; SMS code is auto-sent if method is SMS).
   - ADMIN role → `{ token, tokenType: "Bearer", name, rol }` directly (no 2FA).
   - Bad credentials → 401 with empty body.
4. Second factor (both return full `UserLoginResponse { idUsuario, name, rol, token, tokenType: "Bearer" }`):
   - SMS: `POST /api/2fa/sms` → JSON `{ tempToken, code }` (6-digit code, 5-min expiry, single use).
   - Voice: `POST /api/2fa/voz` → header `X-Temp-Token: <tempToken>`, multipart file field `audio` (cosine similarity ≥ 0.90 via FastAPI).

Public endpoints: the five above plus `GET /api/metodo-auth` → `[{ idMethod, name }]` (SMS=1, VOZ=2 by seed order). Everything else requires `Authorization: Bearer <JWT>`; temp tokens are rejected by `JwtAuthenticationFilter` on protected routes.

## Protected API (content)

- `GET /api/categoria` — categories.
- Movies `/api/pelicula`: `GET /list`, `GET /{id}`, `POST` & `PUT /{id}` (multipart: `datos` JSON + `imagen` file), `PATCH /{id}/estado/{estado}`, `DELETE /{id}`.
- Series `/api/serie`: same shape (`GET /`, `GET /{id}`, POST/PUT multipart, PATCH estado, DELETE).

## Database

Tables: `usuario` (UUID PK, FKs to `rol` USER/ADMIN, `estado` ACTIVO/INACTIVO, `metodo_auth` SMS/VOZ), `pelicula`, `serie`, `categoria`, `imagen` (Firebase path), `registro_error`. Voice embeddings live in the VoiceRecognition service's own DB (`voz_usuario`), keyed by user UUID. Hibernate `ddl-auto=update` creates tables; seed data for rol/estado/metodo_auth must be inserted manually.

## Front end

Next.js App Router under `streaming-app-front-end/src/app`. Dev server must run on **5173** (`npm run dev`) or the backend CORS rejects it. API base URL via `NEXT_PUBLIC_API_URL` (default `http://localhost:80`). Pages: `/login`, `/register` (with voice enrollment step for VOZ), `/2fa` (SMS code or voice recording depending on method). API client in `src/lib/api.ts`; final JWT stored in `localStorage`, temp 2FA state in `sessionStorage`.

## Commands

Backend (run in `streaming-app-auth`; no Maven wrapper — `mvn` must be on PATH, JDK 25):

- Build: `mvn clean package`
- All tests: `mvn test`
- Single class: `mvn test -Dtest=UserControllerTest`
- Single method: `mvn test -Dtest=UserControllerTest#register_returnsCreatedWithBody`

Tests are plain JUnit 5 + Mockito unit tests (`@ExtendWith(MockitoExtension.class)`, AssertJ assertions) — no Spring context or database required, so `mvn test` runs without MariaDB.

Front end (run in `streaming-app-front-end`):

- Dev server: `npm run dev` (port 5173 — hardcoded in the script; required by backend CORS)
- Build: `npm run build`
- Lint: `npm run lint`

`VoiceRecognition` has no tests; deps via `pip install -r requirements.txt`.

## Running

- Backend: `mvn spring-boot:run` in `streaming-app-auth` (needs MariaDB on 3307, env vars `TWILIO_*`, `GOOGLE_CLIENT_ID/SECRET`). Note `voice.ms.url` in `application.properties` must point to the FastAPI service.
- Voice service: `uvicorn app:app --host 0.0.0.0 --port 8000` in `VoiceRecognition` (needs `.env` with `DATABASE_URL`, ffmpeg via `FFMPEG_BIN`/`FFPROBE_BIN`).
- Front end: `npm run dev` in `streaming-app-front-end`.
- Dockerfiles exist for all three components.

## Conventions / notes

- Mixed Spanish/English: domain and comments are Spanish-flavored; the REST layer was migrated to English names (branch `Achitecture-Migration`). DTO field names are English (`name`, `surname`, `email`, `phone`, `authMethod`).
- `application.properties` still has hardcoded DB credentials and mail password — known issue, don't propagate secrets.
- Login response for USER never includes name/rol — only the 2FA verification responses do.

## Available skills

- `.claude/skills/scaffold-vertical-slice/` — use when adding a new feature 
  or extending an existing slice with a DTO/repo/service.