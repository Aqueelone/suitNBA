import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../team-suit-nba.test-samples';

import { TeamSuitNbaFormService } from './team-suit-nba-form.service';

describe('TeamSuitNba Form Service', () => {
  let service: TeamSuitNbaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamSuitNbaFormService);
  });

  describe('Service methods', () => {
    describe('createTeamSuitNbaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeamSuitNbaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            teamName: expect.any(Object),
          }),
        );
      });

      it('passing ITeamSuitNba should create a new form with FormGroup', () => {
        const formGroup = service.createTeamSuitNbaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            teamName: expect.any(Object),
          }),
        );
      });
    });

    describe('getTeamSuitNba', () => {
      it('should return NewTeamSuitNba for default TeamSuitNba initial value', () => {
        const formGroup = service.createTeamSuitNbaFormGroup(sampleWithNewData);

        const team = service.getTeamSuitNba(formGroup) as any;

        expect(team).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeamSuitNba for empty TeamSuitNba initial value', () => {
        const formGroup = service.createTeamSuitNbaFormGroup();

        const team = service.getTeamSuitNba(formGroup) as any;

        expect(team).toMatchObject({});
      });

      it('should return ITeamSuitNba', () => {
        const formGroup = service.createTeamSuitNbaFormGroup(sampleWithRequiredData);

        const team = service.getTeamSuitNba(formGroup) as any;

        expect(team).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeamSuitNba should not enable id FormControl', () => {
        const formGroup = service.createTeamSuitNbaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeamSuitNba should disable id FormControl', () => {
        const formGroup = service.createTeamSuitNbaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
