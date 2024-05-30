import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';

@Component({
  standalone: true,
  selector: 'jhi-player-in-game-suit-nba-detail',
  templateUrl: './player-in-game-suit-nba-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PlayerInGameSuitNbaDetailComponent {
  playerInGame = input<IPlayerInGameSuitNba | null>(null);

  previousState(): void {
    window.history.back();
  }
}
