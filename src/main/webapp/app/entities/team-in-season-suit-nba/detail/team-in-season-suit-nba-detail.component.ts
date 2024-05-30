import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ITeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';

@Component({
  standalone: true,
  selector: 'jhi-team-in-season-suit-nba-detail',
  templateUrl: './team-in-season-suit-nba-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TeamInSeasonSuitNbaDetailComponent {
  teamInSeason = input<ITeamInSeasonSuitNba | null>(null);

  previousState(): void {
    window.history.back();
  }
}
