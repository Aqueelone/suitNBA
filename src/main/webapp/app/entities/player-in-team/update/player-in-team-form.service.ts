import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPlayerInTeam, NewPlayerInTeam } from '../player-in-team.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlayerInTeam for edit and NewPlayerInTeamFormGroupInput for create.
 */
type PlayerInTeamFormGroupInput = IPlayerInTeam | PartialWithRequiredKeyOf<NewPlayerInTeam>;

type PlayerInTeamFormDefaults = Pick<NewPlayerInTeam, 'id'>;

type PlayerInTeamFormGroupContent = {
  id: FormControl<IPlayerInTeam['id'] | NewPlayerInTeam['id']>;
  player: FormControl<IPlayerInTeam['player']>;
  teamInSeason: FormControl<IPlayerInTeam['teamInSeason']>;
};

export type PlayerInTeamFormGroup = FormGroup<PlayerInTeamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlayerInTeamFormService {
  createPlayerInTeamFormGroup(playerInTeam: PlayerInTeamFormGroupInput = { id: null }): PlayerInTeamFormGroup {
    const playerInTeamRawValue = {
      ...this.getFormDefaults(),
      ...playerInTeam,
    };
    return new FormGroup<PlayerInTeamFormGroupContent>({
      id: new FormControl(
        { value: playerInTeamRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      player: new FormControl(playerInTeamRawValue.player),
      teamInSeason: new FormControl(playerInTeamRawValue.teamInSeason),
    });
  }

  getPlayerInTeam(form: PlayerInTeamFormGroup): IPlayerInTeam | NewPlayerInTeam {
    return form.getRawValue() as IPlayerInTeam | NewPlayerInTeam;
  }

  resetForm(form: PlayerInTeamFormGroup, playerInTeam: PlayerInTeamFormGroupInput): void {
    const playerInTeamRawValue = { ...this.getFormDefaults(), ...playerInTeam };
    form.reset(
      {
        ...playerInTeamRawValue,
        id: { value: playerInTeamRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PlayerInTeamFormDefaults {
    return {
      id: null,
    };
  }
}
