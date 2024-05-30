import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITeamSuitNba, NewTeamSuitNba } from '../team-suit-nba.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeamSuitNba for edit and NewTeamSuitNbaFormGroupInput for create.
 */
type TeamSuitNbaFormGroupInput = ITeamSuitNba | PartialWithRequiredKeyOf<NewTeamSuitNba>;

type TeamSuitNbaFormDefaults = Pick<NewTeamSuitNba, 'id'>;

type TeamSuitNbaFormGroupContent = {
  id: FormControl<ITeamSuitNba['id'] | NewTeamSuitNba['id']>;
  teamName: FormControl<ITeamSuitNba['teamName']>;
};

export type TeamSuitNbaFormGroup = FormGroup<TeamSuitNbaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamSuitNbaFormService {
  createTeamSuitNbaFormGroup(team: TeamSuitNbaFormGroupInput = { id: null }): TeamSuitNbaFormGroup {
    const teamRawValue = {
      ...this.getFormDefaults(),
      ...team,
    };
    return new FormGroup<TeamSuitNbaFormGroupContent>({
      id: new FormControl(
        { value: teamRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      teamName: new FormControl(teamRawValue.teamName),
    });
  }

  getTeamSuitNba(form: TeamSuitNbaFormGroup): ITeamSuitNba | NewTeamSuitNba {
    return form.getRawValue() as ITeamSuitNba | NewTeamSuitNba;
  }

  resetForm(form: TeamSuitNbaFormGroup, team: TeamSuitNbaFormGroupInput): void {
    const teamRawValue = { ...this.getFormDefaults(), ...team };
    form.reset(
      {
        ...teamRawValue,
        id: { value: teamRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TeamSuitNbaFormDefaults {
    return {
      id: null,
    };
  }
}
