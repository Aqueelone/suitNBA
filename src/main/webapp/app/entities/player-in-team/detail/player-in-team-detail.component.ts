import { Component, inject, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IPlayerInTeam } from '../player-in-team.model';
import { ITeamInSeasonSuitNba } from 'app/entities/team-in-season-suit-nba/team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from 'app/entities/team-in-season-suit-nba/service/team-in-season-suit-nba.service';
import { map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'jhi-player-in-team-detail',
  templateUrl: './player-in-team-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PlayerInTeamDetailComponent {
  playerInTeam = input<IPlayerInTeam | null>(null);

  teamInSeason: ITeamInSeasonSuitNba | undefined | null;
  teamInSeasonsSharedCollection: ITeamInSeasonSuitNba[] = [];

  protected teamInSeasonService = inject(TeamInSeasonSuitNbaService);

  ngOnInit(): void {
    this.teamInSeasonService
      .query()
      .pipe(map((res: HttpResponse<ITeamInSeasonSuitNba[]>) => res.body ?? []))
      .pipe(
        map((teamInSeasons: ITeamInSeasonSuitNba[]) =>
          this.teamInSeasonService.addTeamInSeasonSuitNbaToCollectionIfMissing<ITeamInSeasonSuitNba>(teamInSeasons, this.teamInSeason),
        ),
      )
      .subscribe((teamInSeasons: ITeamInSeasonSuitNba[]) => (this.teamInSeasonsSharedCollection = teamInSeasons));
  }

  previousState(): void {
    window.history.back();
  }
}
