import { NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterLink,
    NgIf
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = this.fb.nonNullable.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  protected submitting = false;
  protected error?: string;

  submit(): void {
    if (this.submitting) {
      return;
    }

    if (!this.form.controls.username.value || !this.form.controls.password.value) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.error = undefined;

    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => {
        const redirectTo = this.route.snapshot.queryParamMap.get('redirectTo');
        const target = redirectTo && redirectTo !== '/login' ? redirectTo : '/chat';
        this.router.navigateByUrl(target);
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Неуспешен вход. Моля, опитайте отново.';
        this.submitting = false;
      }
    });
  }
}

