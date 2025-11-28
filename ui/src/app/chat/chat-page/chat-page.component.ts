import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ChatService } from '../chat.service';
import { ConversationService, Conversation, ConversationDetail } from '../conversation.service';
import {Message, Role} from '../../models/message/message.model';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MarkdownComponent } from 'ngx-markdown';
import { FormsModule } from '@angular/forms';
import { DatePipe, NgIf, NgFor } from '@angular/common';
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
    MatTooltipModule,
    DatePipe,
    RouterLink,
    NgIf,
    NgFor
  ],
  templateUrl: './chat-page.component.html',
  styleUrl: './chat-page.component.scss'
})
export class ChatPageComponent implements OnInit {
  messages: Message[] = [];
  input = '';
  loading = false;
  currentConversationId: number | null = null;
  conversations: Conversation[] = [];
  conversationsLoading = false;
  sidebarOpen = true;

  @ViewChild('bottom') bottomRef?: ElementRef<HTMLDivElement>;

  constructor(
    private chatService: ChatService,
    private conversationService: ConversationService
  ) {}

  ngOnInit() {
    this.loadConversations();
    this.startNewConversation();
  }

  trackById = (_: number, m: Message) => m.id;

  startNewConversation() {
    this.conversationService.createConversation().subscribe({
      next: (conversation) => {
        this.currentConversationId = conversation.id;
        this.messages = [
          {
            id: uid(),
            role: 'assistant',
            text: 'Здрасти! Питай ме нещо за ТУ-София. Ще добавя източници в края на отговора.',
            timeStamp: Date.now()
          }
        ];
        this.loadConversations();
      },
      error: (error) => {
        console.error('Error creating conversation:', error);
      }
    });
  }

  loadConversations() {
    this.conversationsLoading = true;
    this.conversationService.getUserConversations().subscribe({
      next: (conversations) => {
        this.conversations = conversations;
        this.conversationsLoading = false;
      },
      error: (error) => {
        console.error('Error loading conversations:', error);
        this.conversationsLoading = false;
      }
    });
  }

  loadConversation(conversationId: number) {
    this.conversationService.getConversation(conversationId).subscribe({
      next: (conversationDetail) => {
        this.currentConversationId = conversationDetail.id;
        this.messages = conversationDetail.messages.map(msg => ({
          id: msg.id.toString(),
          role: msg.role,
          text: msg.content,
          timeStamp: new Date(msg.createdAt).getTime()
        }));
        this.loadConversations();
        this.scrollToBottomSoon();
      },
      error: (error) => {
        console.error('Error loading conversation:', error);
      }
    });
  }

  onSend(): void {
    const question = this.input.trim();

    if (!question || this.loading || !this.currentConversationId)
      return;

    this.messages.push({ id: uid(), role: 'user', text: question, timeStamp: Date.now() });
    this.input = '';
    this.loading = true;

    this.scrollToBottomSoon();

    this.chatService.send(question, this.currentConversationId).subscribe({
      next: (answerStream) => {
        this.push('assistant', answerStream);
        this.loading = false;
        this.scrollToBottomSoon();
        this.loadConversations();
      },
      error: (error) => {
        console.error(error);
        this.push('assistant', '\n\n⚠️ Грешка при стрийм отговора.');
        this.loading = false;
        this.scrollToBottomSoon();
      }
    });
  }

  onEnter(buttonPress: KeyboardEvent) {
    if (buttonPress.key === 'Enter' && !buttonPress.shiftKey) {
      buttonPress.preventDefault();
      this.onSend();
    }
  }

  onInput(event: Event) {
    const textarea = event.target as HTMLTextAreaElement;
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 200) + 'px';
  }

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  deleteConversation(conversationId: number, event: Event) {
    event.stopPropagation();
    if (confirm('Сигурни ли сте, че искате да изтриете този разговор?')) {
      this.conversationService.deleteConversation(conversationId).subscribe({
        next: () => {
          this.loadConversations();
          if (this.currentConversationId === conversationId) {
            this.startNewConversation();
          }
        },
        error: (error) => {
          console.error('Error deleting conversation:', error);
        }
      });
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
