import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../season-suit-nba.test-samples';

import { SeasonSuitNbaFormService } from './season-suit-nba-form.service';

describe('SeasonSuitNba Form Service', () => {
  let service: SeasonSuitNbaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SeasonSuitNbaFormService);
  });

  describe('Service methods', () => {
    describe('createSeasonSuitNbaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSeasonSuitNbaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            seasonName: expect.any(Object),
          }),
        );
      });

      it('passing ISeasonSuitNba should create a new form with FormGroup', () => {
        const formGroup = service.createSeasonSuitNbaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            seasonName: expect.any(Object),
          }),
        );
      });
    });

    describe('getSeasonSuitNba', () => {
      it('should return NewSeasonSuitNba for default SeasonSuitNba initial value', () => {
        const formGroup = service.createSeasonSuitNbaFormGroup(sampleWithNewData);

        const season = service.getSeasonSuitNba(formGroup) as any;

        expect(season).toMatchObject(sampleWithNewData);
      });

      it('should return NewSeasonSuitNba for empty SeasonSuitNba initial value', () => {
        const formGroup = service.createSeasonSuitNbaFormGroup();

        const season = service.getSeasonSuitNba(formGroup) as any;

        expect(season).toMatchObject({});
      });

      it('should return ISeasonSuitNba', () => {
        const formGroup = service.createSeasonSuitNbaFormGroup(sampleWithRequiredData);

        const season = service.getSeasonSuitNba(formGroup) as any;

        expect(season).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISeasonSuitNba should not enable id FormControl', () => {
        const formGroup = service.createSeasonSuitNbaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSeasonSuitNba should disable id FormControl', () => {
        const formGroup = service.createSeasonSuitNbaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
