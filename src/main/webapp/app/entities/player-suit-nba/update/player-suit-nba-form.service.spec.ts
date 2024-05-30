import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../player-suit-nba.test-samples';

import { PlayerSuitNbaFormService } from './player-suit-nba-form.service';

describe('PlayerSuitNba Form Service', () => {
  let service: PlayerSuitNbaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlayerSuitNbaFormService);
  });

  describe('Service methods', () => {
    describe('createPlayerSuitNbaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPlayerSuitNbaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          }),
        );
      });

      it('passing IPlayerSuitNba should create a new form with FormGroup', () => {
        const formGroup = service.createPlayerSuitNbaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          }),
        );
      });
    });

    describe('getPlayerSuitNba', () => {
      it('should return NewPlayerSuitNba for default PlayerSuitNba initial value', () => {
        const formGroup = service.createPlayerSuitNbaFormGroup(sampleWithNewData);

        const player = service.getPlayerSuitNba(formGroup) as any;

        expect(player).toMatchObject(sampleWithNewData);
      });

      it('should return NewPlayerSuitNba for empty PlayerSuitNba initial value', () => {
        const formGroup = service.createPlayerSuitNbaFormGroup();

        const player = service.getPlayerSuitNba(formGroup) as any;

        expect(player).toMatchObject({});
      });

      it('should return IPlayerSuitNba', () => {
        const formGroup = service.createPlayerSuitNbaFormGroup(sampleWithRequiredData);

        const player = service.getPlayerSuitNba(formGroup) as any;

        expect(player).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPlayerSuitNba should not enable id FormControl', () => {
        const formGroup = service.createPlayerSuitNbaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPlayerSuitNba should disable id FormControl', () => {
        const formGroup = service.createPlayerSuitNbaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
