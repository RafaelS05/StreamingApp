const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:80";

export interface AuthMethod {
  idMethod: number;
  name: string;
}

export interface UserRegisterRequest {
  name: string;
  surname: string;
  email: string;
  password: string;
  phone: string;
  authMethod: number;
}

export interface UserResponse {
  idUsuario: string;
  name: string;
  surname: string;
  email: string;
  phone: string;
  status: string;
  authMethod: number;
}

export interface UserLoginResponse {
  idUsuario: string | null;
  name: string | null;
  rol: string | null;
  authMethod: number | null;
  token: string;
  tokenType: "Bearer" | "Bearer_TEMP";
}

export class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message);
  }
}

async function handleJson<T>(res: Response, errorMessage: string): Promise<T> {
  if (!res.ok) {
    throw new ApiError(res.status, errorMessage);
  }
  return res.json() as Promise<T>;
}

export async function getAuthMethods(): Promise<AuthMethod[]> {
  const res = await fetch(`${API_URL}/api/metodo-auth`);
  return handleJson(res, "Could not load authentication methods");
}

export async function register(data: UserRegisterRequest): Promise<UserResponse> {
  const res = await fetch(`${API_URL}/api/user/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return handleJson(res, "Registration failed");
}

export async function enrollVoice(idUsuario: string, audio: Blob): Promise<void> {
  const form = new FormData();
  form.append("audio", audio, "enroll.webm");
  const res = await fetch(`${API_URL}/api/voz/enroll/${idUsuario}`, {
    method: "POST",
    body: form,
  });
  if (!res.ok) {
    throw new ApiError(res.status, "Voice enrollment failed");
  }
}

export async function login(email: string, password: string): Promise<UserLoginResponse> {
  const res = await fetch(`${API_URL}/api/user/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });
  if (res.status === 401) {
    throw new ApiError(401, "Invalid email or password");
  }
  return handleJson(res, "Login failed");
}

export async function verifySms(tempToken: string, code: string): Promise<UserLoginResponse> {
  const res = await fetch(`${API_URL}/api/2fa/sms`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ tempToken, code }),
  });
  if (res.status === 401) {
    throw new ApiError(401, "Invalid or expired code");
  }
  return handleJson(res, "SMS verification failed");
}

export async function verifyVoice(tempToken: string, audio: Blob): Promise<UserLoginResponse> {
  const form = new FormData();
  form.append("audio", audio, "verify.webm");
  const res = await fetch(`${API_URL}/api/2fa/voz`, {
    method: "POST",
    headers: { "X-Temp-Token": tempToken },
    body: form,
  });
  if (res.status === 401) {
    throw new ApiError(401, "Voice did not match. Try again.");
  }
  return handleJson(res, "Voice verification failed");
}
