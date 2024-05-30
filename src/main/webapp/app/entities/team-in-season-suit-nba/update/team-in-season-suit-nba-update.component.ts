import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITeamSuitNba } from 'app/entities/team-suit-nba/team-suit-nba.model';
import { TeamSuitNbaService } from 'app/entities/team-suit-nba/service/team-suit-nba.service';
import { ISeasonSuitNba } from 'app/entities/season-suit-nba/season-suit-nba.model';
import { SeasonSuitNbaService } from 'app/entities/season-suit-nba/service/season-suit-nba.service';
import { TeamInSeasonSuitNbaService } from '../service/team-in-season-suit-nba.service';
import { ITeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaFormService, TeamInSeasonSuitNbaFormGroup } from './team-in-season-suit-nba-form.service';

@Component({
  standalone: true,
  selector: 'jhi-team-in-season-suit-nba-update',
  templateUrl: './team-in-season-suit-nba-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeamInSeasonSuitNbaUpdateComponent implements OnInit {
  isSaving = false;
  teamInSeason: ITeamInSeasonSuitNba | null = null;

  teamsSharedCollection: ITeamSuitNba[] = [];
  seasonsSharedCollection: ISeasonSuitNba[] = [];

  protected teamInSeasonService = inject(TeamInSeasonSuitNbaService);
  protected teamInSeasonFormService = inject(TeamInSeasonSuitNbaFormService);
  protected teamService = inject(TeamSuitNbaService);
  protected seasonService = inject(SeasonSuitNbaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TeamInSeasonSuitNbaFormGroup = this.teamInSeasonFormService.createTeamInSeasonSuitNbaFormGroup();

  compareTeamSuitNba = (o1: ITeamSuitNba | null, o2: ITeamSuitNba | null): boolean => this.teamService.compareTeamSuitNba(o1, o2);

  compareSeasonSuitNba = (o1: ISeasonSuitNba | null, o2: ISeasonSuitNba | null): boolean => this.seasonService.compareSeasonSuitNba(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamInSeason }) => {
      this.teamInSeason = teamInSeason;
      if (teamInSeason) {
        this.updateForm(teamInSeason);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamInSeason = this.teamInSeasonFormService.getTeamInSeasonSuitNba(this.editForm);
    if (teamInSeason.id !== null) {
      this.subscribeToSaveResponse(this.teamInSeasonService.update(teamInSeason));
    } else {
      this.subscribeToSaveResponse(this.teamInSeasonService.create(teamInSeason));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamInSeasonSuitNba>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(teamInSeason: ITeamInSeasonSuitNba): void {
    this.teamInSeason = teamInSeason;
    this.teamInSeasonFormService.resetForm(this.editForm, teamInSeason);

    this.teamsSharedCollection = this.teamService.addTeamSuitNbaToCollectionIfMissing<ITeamSuitNba>(
      this.teamsSharedCollection,
      teamInSeason.team,
    );
    this.seasonsSharedCollection = this.seasonService.addSeasonSuitNbaToCollectionIfMissing<ISeasonSuitNba>(
      this.seasonsSharedCollection,
      teamInSeason.season,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeamSuitNba[]>) => res.body ?? []))
      .pipe(
        map((teams: ITeamSuitNba[]) => this.teamService.addTeamSuitNbaToCollectionIfMissing<ITeamSuitNba>(teams, this.teamInSeason?.team)),
      )
      .subscribe((teams: ITeamSuitNba[]) => (this.teamsSharedCollection = teams));

    this.seasonService
      .query()
      .pipe(map((res: HttpResponse<ISeasonSuitNba[]>) => res.body ?? []))
      .pipe(
        map((seasons: ISeasonSuitNba[]) =>
          this.seasonService.addSeasonSuitNbaToCollectionIfMissing<ISeasonSuitNba>(seasons, this.teamInSeason?.season),
        ),
      )
      .subscribe((seasons: ISeasonSuitNba[]) => (this.seasonsSharedCollection = seasons));
  }
}
