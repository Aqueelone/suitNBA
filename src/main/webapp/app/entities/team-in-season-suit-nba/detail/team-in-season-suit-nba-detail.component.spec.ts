import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TeamInSeasonSuitNbaDetailComponent } from './team-in-season-suit-nba-detail.component';

describe('TeamInSeasonSuitNba Management Detail Component', () => {
  let comp: TeamInSeasonSuitNbaDetailComponent;
  let fixture: ComponentFixture<TeamInSeasonSuitNbaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeamInSeasonSuitNbaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TeamInSeasonSuitNbaDetailComponent,
              resolve: { teamInSeason: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TeamInSeasonSuitNbaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamInSeasonSuitNbaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load teamInSeason on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TeamInSeasonSuitNbaDetailComponent);

      // THEN
      expect(instance.teamInSeason()).toEqual(expect.objectContaining({ id: 123 }));
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
