# StreamingApp — API Test Guide

Test data and end-to-end workflows for the backend: user register, login, 2FA (SMS + voice), series and movies.

- **Base URL:** `http://localhost` (server runs on port **80**, see `application.properties`)
- **Auth scheme:** JWT Bearer token in the `Authorization` header
- **Frontend origin allowed by CORS:** `http://localhost:5173`

---

## 0. Read this before testing (current blockers)

### 0.1 Security whitelist is out of sync with the controllers
`ProjectConfig.java` still `permitAll()`s the **old Spanish paths**, but the controllers were renamed to English. Until this is fixed, the public endpoints below return **403 Forbidden**:

| Whitelisted in `ProjectConfig` (old) | Actual controller path (current) | Status |
|---|---|---|
| `/api/usuario/registro`        | `/api/user/register` | ❌ blocked |
| `/api/usuario/login/password`  | `/api/user/login`    | ❌ blocked |
| `/api/2fa/verificar-sms`       | `/api/2fa/sms`       | ❌ blocked |
| `/api/2fa/voz`                 | `/api/2fa/voz`       | ✅ matches |
| `/api/voz/enroll/*`            | `/api/voz/enroll/{id}` | ✅ matches |
| `/api/metodo-auth`             | `/api/metodo-auth`   | ✅ matches |

**Fix:** update the `requestMatchers(...)` list in `ProjectConfig.java:30` to:
```java
.requestMatchers(
        "/api/user/register",
        "/api/user/login",
        "/api/2fa/sms",
        "/api/2fa/voz",
        "/api/voz/enroll/*",
        "/api/metodo-auth"
).permitAll()
```
> Everything else (`/api/serie`, `/api/movie`, `/api/categoria`, ...) is `authenticated()` — you need a full Bearer token to call them.

### 0.2 The database is recreated on every boot
`spring.jpa.hibernate.ddl-auto=create` drops and recreates the schema each startup, so the lookup tables start **empty**. Registration calls `findByName("ACTIVO")` (status) and `findByName("USER")` (rol); series/movies need a `category`. Seed this data first (section 1).

### 0.3 External services
- **Voice 2FA** (`/api/voz/...`, `/api/2fa/voz`) calls a FastAPI service at `voice.ms.url=http://127.0.0.1:36/`. It must be running for enroll/verify to work.
- **SMS 2FA** sends real codes via Twilio (`TWILIO_*` env vars). Needs valid credentials + a reachable phone.

---

## 1. Seed data (run once after the app starts)

Connect to MariaDB (`localhost:3307`, db `pstreaming`, user `usuariop`) and run:

```sql
-- Roles
INSERT INTO rol (name) VALUES ('USER'), ('ADMIN');

-- Statuses
INSERT INTO status (name) VALUES ('ACTIVO'), ('INACTIVO');

-- 2FA / auth methods  (order matters: SMS becomes id 1, VOZ id 2)
INSERT INTO metodo_auth (name) VALUES ('SMS'), ('VOZ');

-- Categories for series/movies
INSERT INTO category (name) VALUES ('Acción'), ('Comedia'), ('Drama'), ('Terror');
```

Resulting IDs (fresh DB, identity columns start at 1):

| Table | id | name |
|---|---|---|
| rol | 1 / 2 | USER / ADMIN |
| status | 1 / 2 | ACTIVO / INACTIVO |
| metodo_auth | 1 / 2 | SMS / VOZ |
| category | 1..4 | Acción / Comedia / Drama / Terror |

Use these IDs in the `authMethod` / `idCategory` fields below.

---

## 2. User registration

`POST /api/user/register` — public. Body = `UserRegisterRequest` (JSON).

```json
{
  "name": "Rafael",
  "surname": "Solano",
  "email": "rafael@correo.com",
  "password": "Password123!",
  "phone": "+50688887777",
  "authMethod": 1
}
```
- `authMethod`: `1` = SMS, `2` = VOZ (from the seed).
- On success → **201 Created**, returns `UserResponse` (note: `idUsuario` is the UUID you need for voice enrollment).

```bash
curl -X POST http://localhost/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Rafael","surname":"Solano","email":"rafael@correo.com","password":"Password123!","phone":"+50688887777","authMethod":1}'
```

