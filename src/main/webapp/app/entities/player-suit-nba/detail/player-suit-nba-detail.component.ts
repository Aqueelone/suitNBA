import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IPlayerSuitNba } from '../player-suit-nba.model';

@Component({
  standalone: true,
  selector: 'jhi-player-suit-nba-detail',
  templateUrl: './player-suit-nba-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PlayerSuitNbaDetailComponent {
  player = input<IPlayerSuitNba | null>(null);

  previousState(): void {
    window.history.back();
  }
}
