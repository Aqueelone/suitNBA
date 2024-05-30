import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISeasonSuitNba } from 'app/entities/season-suit-nba/season-suit-nba.model';
import { SeasonSuitNbaService } from 'app/entities/season-suit-nba/service/season-suit-nba.service';
import { IGameSuitNba } from '../game-suit-nba.model';
import { GameSuitNbaService } from '../service/game-suit-nba.service';
import { GameSuitNbaFormService, GameSuitNbaFormGroup } from './game-suit-nba-form.service';

@Component({
  standalone: true,
  selector: 'jhi-game-suit-nba-update',
  templateUrl: './game-suit-nba-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class GameSuitNbaUpdateComponent implements OnInit {
  isSaving = false;
  game: IGameSuitNba | null = null;

  seasonsSharedCollection: ISeasonSuitNba[] = [];

  protected gameService = inject(GameSuitNbaService);
  protected gameFormService = inject(GameSuitNbaFormService);
  protected seasonService = inject(SeasonSuitNbaService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GameSuitNbaFormGroup = this.gameFormService.createGameSuitNbaFormGroup();

  compareSeasonSuitNba = (o1: ISeasonSuitNba | null, o2: ISeasonSuitNba | null): boolean => this.seasonService.compareSeasonSuitNba(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ game }) => {
      this.game = game;
      if (game) {
        this.updateForm(game);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const game = this.gameFormService.getGameSuitNba(this.editForm);
    if (game.id !== null) {
      this.subscribeToSaveResponse(this.gameService.update(game));
    } else {
      this.subscribeToSaveResponse(this.gameService.create(game));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGameSuitNba>>): void {
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

  protected updateForm(game: IGameSuitNba): void {
    this.game = game;
    this.gameFormService.resetForm(this.editForm, game);

    this.seasonsSharedCollection = this.seasonService.addSeasonSuitNbaToCollectionIfMissing<ISeasonSuitNba>(
      this.seasonsSharedCollection,
      game.season,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.seasonService
      .query()
      .pipe(map((res: HttpResponse<ISeasonSuitNba[]>) => res.body ?? []))
      .pipe(
        map((seasons: ISeasonSuitNba[]) =>
          this.seasonService.addSeasonSuitNbaToCollectionIfMissing<ISeasonSuitNba>(seasons, this.game?.season),
        ),
      )
      .subscribe((seasons: ISeasonSuitNba[]) => (this.seasonsSharedCollection = seasons));
  }
}
