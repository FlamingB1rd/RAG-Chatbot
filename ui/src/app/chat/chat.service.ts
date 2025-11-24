import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  constructor(private http: HttpClient) {}

  send(question: string, conversationId: number | null): Observable<string> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const body = {
      question: question,
      conversationId: conversationId
    };

    return this.http.post(`/api/chat`, body, { headers, responseType: 'text' });
  }

  // stream(question: string): Observable<string> {
  //   const url = `/api/chat/stream?question=${encodeURIComponent(question)}`;
  //
  //   return this.sse.stream(url).pipe(map((e: any) => String(e.data)));
  // }
}
