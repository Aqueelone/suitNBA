import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPlayerSuitNba } from 'app/entities/player-suit-nba/player-suit-nba.model';
import { PlayerSuitNbaService } from 'app/entities/player-suit-nba/service/player-suit-nba.service';
import { ITeamInSeasonSuitNba } from 'app/entities/team-in-season-suit-nba/team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from 'app/entities/team-in-season-suit-nba/service/team-in-season-suit-nba.service';
import { PlayerInTeamService } from '../service/player-in-team.service';
import { IPlayerInTeam } from '../player-in-team.model';
import { PlayerInTeamFormService, PlayerInTeamFormGroup } from './player-in-team-form.service';

@Component({
  standalone: true,
  selector: 'jhi-player-in-team-update',
  templateUrl: './player-in-team-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PlayerInTeamUpdateComponent implements OnInit {
  isSaving = false;
  playerInTeam: IPlayerInTeam | null = null;

  playersSharedCollection: IPlayerSuitNba[] = [];
  teamInSeasonsSharedCollection: ITeamInSeasonSuitNba[] = [];

  protected playerInTeamService = inject(PlayerInTeamService);
  protected playerInTeamFormService = inject(PlayerInTeamFormService);
  protected playerService = inject(PlayerSuitNbaService);
  protected teamInSeasonService = inject(TeamInSeasonSuitNbaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PlayerInTeamFormGroup = this.playerInTeamFormService.createPlayerInTeamFormGroup();

  comparePlayerSuitNba = (o1: IPlayerSuitNba | null, o2: IPlayerSuitNba | null): boolean => this.playerService.comparePlayerSuitNba(o1, o2);

  compareTeamInSeasonSuitNba = (o1: ITeamInSeasonSuitNba | null, o2: ITeamInSeasonSuitNba | null): boolean =>
    this.teamInSeasonService.compareTeamInSeasonSuitNba(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ playerInTeam }) => {
      this.playerInTeam = playerInTeam;
      if (playerInTeam) {
        this.updateForm(playerInTeam);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const playerInTeam = this.playerInTeamFormService.getPlayerInTeam(this.editForm);
    if (playerInTeam.id !== null) {
      this.subscribeToSaveResponse(this.playerInTeamService.update(playerInTeam));
    } else {
      this.subscribeToSaveResponse(this.playerInTeamService.create(playerInTeam));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlayerInTeam>>): void {
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

  protected updateForm(playerInTeam: IPlayerInTeam): void {
    this.playerInTeam = playerInTeam;
    this.playerInTeamFormService.resetForm(this.editForm, playerInTeam);

    this.playersSharedCollection = this.playerService.addPlayerSuitNbaToCollectionIfMissing<IPlayerSuitNba>(
      this.playersSharedCollection,
      playerInTeam.player,
    );
    this.teamInSeasonsSharedCollection = this.teamInSeasonService.addTeamInSeasonSuitNbaToCollectionIfMissing<ITeamInSeasonSuitNba>(
      this.teamInSeasonsSharedCollection,
      playerInTeam.teamInSeason,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.playerService
      .query()
      .pipe(map((res: HttpResponse<IPlayerSuitNba[]>) => res.body ?? []))
      .pipe(
        map((players: IPlayerSuitNba[]) =>
          this.playerService.addPlayerSuitNbaToCollectionIfMissing<IPlayerSuitNba>(players, this.playerInTeam?.player),
        ),
      )
      .subscribe((players: IPlayerSuitNba[]) => (this.playersSharedCollection = players));

    this.teamInSeasonService
      .query()
      .pipe(map((res: HttpResponse<ITeamInSeasonSuitNba[]>) => res.body ?? []))
      .pipe(
        map((teamInSeasons: ITeamInSeasonSuitNba[]) =>
          this.teamInSeasonService.addTeamInSeasonSuitNbaToCollectionIfMissing<ITeamInSeasonSuitNba>(
            teamInSeasons,
            this.playerInTeam?.teamInSeason,
          ),
        ),
      )
      .subscribe((teamInSeasons: ITeamInSeasonSuitNba[]) => (this.teamInSeasonsSharedCollection = teamInSeasons));
  }
}
