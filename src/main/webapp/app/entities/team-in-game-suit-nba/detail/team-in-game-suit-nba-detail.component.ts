import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ITeamInGameSuitNba } from '../team-in-game-suit-nba.model';

@Component({
  standalone: true,
  selector: 'jhi-team-in-game-suit-nba-detail',
  templateUrl: './team-in-game-suit-nba-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TeamInGameSuitNbaDetailComponent {
  teamInGame = input<ITeamInGameSuitNba | null>(null);

  previousState(): void {
    window.history.back();
  }
}
