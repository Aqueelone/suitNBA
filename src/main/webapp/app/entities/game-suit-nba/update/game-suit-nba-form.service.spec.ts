import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../game-suit-nba.test-samples';

import { GameSuitNbaFormService } from './game-suit-nba-form.service';

describe('GameSuitNba Form Service', () => {
  let service: GameSuitNbaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameSuitNbaFormService);
  });

  describe('Service methods', () => {
    describe('createGameSuitNbaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGameSuitNbaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            season: expect.any(Object),
          }),
        );
      });

      it('passing IGameSuitNba should create a new form with FormGroup', () => {
        const formGroup = service.createGameSuitNbaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            season: expect.any(Object),
          }),
        );
      });
    });

    describe('getGameSuitNba', () => {
      it('should return NewGameSuitNba for default GameSuitNba initial value', () => {
        const formGroup = service.createGameSuitNbaFormGroup(sampleWithNewData);

        const game = service.getGameSuitNba(formGroup) as any;

        expect(game).toMatchObject(sampleWithNewData);
      });

      it('should return NewGameSuitNba for empty GameSuitNba initial value', () => {
        const formGroup = service.createGameSuitNbaFormGroup();

        const game = service.getGameSuitNba(formGroup) as any;

        expect(game).toMatchObject({});
      });

      it('should return IGameSuitNba', () => {
        const formGroup = service.createGameSuitNbaFormGroup(sampleWithRequiredData);

        const game = service.getGameSuitNba(formGroup) as any;

        expect(game).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGameSuitNba should not enable id FormControl', () => {
        const formGroup = service.createGameSuitNbaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGameSuitNba should disable id FormControl', () => {
        const formGroup = service.createGameSuitNbaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