A second user for voice testing:
```json
{
  "name": "Ana",
  "surname": "López",
  "email": "ana@correo.com",
  "password": "Password123!",
  "phone": "+50677776666",
  "authMethod": 2
}
```

**Failure cases to try:**
- Duplicate email → `RuntimeException` "ya se encuentra registrado".
- `authMethod` id not in `metodo_auth` → "Metodo Invalido".
- No `ACTIVO` status seeded → "no cuenta con un estado definido".

---

## 3. Login (step 1 of 2FA)

`POST /api/user/login` — public. Body = `UserLoginRequest`.

```json
{
  "email": "rafael@correo.com",
  "password": "Password123!"
}
```

Behavior (see `UserController.userTempLoginPassword`):
- Wrong email/password → **401**.
- Role `USER` → returns a **temp token** and triggers 2FA. If method is `SMS`, a code is sent to the phone. Response:
  ```json
  {
    "token": "<TEMP_JWT>",
    "tokenType": "Bearer_TEMP",
    "authMethod": 1
  }
  ```
- Non-`USER` role (e.g. ADMIN) → returns a **full token** immediately (`tokenType: "Bearer"`, includes `name`, `rol`), no 2FA.

```bash
curl -X POST http://localhost/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"email":"rafael@correo.com","password":"Password123!"}'
```

> Keep the temp token — it's the input to step 2.

---

## 4. 2FA — step 2

### 4.1 SMS verification
`POST /api/2fa/sms` — public. Body = `SmsVerifyRequest`.

```json
{
  "tempToken": "<TEMP_JWT from /login>",
  "code": "123456"
}
```
- Invalid/expired temp token → **401**.
- Wrong code → **401** (code TTL = `sms.code.expiration=300` seconds).
- Success → **200** with full token:
  ```json
  {
    "token": "<FULL_JWT>",
    "tokenType": "Bearer",
    "idUsuario": "uuid...",
    "name": "Rafael",
    "rol": "USER"
  }
  ```

```bash
curl -X POST http://localhost/api/2fa/sms \
  -H "Content-Type: application/json" \
  -d '{"tempToken":"<TEMP_JWT>","code":"123456"}'
```

### 4.2 Voice enrollment (one-time, before voice login)
`POST /api/voz/enroll/{idUsuario}` — public. `multipart/form-data` with part `audio` (the voice sample). `{idUsuario}` is the UUID from registration.

```bash
curl -X POST http://localhost/api/voz/enroll/<UUID> \
  -F "audio=@sample.wav"
```
- User not found → **404**. FastAPI failure → **500**. Success → **200**.

### 4.3 Voice verification
`POST /api/2fa/voz` — public. Temp token goes in a **header** `X-Temp-Token`, audio as multipart part `audio`.

```bash
curl -X POST http://localhost/api/2fa/voz \
  -H "X-Temp-Token: <TEMP_JWT>" \
  -F "audio=@sample.wav"
```
- Invalid temp token or failed match → **401**. Success → **200** with full token (same shape as SMS success).

---

## 5. Using the token on protected endpoints

All `/api/serie`, `/api/movie`, `/api/categoria`, `/api/metodo-auth*` (except the whitelisted ones) require:

```
Authorization: Bearer <FULL_JWT>
```
Full token lifetime = `jwt.expiration=21600000` ms (6 h). Temp token = `jwt.temp.expiration` (≈14 min).

---

## 6. Categories & auth methods (helpers)

```bash
# Auth methods (public)
curl http://localhost/api/metodo-auth

# Categories (requires Bearer token)
curl http://localhost/api/categoria -H "Authorization: Bearer <FULL_JWT>"
```
Responses: list of `{ "idCategory": 1, "name": "Acción" }` / `{ "idMethod": 1, "name": "SMS" }`.

---

## 7. Series

Base path `/api/serie`. All require a Bearer token.

| Method | Path | Purpose |
|---|---|---|
| GET  | `/api/serie`                    | List all series |
| GET  | `/api/serie/{id}`               | Get one serie |
| POST | `/api/serie`                    | Create (multipart) |
| PUT  | `/api/serie/{id}`               | Update (multipart) |
| PATCH| `/api/serie/{id}/estado/{estado}` | Change status |

### Create — `POST /api/serie`
`multipart/form-data` with **two parts**: `datos` (JSON = `SerieCreateRequest`) and optional `imagen` (image file). Note `publishYear` is a **date** (`YYYY-MM-DD`), and status is forced to `ACTIVO` server-side.

