import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITeamInGameSuitNba, NewTeamInGameSuitNba } from '../team-in-game-suit-nba.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeamInGameSuitNba for edit and NewTeamInGameSuitNbaFormGroupInput for create.
 */
type TeamInGameSuitNbaFormGroupInput = ITeamInGameSuitNba | PartialWithRequiredKeyOf<NewTeamInGameSuitNba>;

type TeamInGameSuitNbaFormDefaults = Pick<NewTeamInGameSuitNba, 'id'>;

type TeamInGameSuitNbaFormGroupContent = {
  id: FormControl<ITeamInGameSuitNba['id'] | NewTeamInGameSuitNba['id']>;
  team: FormControl<ITeamInGameSuitNba['team']>;
  game: FormControl<ITeamInGameSuitNba['game']>;
};

export type TeamInGameSuitNbaFormGroup = FormGroup<TeamInGameSuitNbaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamInGameSuitNbaFormService {
  createTeamInGameSuitNbaFormGroup(teamInGame: TeamInGameSuitNbaFormGroupInput = { id: null }): TeamInGameSuitNbaFormGroup {
    const teamInGameRawValue = {
      ...this.getFormDefaults(),
      ...teamInGame,
    };
    return new FormGroup<TeamInGameSuitNbaFormGroupContent>({
      id: new FormControl(
        { value: teamInGameRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      team: new FormControl(teamInGameRawValue.team),
      game: new FormControl(teamInGameRawValue.game),
    });
  }

  getTeamInGameSuitNba(form: TeamInGameSuitNbaFormGroup): ITeamInGameSuitNba | NewTeamInGameSuitNba {
    return form.getRawValue() as ITeamInGameSuitNba | NewTeamInGameSuitNba;
  }

  resetForm(form: TeamInGameSuitNbaFormGroup, teamInGame: TeamInGameSuitNbaFormGroupInput): void {
    const teamInGameRawValue = { ...this.getFormDefaults(), ...teamInGame };
    form.reset(
      {
        ...teamInGameRawValue,
        id: { value: teamInGameRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TeamInGameSuitNbaFormDefaults {
    return {
      id: null,
    };
  }
}
