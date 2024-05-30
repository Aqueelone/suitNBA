import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPlayerInGameSuitNba, NewPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlayerInGameSuitNba for edit and NewPlayerInGameSuitNbaFormGroupInput for create.
 */
type PlayerInGameSuitNbaFormGroupInput = IPlayerInGameSuitNba | PartialWithRequiredKeyOf<NewPlayerInGameSuitNba>;

type PlayerInGameSuitNbaFormDefaults = Pick<NewPlayerInGameSuitNba, 'id'>;

type PlayerInGameSuitNbaFormGroupContent = {
  id: FormControl<IPlayerInGameSuitNba['id'] | NewPlayerInGameSuitNba['id']>;
  points: FormControl<IPlayerInGameSuitNba['points']>;
  rebounds: FormControl<IPlayerInGameSuitNba['rebounds']>;
  assists: FormControl<IPlayerInGameSuitNba['assists']>;
  steals: FormControl<IPlayerInGameSuitNba['steals']>;
  blocks: FormControl<IPlayerInGameSuitNba['blocks']>;
  fouls: FormControl<IPlayerInGameSuitNba['fouls']>;
  turnovers: FormControl<IPlayerInGameSuitNba['turnovers']>;
  played: FormControl<IPlayerInGameSuitNba['played']>;
  team: FormControl<IPlayerInGameSuitNba['team']>;
  player: FormControl<IPlayerInGameSuitNba['player']>;
  game: FormControl<IPlayerInGameSuitNba['game']>;
};

export type PlayerInGameSuitNbaFormGroup = FormGroup<PlayerInGameSuitNbaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlayerInGameSuitNbaFormService {
  createPlayerInGameSuitNbaFormGroup(playerInGame: PlayerInGameSuitNbaFormGroupInput = { id: null }): PlayerInGameSuitNbaFormGroup {
    const playerInGameRawValue = {
      ...this.getFormDefaults(),
      ...playerInGame,
    };
    return new FormGroup<PlayerInGameSuitNbaFormGroupContent>({
      id: new FormControl(
        { value: playerInGameRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      points: new FormControl(playerInGameRawValue.points),
      rebounds: new FormControl(playerInGameRawValue.rebounds),
      assists: new FormControl(playerInGameRawValue.assists),
      steals: new FormControl(playerInGameRawValue.steals),
      blocks: new FormControl(playerInGameRawValue.blocks),
      fouls: new FormControl(playerInGameRawValue.fouls),
      turnovers: new FormControl(playerInGameRawValue.turnovers),
      played: new FormControl(playerInGameRawValue.played),
      team: new FormControl(playerInGameRawValue.team),
      player: new FormControl(playerInGameRawValue.player),
      game: new FormControl(playerInGameRawValue.game),
    });
  }

  getPlayerInGameSuitNba(form: PlayerInGameSuitNbaFormGroup): IPlayerInGameSuitNba | NewPlayerInGameSuitNba {
    return form.getRawValue() as IPlayerInGameSuitNba | NewPlayerInGameSuitNba;
  }

  resetForm(form: PlayerInGameSuitNbaFormGroup, playerInGame: PlayerInGameSuitNbaFormGroupInput): void {
    const playerInGameRawValue = { ...this.getFormDefaults(), ...playerInGame };
    form.reset(
      {
        ...playerInGameRawValue,
        id: { value: playerInGameRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PlayerInGameSuitNbaFormDefaults {
    return {
      id: null,
    };
  }
}
