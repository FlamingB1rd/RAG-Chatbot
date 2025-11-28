import { Component, inject, OnInit } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../auth/auth.service';
import { AuthState } from '../../../auth/models/auth.models';
import { FaqService, Faq } from '../faq.service';

@Component({
  selector: 'app-qa',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    FormsModule,
    RouterLink,
    AsyncPipe,
    NgIf
  ],
  templateUrl: './qa.html',
  styleUrl: './qa.css'
})
export class QaComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly faqService = inject(FaqService);
  protected readonly authState$ = this.authService.authState$;

  expandedIndex: number | null = null;
  showAddForm = false;
  newQuestion = '';
  newAnswer = '';
  faqs: Faq[] = [];
  loading = false;
  addLoading = false;
  deleteLoading: number | null = null;

  ngOnInit(): void {
    this.loadFaqs();
  }

  loadFaqs(): void {
    this.loading = true;
    this.faqService.getAllFaqs().subscribe({
      next: (faqs) => {
        this.faqs = faqs;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading FAQs:', err);
        this.loading = false;
      }
    });
  }

  toggle(index: number): void {
    this.expandedIndex = this.expandedIndex === index ? null : index;
  }

  isExpanded(index: number): boolean {
    return this.expandedIndex === index;
  }

  isAdmin(state: AuthState | null): boolean {
    return !!state?.roles?.includes('ROLE_ADMIN');
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.newQuestion = '';
      this.newAnswer = '';
    }
  }

  addFaq(): void {
    if (!this.newQuestion.trim() || !this.newAnswer.trim()) return;

    this.addLoading = true;
    this.faqService.createFaq(this.newQuestion.trim(), this.newAnswer.trim()).subscribe({
      next: (faq) => {
        this.faqs.push(faq);
        this.newQuestion = '';
        this.newAnswer = '';
        this.showAddForm = false;
        this.addLoading = false;
      },
      error: (err) => {
        console.error('Error creating FAQ:', err);
        alert('Грешка при добавяне на FAQ: ' + (err?.message ?? ''));
        this.addLoading = false;
      }
    });
  }

  cancelAdd(): void {
    this.newQuestion = '';
    this.newAnswer = '';
    this.showAddForm = false;
  }

  deleteFaq(faqId: number, index: number, event: Event): void {
    event.stopPropagation();
    if (!confirm('Сигурни ли сте, че искате да изтриете този въпрос/отговор?')) {
      return;
    }

    this.deleteLoading = faqId;
    this.faqService.deleteFaq(faqId).subscribe({
      next: () => {
        this.faqs.splice(index, 1);
        if (this.expandedIndex === index) {
          this.expandedIndex = null;
        } else if (this.expandedIndex !== null && this.expandedIndex > index) {
          this.expandedIndex = this.expandedIndex - 1;
        }
        this.deleteLoading = null;
      },
      error: (err) => {
        console.error('Error deleting FAQ:', err);
        alert('Грешка при изтриване на FAQ: ' + (err?.message ?? ''));
        this.deleteLoading = null;
      }
    });
  }
}
