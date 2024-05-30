import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../team-in-season-suit-nba.test-samples';

import { TeamInSeasonSuitNbaFormService } from './team-in-season-suit-nba-form.service';

describe('TeamInSeasonSuitNba Form Service', () => {
  let service: TeamInSeasonSuitNbaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamInSeasonSuitNbaFormService);
  });

  describe('Service methods', () => {
    describe('createTeamInSeasonSuitNbaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeamInSeasonSuitNbaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            team: expect.any(Object),
            season: expect.any(Object),
          }),
        );
      });

      it('passing ITeamInSeasonSuitNba should create a new form with FormGroup', () => {
        const formGroup = service.createTeamInSeasonSuitNbaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            team: expect.any(Object),
            season: expect.any(Object),
          }),
        );
      });
    });

    describe('getTeamInSeasonSuitNba', () => {
      it('should return NewTeamInSeasonSuitNba for default TeamInSeasonSuitNba initial value', () => {
        const formGroup = service.createTeamInSeasonSuitNbaFormGroup(sampleWithNewData);

        const teamInSeason = service.getTeamInSeasonSuitNba(formGroup) as any;

        expect(teamInSeason).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeamInSeasonSuitNba for empty TeamInSeasonSuitNba initial value', () => {
        const formGroup = service.createTeamInSeasonSuitNbaFormGroup();

        const teamInSeason = service.getTeamInSeasonSuitNba(formGroup) as any;

        expect(teamInSeason).toMatchObject({});
      });

      it('should return ITeamInSeasonSuitNba', () => {
        const formGroup = service.createTeamInSeasonSuitNbaFormGroup(sampleWithRequiredData);

        const teamInSeason = service.getTeamInSeasonSuitNba(formGroup) as any;

        expect(teamInSeason).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeamInSeasonSuitNba should not enable id FormControl', () => {
        const formGroup = service.createTeamInSeasonSuitNbaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeamInSeasonSuitNba should disable id FormControl', () => {
        const formGroup = service.createTeamInSeasonSuitNbaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
