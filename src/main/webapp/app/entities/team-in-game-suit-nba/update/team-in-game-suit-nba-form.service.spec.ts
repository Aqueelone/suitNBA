import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../team-in-game-suit-nba.test-samples';

import { TeamInGameSuitNbaFormService } from './team-in-game-suit-nba-form.service';

describe('TeamInGameSuitNba Form Service', () => {
  let service: TeamInGameSuitNbaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamInGameSuitNbaFormService);
  });

  describe('Service methods', () => {
    describe('createTeamInGameSuitNbaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeamInGameSuitNbaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            team: expect.any(Object),
            game: expect.any(Object),
          }),
        );
      });

      it('passing ITeamInGameSuitNba should create a new form with FormGroup', () => {
        const formGroup = service.createTeamInGameSuitNbaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            team: expect.any(Object),
            game: expect.any(Object),
          }),
        );
      });
    });

    describe('getTeamInGameSuitNba', () => {
      it('should return NewTeamInGameSuitNba for default TeamInGameSuitNba initial value', () => {
        const formGroup = service.createTeamInGameSuitNbaFormGroup(sampleWithNewData);

        const teamInGame = service.getTeamInGameSuitNba(formGroup) as any;

        expect(teamInGame).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeamInGameSuitNba for empty TeamInGameSuitNba initial value', () => {
        const formGroup = service.createTeamInGameSuitNbaFormGroup();

        const teamInGame = service.getTeamInGameSuitNba(formGroup) as any;

        expect(teamInGame).toMatchObject({});
      });

      it('should return ITeamInGameSuitNba', () => {
        const formGroup = service.createTeamInGameSuitNbaFormGroup(sampleWithRequiredData);

        const teamInGame = service.getTeamInGameSuitNba(formGroup) as any;

        expect(teamInGame).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeamInGameSuitNba should not enable id FormControl', () => {
        const formGroup = service.createTeamInGameSuitNbaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeamInGameSuitNba should disable id FormControl', () => {
        const formGroup = service.createTeamInGameSuitNbaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
