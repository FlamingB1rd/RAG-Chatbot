import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly baseUrl = '/api/data';

  constructor(private http: HttpClient) {}

  /**
   * Calls POST /api/data/ingest with { url: ... }
   * Backend returns plain string.
   */
  addContextFromUrl(url: string): Observable<string> {
    return this.http.post(
      `${this.baseUrl}/ingest`,
      { url },
      { responseType: 'text' }
    );
  }
}
