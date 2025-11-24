import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Faq {
  id: number;
  question: string;
  answer: string;
}

export interface FaqRequest {
  question: string;
  answer: string;
}

@Injectable({
  providedIn: 'root'
})
export class FaqService {
  private readonly baseUrl = '/api/faq';

  constructor(private http: HttpClient) {}

  getAllFaqs(): Observable<Faq[]> {
    return this.http.get<Faq[]>(this.baseUrl);
  }

  createFaq(question: string, answer: string): Observable<Faq> {
    return this.http.post<Faq>(this.baseUrl, { question, answer });
  }

  deleteFaq(id: number): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/${id}`, { responseType: 'text' as 'json' });
  }
}

