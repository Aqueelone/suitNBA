import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISeasonSuitNba } from '../season-suit-nba.model';
import { SeasonSuitNbaService } from '../service/season-suit-nba.service';
import { SeasonSuitNbaFormService, SeasonSuitNbaFormGroup } from './season-suit-nba-form.service';

@Component({
  standalone: true,
  selector: 'jhi-season-suit-nba-update',
  templateUrl: './season-suit-nba-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SeasonSuitNbaUpdateComponent implements OnInit {
  isSaving = false;
  season: ISeasonSuitNba | null = null;

  protected seasonService = inject(SeasonSuitNbaService);
  protected seasonFormService = inject(SeasonSuitNbaFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SeasonSuitNbaFormGroup = this.seasonFormService.createSeasonSuitNbaFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ season }) => {
      this.season = season;
      if (season) {
        this.updateForm(season);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const season = this.seasonFormService.getSeasonSuitNba(this.editForm);
    if (season.id !== null) {
      this.subscribeToSaveResponse(this.seasonService.update(season));
    } else {
      this.subscribeToSaveResponse(this.seasonService.create(season));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISeasonSuitNba>>): void {
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

  protected updateForm(season: ISeasonSuitNba): void {
    this.season = season;
    this.seasonFormService.resetForm(this.editForm, season);
  }
}
