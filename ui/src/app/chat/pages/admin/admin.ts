import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatSelectModule} from '@angular/material/select';
import {MatCardModule} from '@angular/material/card';
import {RouterLink} from '@angular/router';
import {AdminService} from '../admin.service';

@Component({
  selector: 'app-admin',
  imports: [FormsModule,
    MatToolbarModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCardModule,
    RouterLink],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class AdminComponent {
  constructor(private adminService: AdminService) {}

  addUrl = '';
  addResult = '';
  addError = '';
  addLoading = false;

  deleteUrl = '';
  deleteResult = '';

  topKOptions = [3, 5, 7];
  similarityOptions = [0.65, 0.7, 0.75, 0.8];

  selectedTopK = 5;
  selectedSimilarity = 0.65;

  onAddContext() {
    const url = this.addUrl.trim();
    if (!url) return;

    this.addLoading = true;
    this.addResult = '';
    this.addError = '';

    this.adminService.addContextFromUrl(url).subscribe({
      next: (res) => {
        console.log('backend said:', res);
        this.addResult = res;      // 👈 this is what the template shows
        this.addLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.addError = 'Грешка при добавяне на контекст: ' + (err?.message ?? '');
        this.addLoading = false;
      }
    });
  }

  onDeleteContext() {
    // TODO: call backend: /api/admin/delete
    this.deleteResult = `Will send to backend: delete context for URL = ${this.deleteUrl}`;
  }

  onSaveConfig() {
    // TODO: call backend: /api/admin/config
    console.log('Save config', this.selectedTopK, this.selectedSimilarity);
  }

  // TODO: Add a proper service to connect to data ingest backend.
  // constructor(private http: HttpClient) {}

  // onAddContext() {
  //   this.http.post('/api/admin/add-url', { url: this.addUrl })
  //     .subscribe({
  //       next: () => this.addResult = 'Added successfully.',
  //       error: (err) => this.addResult = 'Error: ' + err.message
  //     });
  // }
}
