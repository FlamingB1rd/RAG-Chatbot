import { Routes } from '@angular/router';
import { ChatPageComponent } from './chat/chat-page/chat-page.component';
import {QaComponent} from './chat/pages/qa/qa';
import {AdminComponent} from './chat/pages/admin/admin';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'chat' },
  { path: 'chat', component: ChatPageComponent },
  { path: 'qa', component: QaComponent },
  { path: 'admin', component: AdminComponent },
  { path: '**', redirectTo: 'chat' }
];
