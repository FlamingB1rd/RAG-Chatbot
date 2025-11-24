import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface UrlContent {
  url: string;
  content: string;
}

export interface ChatConfig {
  topK: number;
  similarityThreshold: number;
  cronExpression?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly dataUrl = '/api/data';
  private readonly adminUrl = '/api/admin';

  constructor(private http: HttpClient) {}

  addContextFromUrl(url: string): Observable<void> {
    return this.http.post<void>(
      `${this.dataUrl}/ingest`,
      { url }
    );
  }

  getAllUrls(): Observable<string[]> {
    return this.http.get<string[]>(`${this.dataUrl}/urls`);
  }

  getContentByUrl(url: string): Observable<UrlContent> {
    const params = new HttpParams().set('url', url);
    return this.http.get<UrlContent>(`${this.dataUrl}/url`, { params });
  }

  deleteByUrl(url: string): Observable<string> {
    const params = new HttpParams().set('url', url);
    return this.http.delete<string>(`${this.dataUrl}/url`, { params, responseType: 'text' as 'json' });
  }

  upgrade(): Observable<string> {
    return this.http.post<string>(`${this.dataUrl}/upgrade`, {}, { responseType: 'text' as 'json' });
  }

  getConfig(): Observable<ChatConfig> {
    return this.http.get<ChatConfig>(`${this.adminUrl}/config`);
  }

  updateConfig(topK: number, similarityThreshold: number, cronExpression?: string): Observable<ChatConfig> {
    return this.http.post<ChatConfig>(`${this.adminUrl}/config`, { topK, similarityThreshold, cronExpression });
  }

  // User Management
  getAllUsers(): Observable<UserInfo[]> {
    return this.http.get<UserInfo[]>(`${this.adminUrl}/users`);
  }

  updateUserRole(userId: number, role: string): Observable<UserInfo> {
    return this.http.put<UserInfo>(`${this.adminUrl}/users/${userId}/role`, { role });
  }

  deleteUser(userId: number): Observable<string> {
    return this.http.delete<string>(`${this.adminUrl}/users/${userId}`, { responseType: 'text' as 'json' });
  }

  // Audit Logs
  getAuditLogs(): Observable<AuditLogInfo[]> {
    return this.http.get<AuditLogInfo[]>(`${this.adminUrl}/audit/logs`);
  }

  // Roles
  getAllRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${this.adminUrl}/users/roles`);
  }

  // Scheduled URLs
  getAllScheduledUrls(): Observable<ScheduledUrlInfo[]> {
    return this.http.get<ScheduledUrlInfo[]>(`${this.adminUrl}/scheduled-urls`);
  }

  createScheduledUrl(url: string, description?: string): Observable<ScheduledUrlInfo> {
    return this.http.post<ScheduledUrlInfo>(`${this.adminUrl}/scheduled-urls`, { url, description });
  }

  deleteScheduledUrl(id: number): Observable<string> {
    return this.http.delete<string>(`${this.adminUrl}/scheduled-urls/${id}`, { responseType: 'text' as 'json' });
  }

  toggleScheduledUrl(id: number): Observable<ScheduledUrlInfo> {
    return this.http.put<ScheduledUrlInfo>(`${this.adminUrl}/scheduled-urls/${id}/toggle`, {});
  }
}

export interface ScheduledUrlInfo {
  id: number;
  url: string;
  description: string | null;
  createdAt: string;
  createdBy: string | null;
  isActive: boolean;
}

export interface UserInfo {
  id: number;
  username: string;
  email: string;
  roles: string[];
}

export interface AuditLogInfo {
  id: number;
  actionType: string;
  entityType: string;
  entityId: number | null;
  description: string;
  performedBy: string;
  performedAt: string;
  oldValue: string | null;
  newValue: string | null;
}