`datos` JSON:
```json
{
  "title": "Breaking Bad",
  "publishYear": "2008-01-20",
  "seasons": 5,
  "episodes": 62,
  "description": "Un profesor de química se convierte en fabricante de metanfetamina.",
  "idCategory": 3
}
```

curl (JSON part + file):
```bash
curl -X POST http://localhost/api/serie \
  -H "Authorization: Bearer <FULL_JWT>" \
  -F 'datos={"title":"Breaking Bad","publishYear":"2008-01-20","seasons":5,"episodes":62,"description":"Quimica y crimen","idCategory":3};type=application/json' \
  -F "imagen=@poster.jpg"
```
PowerShell note: `curl.exe` (not the `curl` alias) handles `-F` the same way. To send only `datos` with no image, omit the `-F "imagen=..."` line.

Response = `SerieResponse`:
```json
{
  "idSerie": 1, "title": "Breaking Bad", "publishYear": "2008-01-20",
  "seasons": 5, "episodes": 62, "urlImage": "https://...", "description": "...",
  "category": "Drama", "status": "ACTIVO"
}
```

### Update — `PUT /api/serie/{id}`
Same multipart shape (`datos` = `SerieUpdateRequest`, optional `imagen`). Only non-null / non-zero fields are applied — so `seasons`/`episodes` left at `0` are ignored.

### Change status — `PATCH /api/serie/{id}/estado/{estado}`
`{estado}` is the status **name** (must exist in `status`):
```bash
curl -X PATCH http://localhost/api/serie/1/estado/INACTIVO \
  -H "Authorization: Bearer <FULL_JWT>"
```

---

## 8. Movies

Base path `/api/movie`. All require a Bearer token. Same multipart pattern as series (no `seasons`/`episodes`).

| Method | Path | Purpose |
|---|---|---|
| GET  | `/api/movie/list`               | List all movies |
| GET  | `/api/movie/{id}`               | Get one movie |
| POST | `/api/movie`                    | Create (multipart) |
| PUT  | `/api/movie/{id}`               | Update (multipart) |
| PATCH| `/api/movie/{id}/estado/{estado}` | Change status |

> Note the list path is `/api/movie/list` (series uses bare `/api/serie`).

### Create — `POST /api/movie`
`datos` JSON (`MovieCreateRequest`):
```json
{
  "title": "Inception",
  "publishYear": "2010-07-16",
  "description": "Un ladrón roba secretos a través de los sueños.",
  "idCategory": 1
}
```
```bash
curl -X POST http://localhost/api/movie \
  -H "Authorization: Bearer <FULL_JWT>" \
  -F 'datos={"title":"Inception","publishYear":"2010-07-16","description":"Sueños dentro de sueños","idCategory":1};type=application/json' \
  -F "imagen=@inception.jpg"
```
Response = `MovieResponse` (`idMovie`, `title`, `publishYear`, `urlImage`, `description`, `category`, `status`).

### Change status
```bash
curl -X PATCH http://localhost/api/movie/1/estado/INACTIVO \
  -H "Authorization: Bearer <FULL_JWT>"
```

---

## 9. Full happy-path script (SMS user)

```bash
# 1. Register
curl -X POST http://localhost/api/user/register -H "Content-Type: application/json" \
  -d '{"name":"Rafael","surname":"Solano","email":"rafael@correo.com","password":"Password123!","phone":"+50688887777","authMethod":1}'

# 2. Login -> copy "token" (Bearer_TEMP)
curl -X POST http://localhost/api/user/login -H "Content-Type: application/json" \
  -d '{"email":"rafael@correo.com","password":"Password123!"}'

# 3. Verify SMS code -> copy "token" (Bearer)
curl -X POST http://localhost/api/2fa/sms -H "Content-Type: application/json" \
  -d '{"tempToken":"<TEMP_JWT>","code":"<CODE_FROM_SMS>"}'

# 4. Use the full token
curl http://localhost/api/serie -H "Authorization: Bearer <FULL_JWT>"
curl http://localhost/api/movie/list -H "Authorization: Bearer <FULL_JWT>"
```

For the **voice** user: register with `authMethod: 2`, enroll via `/api/voz/enroll/{idUsuario}`, login, then `/api/2fa/voz` with the `X-Temp-Token` header instead of step 3.
