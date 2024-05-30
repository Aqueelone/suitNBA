import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ITeamSuitNba } from '../team-suit-nba.model';

@Component({
  standalone: true,
  selector: 'jhi-team-suit-nba-detail',
  templateUrl: './team-suit-nba-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TeamSuitNbaDetailComponent {
  team = input<ITeamSuitNba | null>(null);

  previousState(): void {
    window.history.back();
  }
}
