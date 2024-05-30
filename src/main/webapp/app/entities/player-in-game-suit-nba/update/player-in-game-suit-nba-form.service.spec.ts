import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../player-in-game-suit-nba.test-samples';

import { PlayerInGameSuitNbaFormService } from './player-in-game-suit-nba-form.service';

describe('PlayerInGameSuitNba Form Service', () => {
  let service: PlayerInGameSuitNbaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlayerInGameSuitNbaFormService);
  });

  describe('Service methods', () => {
    describe('createPlayerInGameSuitNbaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPlayerInGameSuitNbaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            points: expect.any(Object),
            rebounds: expect.any(Object),
            assists: expect.any(Object),
            steals: expect.any(Object),
            blocks: expect.any(Object),
            fouls: expect.any(Object),
            turnovers: expect.any(Object),
            played: expect.any(Object),
            team: expect.any(Object),
            player: expect.any(Object),
            game: expect.any(Object),
          }),
        );
      });

      it('passing IPlayerInGameSuitNba should create a new form with FormGroup', () => {
        const formGroup = service.createPlayerInGameSuitNbaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            points: expect.any(Object),
            rebounds: expect.any(Object),
            assists: expect.any(Object),
            steals: expect.any(Object),
            blocks: expect.any(Object),
            fouls: expect.any(Object),
            turnovers: expect.any(Object),
            played: expect.any(Object),
            team: expect.any(Object),
            player: expect.any(Object),
            game: expect.any(Object),
          }),
        );
      });
    });

    describe('getPlayerInGameSuitNba', () => {
      it('should return NewPlayerInGameSuitNba for default PlayerInGameSuitNba initial value', () => {
        const formGroup = service.createPlayerInGameSuitNbaFormGroup(sampleWithNewData);

        const playerInGame = service.getPlayerInGameSuitNba(formGroup) as any;

        expect(playerInGame).toMatchObject(sampleWithNewData);
      });

      it('should return NewPlayerInGameSuitNba for empty PlayerInGameSuitNba initial value', () => {
        const formGroup = service.createPlayerInGameSuitNbaFormGroup();

        const playerInGame = service.getPlayerInGameSuitNba(formGroup) as any;

        expect(playerInGame).toMatchObject({});
      });

      it('should return IPlayerInGameSuitNba', () => {
        const formGroup = service.createPlayerInGameSuitNbaFormGroup(sampleWithRequiredData);

        const playerInGame = service.getPlayerInGameSuitNba(formGroup) as any;

        expect(playerInGame).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPlayerInGameSuitNba should not enable id FormControl', () => {
        const formGroup = service.createPlayerInGameSuitNbaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPlayerInGameSuitNba should disable id FormControl', () => {
        const formGroup = service.createPlayerInGameSuitNbaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
