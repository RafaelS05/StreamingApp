import type { UserLoginResponse } from "./api";

// Final session (full JWT) lives in localStorage; the short-lived 2FA
// hand-off between /login and /2fa lives in sessionStorage.

const SESSION_KEY = "session";
const PENDING_2FA_KEY = "pending2fa";

export interface Session {
  token: string;
  idUsuario: string | null;
  name: string | null;
  rol: string | null;
}

export interface Pending2FA {
  tempToken: string;
  authMethod: number;
}

// Listeners let useSyncExternalStore react to same-tab session changes;
// the "storage" event only covers other tabs.
const listeners = new Set<() => void>();

function emitSessionChange(): void {
  listeners.forEach((listener) => listener());
}

export function subscribeSession(callback: () => void): () => void {
  listeners.add(callback);
  window.addEventListener("storage", callback);
  return () => {
    listeners.delete(callback);
    window.removeEventListener("storage", callback);
  };
}

export function getSessionRaw(): string | null {
  return localStorage.getItem(SESSION_KEY);
}

export function getServerSessionRaw(): string | null {
  return null;
}

export function saveSession(res: UserLoginResponse): void {
  const session: Session = {
    token: res.token,
    idUsuario: res.idUsuario,
    name: res.name,
    rol: res.rol,
  };
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  sessionStorage.removeItem(PENDING_2FA_KEY);
  emitSessionChange();
}

export function getSession(): Session | null {
  const raw = getSessionRaw();
  return raw ? (JSON.parse(raw) as Session) : null;
}

export function clearSession(): void {
  localStorage.removeItem(SESSION_KEY);
  emitSessionChange();
}

export function savePending2FA(pending: Pending2FA): void {
  sessionStorage.setItem(PENDING_2FA_KEY, JSON.stringify(pending));
}

export function getPending2FA(): Pending2FA | null {
  const raw = sessionStorage.getItem(PENDING_2FA_KEY);
  return raw ? (JSON.parse(raw) as Pending2FA) : null;
}
