import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITeamInGameSuitNba } from 'app/entities/team-in-game-suit-nba/team-in-game-suit-nba.model';
import { TeamInGameSuitNbaService } from 'app/entities/team-in-game-suit-nba/service/team-in-game-suit-nba.service';
import { IPlayerSuitNba } from 'app/entities/player-suit-nba/player-suit-nba.model';
import { PlayerSuitNbaService } from 'app/entities/player-suit-nba/service/player-suit-nba.service';
import { IGameSuitNba } from 'app/entities/game-suit-nba/game-suit-nba.model';
import { GameSuitNbaService } from 'app/entities/game-suit-nba/service/game-suit-nba.service';
import { PlayerInGameSuitNbaService } from '../service/player-in-game-suit-nba.service';
import { IPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';
import { PlayerInGameSuitNbaFormService, PlayerInGameSuitNbaFormGroup } from './player-in-game-suit-nba-form.service';

@Component({
  standalone: true,
  selector: 'jhi-player-in-game-suit-nba-update',
  templateUrl: './player-in-game-suit-nba-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PlayerInGameSuitNbaUpdateComponent implements OnInit {
  isSaving = false;
  playerInGame: IPlayerInGameSuitNba | null = null;

  teamInGamesSharedCollection: ITeamInGameSuitNba[] = [];
  playersSharedCollection: IPlayerSuitNba[] = [];
  gamesSharedCollection: IGameSuitNba[] = [];

  protected playerInGameService = inject(PlayerInGameSuitNbaService);
  protected playerInGameFormService = inject(PlayerInGameSuitNbaFormService);
  protected teamInGameService = inject(TeamInGameSuitNbaService);
  protected playerService = inject(PlayerSuitNbaService);
  protected gameService = inject(GameSuitNbaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PlayerInGameSuitNbaFormGroup = this.playerInGameFormService.createPlayerInGameSuitNbaFormGroup();

  compareTeamInGameSuitNba = (o1: ITeamInGameSuitNba | null, o2: ITeamInGameSuitNba | null): boolean =>
    this.teamInGameService.compareTeamInGameSuitNba(o1, o2);

  comparePlayerSuitNba = (o1: IPlayerSuitNba | null, o2: IPlayerSuitNba | null): boolean => this.playerService.comparePlayerSuitNba(o1, o2);

  compareGameSuitNba = (o1: IGameSuitNba | null, o2: IGameSuitNba | null): boolean => this.gameService.compareGameSuitNba(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ playerInGame }) => {
      this.playerInGame = playerInGame;
      if (playerInGame) {
        this.updateForm(playerInGame);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const playerInGame = this.playerInGameFormService.getPlayerInGameSuitNba(this.editForm);
    if (playerInGame.id !== null) {
      this.subscribeToSaveResponse(this.playerInGameService.update(playerInGame));
    } else {
      this.subscribeToSaveResponse(this.playerInGameService.create(playerInGame));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlayerInGameSuitNba>>): void {
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

  protected updateForm(playerInGame: IPlayerInGameSuitNba): void {
    this.playerInGame = playerInGame;
    this.playerInGameFormService.resetForm(this.editForm, playerInGame);

    this.teamInGamesSharedCollection = this.teamInGameService.addTeamInGameSuitNbaToCollectionIfMissing<ITeamInGameSuitNba>(
      this.teamInGamesSharedCollection,
      playerInGame.team,
    );
    this.playersSharedCollection = this.playerService.addPlayerSuitNbaToCollectionIfMissing<IPlayerSuitNba>(
      this.playersSharedCollection,
      playerInGame.player,
    );
    this.gamesSharedCollection = this.gameService.addGameSuitNbaToCollectionIfMissing<IGameSuitNba>(
      this.gamesSharedCollection,
      playerInGame.game,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teamInGameService
      .query()
      .pipe(map((res: HttpResponse<ITeamInGameSuitNba[]>) => res.body ?? []))
      .pipe(
        map((teamInGames: ITeamInGameSuitNba[]) =>
          this.teamInGameService.addTeamInGameSuitNbaToCollectionIfMissing<ITeamInGameSuitNba>(teamInGames, this.playerInGame?.team),
        ),
      )
      .subscribe((teamInGames: ITeamInGameSuitNba[]) => (this.teamInGamesSharedCollection = teamInGames));

    this.playerService
      .query()
      .pipe(map((res: HttpResponse<IPlayerSuitNba[]>) => res.body ?? []))
      .pipe(
        map((players: IPlayerSuitNba[]) =>
          this.playerService.addPlayerSuitNbaToCollectionIfMissing<IPlayerSuitNba>(players, this.playerInGame?.player),
        ),
      )
      .subscribe((players: IPlayerSuitNba[]) => (this.playersSharedCollection = players));

    this.gameService
      .query()
      .pipe(map((res: HttpResponse<IGameSuitNba[]>) => res.body ?? []))
      .pipe(
        map((games: IGameSuitNba[]) => this.gameService.addGameSuitNbaToCollectionIfMissing<IGameSuitNba>(games, this.playerInGame?.game)),
      )
      .subscribe((games: IGameSuitNba[]) => (this.gamesSharedCollection = games));
  }
}
