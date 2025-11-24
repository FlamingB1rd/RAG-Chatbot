import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import {
  AuthState,
  LoginPayload,
  LoginResponse,
  RegisterPayload,
  UserResponse
} from './models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly storageKey = 'chatbot-auth-state';
  private readonly authStateSubject = new BehaviorSubject<AuthState | null>(this.loadState());

  readonly authState$ = this.authStateSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  register(payload: RegisterPayload): Observable<UserResponse> {
    return this.http.post<UserResponse>('/api/user/register', payload);
  }

  login(payload: LoginPayload): Observable<void> {
    return this.http.post<LoginResponse>('/api/user/login', payload).pipe(
      tap((response) => {
        const expiresAt = Date.now() + response.expiresInSeconds * 1000;
        const state: AuthState = {
          username: response.username,
          roles: response.roles,
          token: response.token,
          expiresAt
        };
        this.persistState(state);
      }),
      map(() => void 0)
    );
  }

  logout(): void {
    this.persistState(null);
    this.router.navigate(['/login']);
  }

  get token(): string | null {
    const state = this.getActiveState();
    return state?.token ?? null;
  }

  isAuthenticated(): boolean {
    return !!this.getActiveState();
  }

  hasAnyRole(roles: string[]): boolean {
    const state = this.getActiveState();
    if (!state) {
      return false;
    }
    return roles.some((role) => state.roles.includes(role));
  }

  private getActiveState(): AuthState | null {
    const state = this.authStateSubject.getValue();
    if (!state) {
      return null;
    }

    if (Date.now() >= state.expiresAt) {
      this.persistState(null);
      return null;
    }

    return state;
  }

  private loadState(): AuthState | null {
    if (typeof window === 'undefined') {
      return null;
    }

    const raw = localStorage.getItem(this.storageKey);
    if (!raw) {
      return null;
    }

    try {
      const state = JSON.parse(raw) as AuthState;
      if (Date.now() >= state.expiresAt) {
        localStorage.removeItem(this.storageKey);
        return null;
      }
      return state;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }

  private persistState(state: AuthState | null): void {
    if (typeof window === 'undefined') {
      return;
    }

    if (state) {
      localStorage.setItem(this.storageKey, JSON.stringify(state));
    } else {
      localStorage.removeItem(this.storageKey);
    }
    this.authStateSubject.next(state);
  }
}

