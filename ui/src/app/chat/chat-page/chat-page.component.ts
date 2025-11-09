import { Component, ElementRef, ViewChild } from '@angular/core';
import { ChatService } from '../chat.service';
import {Message, Role} from '../../models/message/message.model';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MarkdownComponent } from 'ngx-markdown';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import {RouterLink} from '@angular/router';

function uid() { return Math.random().toString(36).slice(2); }

@Component({
  selector: 'app-chat-page',
  imports: [
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MarkdownComponent,
    FormsModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    DatePipe,
    RouterLink
  ],
  templateUrl: './chat-page.component.html',
  styleUrl: './chat-page.component.scss'
})
export class ChatPageComponent {
  messages: Message[] = [
    {
      id: uid(),
      role: 'assistant',
      text: 'Здрасти! Питай ме нещо за ТУ-София. Ще добавя източници в края на отговора.',
      timeStamp: Date.now()
    }
  ];

  input = '';
  loading = false;
  // private streamSub?: Subscription;
  @ViewChild('bottom') bottomRef?: ElementRef<HTMLDivElement>;

  constructor(private chatService: ChatService) {}

  trackById = (_: number, m: Message) => m.id;

  onSend(): void {
    const question = this.input.trim();

    if (!question || this.loading)
      return;

    this.messages.push({ id: uid(), role: 'user', text: question, timeStamp: Date.now() });
    this.input = '';
    this.loading = true;

    // const assistant = { id: uid(), role: 'assistant', text: '', timeStamp: Date.now() } as Message;
    // this.messages.push(assistant);
    // this.scrollToBottomSoon();

    this.chatService.send(question).subscribe({
      next: (answerStream) => {
        this.push('assistant', answerStream);
        this.loading = false;
        this.scrollToBottomSoon();
      },
      error: (error) => {
        console.error(error);
        this.push('assistant', '\n\n⚠️ Грешка при стрийм отговора.');
        this.loading = false;
        this.scrollToBottomSoon();
      }
      // complete: () => {
      //   this.loading = false;
      //   this.scrollToBottomSoon();
      // }
    });
  }

  onEnter(buttonPress: KeyboardEvent) {
    if (buttonPress.key === 'Enter' && !buttonPress.shiftKey) {
      buttonPress.preventDefault();
      this.onSend();
    }
  }

  private push(role: Role, text: string) {
    this.messages.push({ id: uid(), role, text, timeStamp: Date.now() });
  }


  private scrollToBottomSoon() {
    setTimeout(() => {
      this.bottomRef?.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }, 0);
  }
}
