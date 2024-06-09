import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../player-in-team.test-samples';

import { PlayerInTeamFormService } from './player-in-team-form.service';

describe('PlayerInTeam Form Service', () => {
  let service: PlayerInTeamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlayerInTeamFormService);
  });

  describe('Service methods', () => {
    describe('createPlayerInTeamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPlayerInTeamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            player: expect.any(Object),
            teamInSeason: expect.any(Object),
          }),
        );
      });

      it('passing IPlayerInTeam should create a new form with FormGroup', () => {
        const formGroup = service.createPlayerInTeamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            player: expect.any(Object),
            teamInSeason: expect.any(Object),
          }),
        );
      });
    });

    describe('getPlayerInTeam', () => {
      it('should return NewPlayerInTeam for default PlayerInTeam initial value', () => {
        const formGroup = service.createPlayerInTeamFormGroup(sampleWithNewData);

        const playerInTeam = service.getPlayerInTeam(formGroup) as any;

        expect(playerInTeam).toMatchObject(sampleWithNewData);
      });

      it('should return NewPlayerInTeam for empty PlayerInTeam initial value', () => {
        const formGroup = service.createPlayerInTeamFormGroup();

        const playerInTeam = service.getPlayerInTeam(formGroup) as any;

        expect(playerInTeam).toMatchObject({});
      });

      it('should return IPlayerInTeam', () => {
        const formGroup = service.createPlayerInTeamFormGroup(sampleWithRequiredData);

        const playerInTeam = service.getPlayerInTeam(formGroup) as any;

        expect(playerInTeam).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPlayerInTeam should not enable id FormControl', () => {
        const formGroup = service.createPlayerInTeamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPlayerInTeam should disable id FormControl', () => {
        const formGroup = service.createPlayerInTeamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
