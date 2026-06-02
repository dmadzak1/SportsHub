import { createContext, useContext, useMemo, useState, type ReactNode } from 'react';
import { apiClient } from '../lib/api';
import { clearSession, createSession, loadSession, saveSession } from '../lib/auth';
import type { AuthSession, LoginRequest, LoginResponse } from '../types/auth';

interface AuthContextValue {
  session: AuthSession | null;
  login: (request: LoginRequest) => Promise<AuthSession>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(() => loadSession());

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      login: async (request: LoginRequest) => {
        const response = await apiClient.post<LoginResponse>('/user/auth/login', request);
        const nextSession = createSession(response);
        saveSession(nextSession);
        setSession(nextSession);
        return nextSession;
      },
      logout: () => {
        clearSession();
        setSession(null);
      }
    }),
    [session]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }

  return context;
}
