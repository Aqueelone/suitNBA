import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PlayerInTeamDetailComponent } from './player-in-team-detail.component';

describe('PlayerInTeam Management Detail Component', () => {
  let comp: PlayerInTeamDetailComponent;
  let fixture: ComponentFixture<PlayerInTeamDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerInTeamDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PlayerInTeamDetailComponent,
              resolve: { playerInTeam: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PlayerInTeamDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerInTeamDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load playerInTeam on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PlayerInTeamDetailComponent);

      // THEN
      expect(instance.playerInTeam()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
