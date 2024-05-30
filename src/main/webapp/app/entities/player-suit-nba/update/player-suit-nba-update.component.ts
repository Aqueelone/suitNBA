import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPlayerSuitNba } from '../player-suit-nba.model';
import { PlayerSuitNbaService } from '../service/player-suit-nba.service';
import { PlayerSuitNbaFormService, PlayerSuitNbaFormGroup } from './player-suit-nba-form.service';

@Component({
  standalone: true,
  selector: 'jhi-player-suit-nba-update',
  templateUrl: './player-suit-nba-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PlayerSuitNbaUpdateComponent implements OnInit {
  isSaving = false;
  player: IPlayerSuitNba | null = null;

  protected playerService = inject(PlayerSuitNbaService);
  protected playerFormService = inject(PlayerSuitNbaFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PlayerSuitNbaFormGroup = this.playerFormService.createPlayerSuitNbaFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ player }) => {
      this.player = player;
      if (player) {
        this.updateForm(player);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const player = this.playerFormService.getPlayerSuitNba(this.editForm);
    if (player.id !== null) {
      this.subscribeToSaveResponse(this.playerService.update(player));
    } else {
      this.subscribeToSaveResponse(this.playerService.create(player));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlayerSuitNba>>): void {
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

  protected updateForm(player: IPlayerSuitNba): void {
    this.player = player;
    this.playerFormService.resetForm(this.editForm, player);
  }
}
