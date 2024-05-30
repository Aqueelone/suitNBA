import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IGameSuitNba, NewGameSuitNba } from '../game-suit-nba.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGameSuitNba for edit and NewGameSuitNbaFormGroupInput for create.
 */
type GameSuitNbaFormGroupInput = IGameSuitNba | PartialWithRequiredKeyOf<NewGameSuitNba>;

type GameSuitNbaFormDefaults = Pick<NewGameSuitNba, 'id'>;

type GameSuitNbaFormGroupContent = {
  id: FormControl<IGameSuitNba['id'] | NewGameSuitNba['id']>;
  season: FormControl<IGameSuitNba['season']>;
};

export type GameSuitNbaFormGroup = FormGroup<GameSuitNbaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GameSuitNbaFormService {
  createGameSuitNbaFormGroup(game: GameSuitNbaFormGroupInput = { id: null }): GameSuitNbaFormGroup {
    const gameRawValue = {
      ...this.getFormDefaults(),
      ...game,
    };
    return new FormGroup<GameSuitNbaFormGroupContent>({
      id: new FormControl(
        { value: gameRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      season: new FormControl(gameRawValue.season),
    });
  }

  getGameSuitNba(form: GameSuitNbaFormGroup): IGameSuitNba | NewGameSuitNba {
    return form.getRawValue() as IGameSuitNba | NewGameSuitNba;
  }

  resetForm(form: GameSuitNbaFormGroup, game: GameSuitNbaFormGroupInput): void {
    const gameRawValue = { ...this.getFormDefaults(), ...game };
    form.reset(
      {
        ...gameRawValue,
        id: { value: gameRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): GameSuitNbaFormDefaults {
    return {
      id: null,
    };
  }
}
