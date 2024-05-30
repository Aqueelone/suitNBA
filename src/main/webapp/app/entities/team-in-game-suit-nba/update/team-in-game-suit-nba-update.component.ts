import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITeamInSeasonSuitNba } from 'app/entities/team-in-season-suit-nba/team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from 'app/entities/team-in-season-suit-nba/service/team-in-season-suit-nba.service';
import { IGameSuitNba } from 'app/entities/game-suit-nba/game-suit-nba.model';
import { GameSuitNbaService } from 'app/entities/game-suit-nba/service/game-suit-nba.service';
import { TeamInGameSuitNbaService } from '../service/team-in-game-suit-nba.service';
import { ITeamInGameSuitNba } from '../team-in-game-suit-nba.model';
import { TeamInGameSuitNbaFormService, TeamInGameSuitNbaFormGroup } from './team-in-game-suit-nba-form.service';

@Component({
  standalone: true,
  selector: 'jhi-team-in-game-suit-nba-update',
  templateUrl: './team-in-game-suit-nba-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeamInGameSuitNbaUpdateComponent implements OnInit {
  isSaving = false;
  teamInGame: ITeamInGameSuitNba | null = null;

  teamInSeasonsSharedCollection: ITeamInSeasonSuitNba[] = [];
  gamesSharedCollection: IGameSuitNba[] = [];

  protected teamInGameService = inject(TeamInGameSuitNbaService);
  protected teamInGameFormService = inject(TeamInGameSuitNbaFormService);
  protected teamInSeasonService = inject(TeamInSeasonSuitNbaService);
  protected gameService = inject(GameSuitNbaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TeamInGameSuitNbaFormGroup = this.teamInGameFormService.createTeamInGameSuitNbaFormGroup();

  compareTeamInSeasonSuitNba = (o1: ITeamInSeasonSuitNba | null, o2: ITeamInSeasonSuitNba | null): boolean =>
    this.teamInSeasonService.compareTeamInSeasonSuitNba(o1, o2);

  compareGameSuitNba = (o1: IGameSuitNba | null, o2: IGameSuitNba | null): boolean => this.gameService.compareGameSuitNba(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamInGame }) => {
      this.teamInGame = teamInGame;
      if (teamInGame) {
        this.updateForm(teamInGame);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamInGame = this.teamInGameFormService.getTeamInGameSuitNba(this.editForm);
    if (teamInGame.id !== null) {
      this.subscribeToSaveResponse(this.teamInGameService.update(teamInGame));
    } else {
      this.subscribeToSaveResponse(this.teamInGameService.create(teamInGame));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamInGameSuitNba>>): void {
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

  protected updateForm(teamInGame: ITeamInGameSuitNba): void {
    this.teamInGame = teamInGame;
    this.teamInGameFormService.resetForm(this.editForm, teamInGame);

    this.teamInSeasonsSharedCollection = this.teamInSeasonService.addTeamInSeasonSuitNbaToCollectionIfMissing<ITeamInSeasonSuitNba>(
      this.teamInSeasonsSharedCollection,
      teamInGame.team,
    );
    this.gamesSharedCollection = this.gameService.addGameSuitNbaToCollectionIfMissing<IGameSuitNba>(
      this.gamesSharedCollection,
      teamInGame.game,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teamInSeasonService
      .query()
      .pipe(map((res: HttpResponse<ITeamInSeasonSuitNba[]>) => res.body ?? []))
      .pipe(
        map((teamInSeasons: ITeamInSeasonSuitNba[]) =>
          this.teamInSeasonService.addTeamInSeasonSuitNbaToCollectionIfMissing<ITeamInSeasonSuitNba>(teamInSeasons, this.teamInGame?.team),
        ),
      )
      .subscribe((teamInSeasons: ITeamInSeasonSuitNba[]) => (this.teamInSeasonsSharedCollection = teamInSeasons));

    this.gameService
      .query()
      .pipe(map((res: HttpResponse<IGameSuitNba[]>) => res.body ?? []))
      .pipe(
        map((games: IGameSuitNba[]) => this.gameService.addGameSuitNbaToCollectionIfMissing<IGameSuitNba>(games, this.teamInGame?.game)),
      )
      .subscribe((games: IGameSuitNba[]) => (this.gamesSharedCollection = games));
  }
}
