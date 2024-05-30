import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IGameSuitNba } from '../game-suit-nba.model';

@Component({
  standalone: true,
  selector: 'jhi-game-suit-nba-detail',
  templateUrl: './game-suit-nba-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class GameSuitNbaDetailComponent {
  game = input<IGameSuitNba | null>(null);

  previousState(): void {
    window.history.back();
  }
}
