import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Conversation {
  id: number;
  title: string;
  createdAt: string;
  updatedAt: string;
}

export interface ChatMessage {
  id: number;
  role: 'user' | 'assistant';
  content: string;
  createdAt: string;
}

export interface ConversationDetail {
  id: number;
  title: string;
  createdAt: string;
  updatedAt: string;
  messages: ChatMessage[];
}

@Injectable({
  providedIn: 'root'
})
export class ConversationService {
  constructor(private http: HttpClient) {}

  createConversation(title?: string): Observable<Conversation> {
    const body = title ? { title } : {};
    return this.http.post<Conversation>('/api/conversations', body);
  }

  getUserConversations(): Observable<Conversation[]> {
    return this.http.get<Conversation[]>('/api/conversations');
  }

  getConversation(id: number): Observable<ConversationDetail> {
    return this.http.get<ConversationDetail>(`/api/conversations/${id}`);
  }

  deleteConversation(id: number): Observable<void> {
    return this.http.delete<void>(`/api/conversations/${id}`);
  }
}

