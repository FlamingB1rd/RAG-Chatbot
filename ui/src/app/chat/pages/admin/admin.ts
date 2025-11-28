import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DatePipe, NgIf, NgFor, AsyncPipe } from '@angular/common';
import { AdminService, UrlContent, UserInfo, AuditLogInfo, ScheduledUrlInfo } from '../admin.service';

@Component({
  selector: 'app-admin',
  imports: [
    FormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule,
    DatePipe,
    NgIf,
    NgFor,
    AsyncPipe
  ],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class AdminComponent implements OnInit {
  constructor(private adminService: AdminService) {
  }

  // Add URL
  addUrl = '';
  addResult = '';
  addError = '';
  addLoading = false;

  // URLs List
  urls: string[] = [];
  urlsLoading = false;

  // Selected URL & Content
  selectedUrl: string | null = null;
  urlContent: UrlContent | null = null;
  contentLoading = false;
  contentError = '';

  // Delete
  deleteLoading = false;

  // Config
  topKOptions = [3, 5, 7];
  similarityOptions = [0.65, 0.7, 0.75, 0.8];
  selectedTopK = 5;
  selectedSimilarity = 0.65;
  cronExpression = '0 0 9 ? * FRI'; // Default: Every Friday at 9:00 AM
  configLoading = false;
  configResult = '';

  // Scheduled URLs
  scheduledUrls: ScheduledUrlInfo[] = [];
  scheduledUrlsLoading = false;
  newScheduledUrl = '';
  newScheduledDescription = '';
  scheduledUrlAdding = false;

  // Upgrade
  upgradeLoading = false;
  upgradeResult = '';

  // Users Management
  users: UserInfo[] = [];
  usersLoading = false;
  usersColumns = ['username', 'email', 'roles', 'actions'];
  roleUpdating: number | null = null;
  userDeleting: number | null = null;
  availableRoles: string[] = [];
  rolesLoading = false;
  userRoleChanges: { [userId: number]: string } = {};

  // Audit Logs
  auditLogs: AuditLogInfo[] = [];
  logsLoading = false;

  ngOnInit() {
    this.loadUrls();
    this.loadConfig();
    this.loadUsers();
    this.loadAuditLogs();
    this.loadRoles();
    this.loadScheduledUrls();
  }

  onAddContext() {
    const url = this.addUrl.trim();
    if (!url) return;

    this.addLoading = true;
    this.addResult = '';
    this.addError = '';

    this.adminService.addContextFromUrl(url).subscribe({
      next: () => {
        this.addResult = 'Успешно добавено!';
        this.addUrl = '';
        this.addLoading = false;
        this.loadUrls();
      },
      error: (err) => {
        this.addError = 'Грешка при добавяне на контекст: ' + (err?.message ?? 'Неизвестна грешка');
        this.addLoading = false;
      }
    });
  }

  loadUrls() {
    this.urlsLoading = true;
    this.adminService.getAllUrls().subscribe({
      next: (urls) => {
        this.urls = urls;
        this.urlsLoading = false;
      },
      error: (err) => {
        console.error('Error loading URLs:', err);
        this.urlsLoading = false;
      }
    });
  }

  selectUrl(url: string) {
    if (this.selectedUrl === url) {
      this.closeSidebar();
      return;
    }
    this.selectedUrl = url;
    this.urlContent = null;
    this.contentError = '';
    this.loadUrlContent(url);
  }

  loadUrlContent(url: string) {
    this.contentLoading = true;
    this.contentError = '';
    this.adminService.getContentByUrl(url).subscribe({
      next: (content) => {
        this.urlContent = content;
        this.contentLoading = false;
      },
      error: (err) => {
        this.contentError = 'Грешка при зареждане на съдържанието: ' + (err?.message ?? '');
        this.contentLoading = false;
      }
    });
  }

  closeSidebar() {
    this.selectedUrl = null;
    this.urlContent = null;
    this.contentError = '';
  }

  onDeleteUrl() {
    if (!this.selectedUrl) return;

    if (!confirm(`Сигурни ли сте, че искате да изтриете контекста за ${this.selectedUrl}?`)) {
      return;
    }

    this.deleteLoading = true;
    this.adminService.deleteByUrl(this.selectedUrl).subscribe({
      next: () => {
        this.deleteLoading = false;
        this.closeSidebar();
        this.loadUrls();
        this.loadAuditLogs(); // Refresh audit logs
      },
      error: (err) => {
        this.contentError = 'Грешка при изтриване: ' + (err?.message ?? '');
        this.deleteLoading = false;
      }
    });
  }

  loadConfig() {
    this.adminService.getConfig().subscribe({
      next: (config) => {
        this.selectedTopK = config.topK;
        this.selectedSimilarity = config.similarityThreshold;
        if (config.cronExpression) {
          this.cronExpression = config.cronExpression;
        }
      },
      error: (err) => {
        console.error('Error loading config:', err);
      }
    });
  }

  onSaveConfig() {
    this.configLoading = true;
    this.configResult = '';
    this.adminService.updateConfig(this.selectedTopK, this.selectedSimilarity, this.cronExpression).subscribe({
      next: () => {
        this.configResult = 'Настройките са запазени успешно!';
        this.configLoading = false;
        this.loadAuditLogs(); // Refresh audit logs
        setTimeout(() => this.configResult = '', 3000);
      },
      error: (err) => {
        this.configResult = 'Грешка при запазване: ' + (err?.message ?? '');
        this.configLoading = false;
      }
    });
  }

  loadScheduledUrls() {
    this.scheduledUrlsLoading = true;
    this.adminService.getAllScheduledUrls().subscribe({
      next: (urls) => {
        this.scheduledUrls = urls;
        this.scheduledUrlsLoading = false;
      },
      error: (err) => {
        console.error('Error loading scheduled URLs:', err);
        this.scheduledUrlsLoading = false;
      }
    });
  }

  onAddScheduledUrl() {
    const url = this.newScheduledUrl.trim();
    if (!url) return;

    this.scheduledUrlAdding = true;
    this.adminService.createScheduledUrl(url, this.newScheduledDescription.trim() || undefined).subscribe({
      next: () => {
        this.scheduledUrlAdding = false;
        this.newScheduledUrl = '';
        this.newScheduledDescription = '';
        this.loadScheduledUrls();
        this.loadAuditLogs();
      },
      error: (err) => {
        console.error('Error adding scheduled URL:', err);
        alert('Грешка при добавяне: ' + (err?.message ?? ''));
        this.scheduledUrlAdding = false;
      }
    });
  }

  onDeleteScheduledUrl(id: number, url: string) {
    if (!confirm(`Сигурни ли сте, че искате да изтриете планирания URL "${url}"?`)) {
      return;
    }

    this.adminService.deleteScheduledUrl(id).subscribe({
      next: () => {
        this.loadScheduledUrls();
        this.loadAuditLogs();
      },
      error: (err) => {
        console.error('Error deleting scheduled URL:', err);
        alert('Грешка при изтриване: ' + (err?.message ?? ''));
      }
    });
  }

  onToggleScheduledUrl(id: number) {
    this.adminService.toggleScheduledUrl(id).subscribe({
      next: () => {
        this.loadScheduledUrls();
        this.loadAuditLogs();
      },
      error: (err) => {
        console.error('Error toggling scheduled URL:', err);
        alert('Грешка при промяна: ' + (err?.message ?? ''));
      }
    });
  }

  onUpgrade() {
    if (!confirm('Сигурни ли сте, че искате да обновите базата данни? Това може да отнеме време.')) {
      return;
    }

    this.upgradeLoading = true;
    this.upgradeResult = '';
    this.adminService.upgrade().subscribe({
      next: (result) => {
        this.upgradeResult = result;
        this.upgradeLoading = false;
      },
      error: (err) => {
        this.upgradeResult = 'Грешка при обновяване: ' + (err?.message ?? '');
        this.upgradeLoading = false;
      }
    });
  }

  loadUsers() {
    this.usersLoading = true;
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.usersLoading = false;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.usersLoading = false;
      }
    });
  }

  onRoleChange(userId: number, newRole: string, currentRoles: string[]) {
    const currentRole = currentRoles.includes('ROLE_ADMIN') ? 'ROLE_ADMIN' : 'ROLE_USER';
    if (newRole === currentRole) {
      return;
    }

    if (!confirm(`Сигурни ли сте, че искате да промените ролята на този потребител на ${this.getRoleDisplayName([newRole])}?`)) {
      this.userRoleChanges[userId] = currentRole;
      return;
    }

    this.roleUpdating = userId;
    this.adminService.updateUserRole(userId, newRole).subscribe({
      next: () => {
        this.roleUpdating = null;
        delete this.userRoleChanges[userId];
        this.loadUsers();
        this.loadAuditLogs();
      },
      error: (err) => {
        console.error('Error updating user role:', err);
        alert('Грешка при промяна на ролята: ' + (err?.message ?? ''));
        this.roleUpdating = null;
        this.userRoleChanges[userId] = currentRole;
      }
    });
  }

  getCurrentRole(roles: string[]): string {
    return roles.includes('ROLE_ADMIN') ? 'ROLE_ADMIN' : 'ROLE_USER';
  }

  getUserRole(userId: number, currentRoles: string[]): string {
    return this.userRoleChanges[userId] ?? this.getCurrentRole(currentRoles);
  }

  deleteUser(userId: number, username: string) {
    if (!confirm(`Сигурни ли сте, че искате да изтриете потребителя "${username}"? Това действие е необратимо!`)) {
      return;
    }

    this.userDeleting = userId;
    this.adminService.deleteUser(userId).subscribe({
      next: () => {
        this.userDeleting = null;
        this.loadUsers();
        this.loadAuditLogs();
      },
      error: (err) => {
        console.error('Error deleting user:', err);
        alert('Грешка при изтриване на потребителя: ' + (err?.message ?? ''));
        this.userDeleting = null;
      }
    });
  }

  getRoleDisplayName(roles: string[]): string {
    if (roles.includes('ROLE_ADMIN')) {
      return 'Администратор';
    }
    return 'Потребител';
  }

  loadAuditLogs() {
    this.logsLoading = true;
    this.adminService.getAuditLogs().subscribe({
      next: (logs) => {
        this.auditLogs = logs;
        this.logsLoading = false;
      },
      error: (err) => {
        console.error('Error loading audit logs:', err);
        this.logsLoading = false;
      }
    });
  }

  loadRoles() {
    this.rolesLoading = true;
    this.adminService.getAllRoles().subscribe({
      next: (roles) => {
        this.availableRoles = roles;
        this.rolesLoading = false;
      },
      error: (err) => {
        console.error('Error loading roles:', err);
        this.availableRoles = ['ROLE_USER', 'ROLE_ADMIN'];
        this.rolesLoading = false;
      }
    });
  }

  getActionDisplayName(actionType: string): string {
    const actionMap: { [key: string]: string } = {
      'CONFIG_UPDATE': 'Обновяване на настройки',
      'USER_ROLE_CHANGE': 'Промяна на роля',
      'USER_DELETE': 'Изтриване на потребител',
      'FAQ_CREATE': 'Създаване на FAQ',
      'FAQ_DELETE': 'Изтриване на FAQ',
      'URL_DELETE': 'Изтриване на URL'
    };
    return actionMap[actionType] || actionType;
  }

  getEntityDisplayName(entityType: string): string {
    const entityMap: { [key: string]: string } = {
      'CONFIG': 'Настройки',
      'USER': 'Потребител',
      'FAQ': 'FAQ',
      'URL': 'URL'
    };
    return entityMap[entityType] || entityType;
  }
}
