import { Routes } from '@angular/router';
import { ChatPageComponent } from './chat/chat-page/chat-page.component';
import { QaComponent } from './chat/pages/qa/qa';
import { AdminComponent } from './chat/pages/admin/admin';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { authGuard } from './auth/auth.guard';
import { roleGuard } from './auth/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'chat' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'chat', component: ChatPageComponent, canActivate: [authGuard] },
  { path: 'qa', component: QaComponent, canActivate: [authGuard] },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  { path: '**', redirectTo: 'chat' }
];
