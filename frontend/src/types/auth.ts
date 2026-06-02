export type UserRole = 'ADMIN' | 'MANAGER' | 'ANALYST' | 'USER' | string;

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  TokenType: string;
  userId: number;
  email: string;
  role: UserRole;
  expiresInSeconds: number;
}

export interface AuthSession {
  token: string;
  tokenType: string;
  userId: number;
  email: string;
  role: UserRole;
  expiresAt: number;
}
