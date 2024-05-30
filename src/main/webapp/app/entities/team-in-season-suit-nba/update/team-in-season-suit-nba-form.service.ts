import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITeamInSeasonSuitNba, NewTeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeamInSeasonSuitNba for edit and NewTeamInSeasonSuitNbaFormGroupInput for create.
 */
type TeamInSeasonSuitNbaFormGroupInput = ITeamInSeasonSuitNba | PartialWithRequiredKeyOf<NewTeamInSeasonSuitNba>;

type TeamInSeasonSuitNbaFormDefaults = Pick<NewTeamInSeasonSuitNba, 'id'>;

type TeamInSeasonSuitNbaFormGroupContent = {
  id: FormControl<ITeamInSeasonSuitNba['id'] | NewTeamInSeasonSuitNba['id']>;
  team: FormControl<ITeamInSeasonSuitNba['team']>;
  season: FormControl<ITeamInSeasonSuitNba['season']>;
};

export type TeamInSeasonSuitNbaFormGroup = FormGroup<TeamInSeasonSuitNbaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamInSeasonSuitNbaFormService {
  createTeamInSeasonSuitNbaFormGroup(teamInSeason: TeamInSeasonSuitNbaFormGroupInput = { id: null }): TeamInSeasonSuitNbaFormGroup {
    const teamInSeasonRawValue = {
      ...this.getFormDefaults(),
      ...teamInSeason,
    };
    return new FormGroup<TeamInSeasonSuitNbaFormGroupContent>({
      id: new FormControl(
        { value: teamInSeasonRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      team: new FormControl(teamInSeasonRawValue.team),
      season: new FormControl(teamInSeasonRawValue.season),
    });
  }

  getTeamInSeasonSuitNba(form: TeamInSeasonSuitNbaFormGroup): ITeamInSeasonSuitNba | NewTeamInSeasonSuitNba {
    return form.getRawValue() as ITeamInSeasonSuitNba | NewTeamInSeasonSuitNba;
  }

  resetForm(form: TeamInSeasonSuitNbaFormGroup, teamInSeason: TeamInSeasonSuitNbaFormGroupInput): void {
    const teamInSeasonRawValue = { ...this.getFormDefaults(), ...teamInSeason };
    form.reset(
      {
        ...teamInSeasonRawValue,
        id: { value: teamInSeasonRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TeamInSeasonSuitNbaFormDefaults {
    return {
      id: null,
    };
  }
}
