import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITeamSuitNba } from '../team-suit-nba.model';
import { TeamSuitNbaService } from '../service/team-suit-nba.service';
import { TeamSuitNbaFormService, TeamSuitNbaFormGroup } from './team-suit-nba-form.service';

@Component({
  standalone: true,
  selector: 'jhi-team-suit-nba-update',
  templateUrl: './team-suit-nba-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeamSuitNbaUpdateComponent implements OnInit {
  isSaving = false;
  team: ITeamSuitNba | null = null;

  protected teamService = inject(TeamSuitNbaService);
  protected teamFormService = inject(TeamSuitNbaFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TeamSuitNbaFormGroup = this.teamFormService.createTeamSuitNbaFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ team }) => {
      this.team = team;
      if (team) {
        this.updateForm(team);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const team = this.teamFormService.getTeamSuitNba(this.editForm);
    if (team.id !== null) {
      this.subscribeToSaveResponse(this.teamService.update(team));
    } else {
      this.subscribeToSaveResponse(this.teamService.create(team));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamSuitNba>>): void {
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

  protected updateForm(team: ITeamSuitNba): void {
    this.team = team;
    this.teamFormService.resetForm(this.editForm, team);
  }
}
