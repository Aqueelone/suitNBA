import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TeamInGameSuitNbaDetailComponent } from './team-in-game-suit-nba-detail.component';

describe('TeamInGameSuitNba Management Detail Component', () => {
  let comp: TeamInGameSuitNbaDetailComponent;
  let fixture: ComponentFixture<TeamInGameSuitNbaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeamInGameSuitNbaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TeamInGameSuitNbaDetailComponent,
              resolve: { teamInGame: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TeamInGameSuitNbaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamInGameSuitNbaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load teamInGame on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TeamInGameSuitNbaDetailComponent);

      // THEN
      expect(instance.teamInGame()).toEqual(expect.objectContaining({ id: 123 }));
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
