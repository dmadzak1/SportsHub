import type { AuthSession, LoginResponse } from '../types/auth';

const AUTH_STORAGE_KEY = 'sportshub_auth_session';

export function createSession(response: LoginResponse): AuthSession {
  return {
    token: response.token,
    tokenType: response.TokenType,
    userId: response.userId,
    email: response.email,
    role: response.role,
    expiresAt: Date.now() + response.expiresInSeconds * 1000
  };
}

export function saveSession(session: AuthSession): void {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
}

export function loadSession(): AuthSession | null {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);

  if (!raw) {
    return null;
  }

  try {
    const session = JSON.parse(raw) as AuthSession;
    if (session.expiresAt <= Date.now()) {
      clearSession();
      return null;
    }

    return session;
  } catch {
    clearSession();
    return null;
  }
}

export function clearSession(): void {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}

export function getAuthorizationHeader(): string | null {
  const session = loadSession();

  if (!session) {
    return null;
  }

  return `${session.tokenType ?? 'Bearer'} ${session.token}`;
}
