import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPlayerSuitNba, NewPlayerSuitNba } from '../player-suit-nba.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlayerSuitNba for edit and NewPlayerSuitNbaFormGroupInput for create.
 */
type PlayerSuitNbaFormGroupInput = IPlayerSuitNba | PartialWithRequiredKeyOf<NewPlayerSuitNba>;

type PlayerSuitNbaFormDefaults = Pick<NewPlayerSuitNba, 'id'>;

type PlayerSuitNbaFormGroupContent = {
  id: FormControl<IPlayerSuitNba['id'] | NewPlayerSuitNba['id']>;
  name: FormControl<IPlayerSuitNba['name']>;
};

export type PlayerSuitNbaFormGroup = FormGroup<PlayerSuitNbaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlayerSuitNbaFormService {
  createPlayerSuitNbaFormGroup(player: PlayerSuitNbaFormGroupInput = { id: null }): PlayerSuitNbaFormGroup {
    const playerRawValue = {
      ...this.getFormDefaults(),
      ...player,
    };
    return new FormGroup<PlayerSuitNbaFormGroupContent>({
      id: new FormControl(
        { value: playerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(playerRawValue.name),
    });
  }

  getPlayerSuitNba(form: PlayerSuitNbaFormGroup): IPlayerSuitNba | NewPlayerSuitNba {
    return form.getRawValue() as IPlayerSuitNba | NewPlayerSuitNba;
  }

  resetForm(form: PlayerSuitNbaFormGroup, player: PlayerSuitNbaFormGroupInput): void {
    const playerRawValue = { ...this.getFormDefaults(), ...player };
    form.reset(
      {
        ...playerRawValue,
        id: { value: playerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PlayerSuitNbaFormDefaults {
    return {
      id: null,
    };
  }
}
