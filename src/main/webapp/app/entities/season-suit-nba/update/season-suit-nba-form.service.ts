import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISeasonSuitNba, NewSeasonSuitNba } from '../season-suit-nba.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISeasonSuitNba for edit and NewSeasonSuitNbaFormGroupInput for create.
 */
type SeasonSuitNbaFormGroupInput = ISeasonSuitNba | PartialWithRequiredKeyOf<NewSeasonSuitNba>;

type SeasonSuitNbaFormDefaults = Pick<NewSeasonSuitNba, 'id'>;

type SeasonSuitNbaFormGroupContent = {
  id: FormControl<ISeasonSuitNba['id'] | NewSeasonSuitNba['id']>;
  seasonName: FormControl<ISeasonSuitNba['seasonName']>;
};

export type SeasonSuitNbaFormGroup = FormGroup<SeasonSuitNbaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SeasonSuitNbaFormService {
  createSeasonSuitNbaFormGroup(season: SeasonSuitNbaFormGroupInput = { id: null }): SeasonSuitNbaFormGroup {
    const seasonRawValue = {
      ...this.getFormDefaults(),
      ...season,
    };
    return new FormGroup<SeasonSuitNbaFormGroupContent>({
      id: new FormControl(
        { value: seasonRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      seasonName: new FormControl(seasonRawValue.seasonName),
    });
  }

  getSeasonSuitNba(form: SeasonSuitNbaFormGroup): ISeasonSuitNba | NewSeasonSuitNba {
    return form.getRawValue() as ISeasonSuitNba | NewSeasonSuitNba;
  }

  resetForm(form: SeasonSuitNbaFormGroup, season: SeasonSuitNbaFormGroupInput): void {
    const seasonRawValue = { ...this.getFormDefaults(), ...season };
    form.reset(
      {
        ...seasonRawValue,
        id: { value: seasonRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SeasonSuitNbaFormDefaults {
    return {
      id: null,
    };
  }
}
