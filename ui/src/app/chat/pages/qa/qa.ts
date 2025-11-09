import { Component } from '@angular/core';
import {NgFor} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-qa',
  imports: [NgFor, MatCardModule, MatToolbarModule, MatIconModule, RouterLink],
  templateUrl: './qa.html',
  styleUrl: './qa.css'
})
export class QaComponent {
  faqs = [
    {
      q: 'Какви са семестриалните такси за 2025/2026?',
      a: 'Зависи от специалността и формата на обучение. Виж раздел „Обучение“ на сайта на ТУ-София.'
    },
    {
      q: 'Как да кандидатствам?',
      a: 'Онлайн през сайта или на място в приемната комисия.'
    }
  ];
}
