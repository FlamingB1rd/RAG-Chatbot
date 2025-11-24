export interface RegisterPayload {
  username: string;
  password: string;
  email: string;
}

export interface LoginPayload {
  username: string;
  password: string;
}

export interface LoginResponse {
  username: string;
  roles: string[];
  token: string;
  tokenType: string;
  expiresInSeconds: number;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  roles: string[];
}

export interface AuthState {
  username: string;
  roles: string[];
  token: string;
  expiresAt: number;
}

